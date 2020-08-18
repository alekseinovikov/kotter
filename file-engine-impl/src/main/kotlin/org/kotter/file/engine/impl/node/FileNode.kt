package org.kotter.file.engine.impl.node

import org.kotter.file.engine.proto.FileRecordProto

interface FileNode {
    fun addData(record: FileRecordProto)
    fun readData(): Sequence<FileRecordProto>
    fun flush()
    fun partitionNumber(): Int
}