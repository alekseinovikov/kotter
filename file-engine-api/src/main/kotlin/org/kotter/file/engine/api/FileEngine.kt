package org.kotter.file.engine.api

import org.kotter.core.Record

interface FileEngine {
    fun addData(record: Record)
    fun readData(): List<Record>
}
