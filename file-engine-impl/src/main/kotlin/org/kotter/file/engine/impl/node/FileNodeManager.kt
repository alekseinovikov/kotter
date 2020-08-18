package org.kotter.file.engine.impl.node

interface FileNodeManager {
    fun recoverAllNodes(): List<FileNode>
    fun createNewNode(): FileNode
    fun createLatestPartitionNode(): FileNode
    fun destroyNodes(nodes: List<FileNode>)
}