package org.kotter.file.engine.impl.serialization

import org.kotter.core.Record
import java.io.InputStream
import java.io.OutputStream

interface Serializer {
    fun serializeAndWriteToOutputStream(record: Record, outputStream: OutputStream)
    fun readFromInputStreamAndDeserialize(inputStream: InputStream): Record
    fun version():Int
}