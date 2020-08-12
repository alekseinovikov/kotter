package org.kotter.file.engine.api

import org.kotter.core.FileRecord

interface FileEngine {
    fun addData(record: FileRecord)
    fun readData(): List<FileRecord>
}
