package org.kotter.file.engine.impl.node

import org.kotter.core.FileSystemInitException
import org.kotter.file.engine.impl.serialization.Serializer
import java.io.File

class FileNodeManagerImpl(
    serializers: List<Serializer>,
    workingFolderPathString: String
) : FileNodeManager {

    private val filePrefix = "kotter"
    private val fileSuffix = ".data"
    private val fileVersionPrefix = "v"
    private val fileNameDelimiter = "-"

    private val serializerMap: Map<Int, Serializer> = serializers.map { it.version() to it }.toMap()
    private val latestSerializer: Serializer = serializers.sortedByDescending { it.version() }.first()
    private val folder = initWorkingFolder(workingFolderPathString)

    override fun recoverAllNodes(): List<FileNode> {
        val allFiles = getAllDirectoryFiles()
        val matchedFiles = filterMatchingFiles(allFiles)
        return matchedFiles
            .map { createFileNode(it) }
            .also { nodes ->
                nodes.forEach { verifyRecords(it) }
                verifyNoDuplicates(nodes)
            }
    }

    override fun createNewNode(): FileNode {
        TODO("Not yet implemented")
    }

    override fun createLatestPartitionNode(): FileNode {
        TODO("Not yet implemented")
    }

    override fun destroyNodes(nodes: List<FileNode>) {
        TODO("Not yet implemented")
    }

    private fun createFileNode(file: File): FileNode {
        val fileName = file.name
        val serializerVersion = getSerializerVersion(fileName)
        val partitionNumber = getPartitionNumber(fileName)
        val serializer = serializerMap[serializerVersion]
            ?: throw FileSystemInitException("Found unsupported file version: $serializerVersion")

        return FileNodeImpl(file, partitionNumber, serializer)
    }

    private fun initWorkingFolder(folderPathString: String): File {
        val folderFile = File(folderPathString)
        if (folderFile.exists().not()) {
            folderFile.mkdirs()
            return folderFile
        }

        if (folderFile.isDirectory.not()) {
            throw FileSystemInitException("$folderPathString is not a directory!")
        }

        return folderFile
    }

    private fun getAllDirectoryFiles(): List<File> = folder.listFiles()?.toList() ?: emptyList()
    private fun filterMatchingFiles(files: List<File>): List<File> = files.filter { it.name.matchLogFileName() }

    private fun String.matchLogFileName(): Boolean {
        val nameParts = this.trim().split(fileNameDelimiter)
        if (nameParts.size != 3 || nameParts[0] != filePrefix || nameParts[1].startsWith(fileVersionPrefix).not()) {
            return false
        }

        try {
            nameParts[1].removePrefix(fileVersionPrefix).toInt()
            nameParts[2].removeSuffix(fileSuffix).toInt()
        } catch (_: Exception) {
            return false
        }

        return true
    }

    private fun getSerializerVersion(fileName: String): Int {
        val nameParts = fileName.trim().split(fileNameDelimiter)
        return nameParts[1].removePrefix(fileVersionPrefix).toInt()
    }

    private fun getPartitionNumber(fileName: String): Int {
        val nameParts = fileName.trim().split(fileNameDelimiter)
        return nameParts[2].removeSuffix(fileSuffix).toInt()
    }

    private fun verifyRecords(fileNode: FileNode) {
        try {
            fileNode.readData().count()
        } catch (exception: Exception) {
            throw FileSystemInitException("Error on verification file: ${fileNode.file}", exception)
        }
    }

    private fun verifyNoDuplicates(fileNodes: List<FileNode>) {
        val uniqueFileNodeSet = mutableSetOf<FileNode>()
        fileNodes.forEach { node ->
            if (uniqueFileNodeSet.add(node).not()) {
                throw FileSystemInitException("Working directory has duplicate partition files: ${node.partitionNumber}")
            }
        }
    }

}