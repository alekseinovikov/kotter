package org.kotter.file.engine.impl

import org.kotter.core.FileAccessException
import org.kotter.core.FileRecord
import org.kotter.core.FileWriteException
import org.kotter.file.engine.api.FileEngine
import org.kotter.file.engine.proto.FileRecordProto
import java.io.*
import java.nio.file.Path

class FileEngineImpl(filePath: Path) : FileEngine {

    private val file: File = openOrCreateFile(filePath)
    private val inputStream: InputStream = file.inputStream()
    private val outputStream: OutputStream = file.outputStream()

    override fun addData(record: FileRecord) {
        synchronized(file) {
            try {
                val proto = convert(record)
                proto.writeDelimitedTo(outputStream)
            } catch (ex: Exception) {
                throw FileWriteException(ex)
            }
        }
    }

    override fun readData(): List<FileRecord> {
        synchronized(file) {
            inputStream.reset()
        }

        return emptyList()
    }

    private fun convert(record: FileRecord) = FileRecordProto.newBuilder()
        .setKey(record.key)
        .setValue(record.value)
        .build()

    private fun convert(record: FileRecordProto) = FileRecord(record.key, record.value)

    private fun openOrCreateFile(path: Path): File {
        val file = File(path.toUri())
        if (!file.exists()) {
            file.createNewFile()
        }

        if (file.canWrite().not()) throw FileAccessException(file)
        return file
    }

}