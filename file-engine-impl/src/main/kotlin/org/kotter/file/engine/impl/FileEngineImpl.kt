package org.kotter.file.engine.impl

import org.kotter.core.Record
import org.kotter.file.engine.api.FileEngine
import java.nio.file.Path

class FileEngineImpl(filePath: Path) : FileEngine {

    override fun addData(record: Record) {
    }

    override fun readData(): List<Record> {
        return emptyList()
    }


}