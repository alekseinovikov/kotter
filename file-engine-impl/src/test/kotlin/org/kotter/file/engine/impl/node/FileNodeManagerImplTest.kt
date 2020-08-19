package org.kotter.file.engine.impl.node

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.kotter.core.FileSystemInitException
import org.kotter.file.engine.impl.serialization.impl.SerializerV1

internal class FileNodeManagerImplTest {

    @Test
    fun recoverAllNodesGreenPath() {
        val folderUri = this.javaClass.classLoader.getResource("correct")!!
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.path)

        val nodes = fileNodeManager.recoverAllNodes()

        assertThat(nodes).hasSize(3)
    }

    @Test
    fun recoverAllNodesOneFileWrongPrefixReturnsEmpty() {
        val folderUri = this.javaClass.classLoader.getResource("wrong-prefix-dir")!!
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.path)

        val nodes = fileNodeManager.recoverAllNodes()

        assertThat(nodes).hasSize(0)
    }

    @Test
    fun recoverAllNodesOneFileWrongSerializationVersionException() {
        val folderUri = this.javaClass.classLoader.getResource("wrong-ser-version")!!
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.path)

        assertThatThrownBy { fileNodeManager.recoverAllNodes() }.isInstanceOf(FileSystemInitException::class.java)
    }

    @Test
    fun recoverAllNodesOneFileWrongFormatException() {
        val folderUri = this.javaClass.classLoader.getResource("wrong-format-dir")!!
        val fileNodeManager = FileNodeManagerImpl(listOf(SerializerV1()), folderUri.path)

        assertThatThrownBy { fileNodeManager.recoverAllNodes() }.isInstanceOf(FileSystemInitException::class.java)
    }

}