package org.kotter.file.engine.impl.serialization.impl

import org.kotter.core.DeserializationException
import org.kotter.core.Record
import org.kotter.core.SerializationException
import org.kotter.file.engine.impl.serialization.Serializer
import org.kotter.file.engine.proto.FileRecordProtoV1
import java.io.InputStream
import java.io.OutputStream

class SerializerV1 : Serializer {

    override fun version() = 1

    override fun serializeAndWriteToOutputStream(record: Record, outputStream: OutputStream) {
        try {
            record.toProto().writeDelimitedTo(outputStream)
        } catch (ex: Exception) {
            throw SerializationException(ex)
        }
    }

    override fun readFromInputStreamAndDeserialize(inputStream: InputStream): Record {
        try {
            return FileRecordProtoV1.parseDelimitedFrom(inputStream).toRecord()
        } catch (ex: Exception) {
            throw DeserializationException(ex)
        }
    }

    private fun Record.toProto(): FileRecordProtoV1 = FileRecordProtoV1.newBuilder()
        .setKey(this.key)
        .setValue(this.value)
        .build()

    private fun FileRecordProtoV1.toRecord() = Record(this.key, this.value)

}