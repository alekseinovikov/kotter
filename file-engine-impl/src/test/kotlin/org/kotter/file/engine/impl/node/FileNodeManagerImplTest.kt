package org.kotter.file.engine.impl.node

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kotter.core.FileSystemInitException
import org.kotter.core.Record
import org.kotter.file.engine.impl.serialization.Serializer
import org.kotter.file.engine.impl.serialization.impl.SerializerV1
import org.kotter.file.engine.impl.tempDirPath
import java.io.File
import java.util.*

internal class FileNodeManagerImplTest {

    private lateinit var tempDir: File

    @BeforeEach
    fun setUp() {
        this.tempDir = createRandomTempDir()
    }

    @AfterEach
    fun clean() {
        deleteFolder(this.tempDir)
    }

    @Test
    fun recoverAllNodesGreenPath() {
        val folderUri =
            createFilesAndFillCorrectRecords(listOf("kotter-v1-11.data", "kotter-v1-12.data", "kotter-v1-13.data"))
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.absolutePath)

        val nodes = fileNodeManager.recoverAllNodes()

        assertThat(nodes).hasSize(3)
    }

    @Test
    fun recoverAllNodesOneFileWrongPrefixReturnsEmpty() {
        val folderUri = createFilesAndFillCorrectRecords(listOf("lorem-v1-21.data"))
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.absolutePath)

        val nodes = fileNodeManager.recoverAllNodes()

        assertThat(nodes).hasSize(0)
    }

    @Test
    fun recoverAllNodesOneFileWrongSerializationVersionException() {
        val folderUri = createFilesAndFillCorrectRecords(listOf("kotter-v9999-1.data"))
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.path)

        assertThatThrownBy { fileNodeManager.recoverAllNodes() }.isInstanceOf(FileSystemInitException::class.java)
    }

    @Test
    fun recoverAllNodesOneFileWrongFormatException() {
        val folderUri = createFilesAndFillWithPassedText(listOf("kotter-v1-1.data"), "wrong data")
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.path)

        assertThatThrownBy { fileNodeManager.recoverAllNodes() }.isInstanceOf(FileSystemInitException::class.java)
    }

    private fun createFilesAndFillWithPassedText(fileNames: List<String>, content: String): File {
        fileNames.forEach { createFileAndWriteContent(tempDir.absolutePath, it, content.toByteArray()) }

        return tempDir
    }

    private fun createFilesAndFillCorrectRecords(fileNames: List<String>): File {
        val record1 = Record("1", 1L)
        val record2 = Record("2", 2L)
        val record3 = Record("3", 3L)

        fileNames.forEach {
            createFileAndWriteRecords(
                tempDir.absolutePath,
                it,
                listOf(record1, record2, record3),
                SerializerV1()
            )
        }

        return tempDir
    }

    private fun createFileAndWriteContent(dirName: String, fileName: String, content: ByteArray): File {
        return createFile(dirName, fileName).also {
            it.outputStream().use { fileOutputStream ->
                fileOutputStream.write(content)
                fileOutputStream.flush()
            }
        }
    }

    private fun createFileAndWriteRecords(
        dirName: String,
        fileName: String,
        records: List<Record>,
        serializer: Serializer
    ): File {
        return createFile(dirName, fileName).also {
            it.outputStream().use { fileOutputStream ->
                records.forEach { record -> serializer.serializeAndWriteToOutputStream(record, fileOutputStream) }
            }
        }
    }

    private fun createFile(dirName: String, fileName: String): File {
        val dirFile = File(dirName).also { it.mkdirs() }

        val file = File("${dirFile.absolutePath}/$fileName")
        file.createNewFile()
        return file
    }

    private fun createRandomTempDir(): File {
        val dirName = "$tempDirPath/${UUID.randomUUID()}"
        return File(dirName).also { it.mkdirs() }
    }

    private fun deleteFolder(folder: File) {
        folder.listFiles()?.forEach {
            if (it.isDirectory) deleteFolder(it) else it.delete()
        }

        folder.delete()
    }

}