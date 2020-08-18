package org.kotter.file.engine.impl.node

import org.kotter.core.FileAccessException
import org.kotter.core.Record
import org.kotter.core.log
import org.kotter.file.engine.impl.serialization.Serializer
import java.io.File
import java.io.OutputStream

internal class FileNodeImpl(
    private val file: File,
    private val partitionNumber: Int,
    private val serializer: Serializer
) : FileNode, AutoCloseable {

    private val outputStream: OutputStream

    init {
        checkFile()
        outputStream = file.outputStream()
    }


    override fun addData(record: Record) {
        synchronized(file) {
            serializer.serializeAndWriteToOutputStream(record, outputStream)
        }
    }

    override fun readData(): Sequence<Record> = synchronized(file) {
        sequence {
            file.inputStream().use { inputStream ->
                while (inputStream.available() > 0) {
                    val parsed = serializer.readFromInputStreamAndDeserialize(inputStream)
                    yield(parsed)
                }
            }
        }
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

    override fun partitionNumber(): Int = this.partitionNumber

    private fun checkFile() {
        if (file.exists().not()) file.createNewFile()
        if (file.canWrite().not()) throw FileAccessException(file)
    }

}