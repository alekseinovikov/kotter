package org.kotter.file.engine.impl

import org.kotter.file.engine.proto.FileRecordProto

interface FileEngineNode {
    fun addData(record: FileRecordProto)
    fun readData(): List<FileRecordProto>
    fun flush()
}