package org.kotter.file.engine.impl

import org.kotter.core.FileAccessException
import org.kotter.core.log
import org.kotter.file.engine.proto.FileRecordProto
import java.io.File
import java.io.InputStream
import java.io.OutputStream

internal class FileEngineNodeImpl(private val file: File) : FileEngineNode, AutoCloseable {

    private val outputStream: OutputStream

    init {
        checkFile()
        outputStream = file.outputStream()
    }


    override fun addData(record: FileRecordProto) {
        synchronized(file) {
            record.writeDelimitedTo(outputStream)
        }
    }

    override fun readData(): List<FileRecordProto> = synchronized(file) {
        val result = ArrayList<FileRecordProto>()

        synchronized(file) {
            file.inputStream().use { inputStream ->
                while (inputStream.available() > 0) {
                    val parsed = FileRecordProto.parseDelimitedFrom(inputStream)
                    result.add(parsed)
                }
            }
        }

        return result
    }

    override fun flush() {
        synchronized(file) {
            outputStream.flush()
        }
    }

    override fun close() {
        try {
            outputStream.close()
        } catch (ex: Exception) {
            log.error("Exception on close file!", ex)
            //ignore
        }
    }

    private fun checkFile() {
        if (file.exists().not()) file.createNewFile()
        if (file.canWrite().not()) throw FileAccessException(file)
    }

}