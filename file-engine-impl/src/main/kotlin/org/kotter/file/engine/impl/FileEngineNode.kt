package org.kotter.file.engine.impl

import org.kotter.file.engine.proto.FileRecordProto

interface FileEngineNode {
    fun addData(record: FileRecordProto)
    fun readData(): Sequence<FileRecordProto>
    fun flush()
}