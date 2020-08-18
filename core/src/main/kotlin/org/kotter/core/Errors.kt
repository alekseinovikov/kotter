package org.kotter.core

import java.io.File

sealed class KotterException(message: String) : Exception(message)

class FileAccessException(file: File) : KotterException("Can't get access to file: $file")
class FileWriteException(ex: Exception): KotterException("Can't write record to file: $ex")
class SerializationException(exception: Exception): KotterException("Can't serialize record: $exception")
class DeserializationException(exception: Exception): KotterException("Can't deserialize record: $exception")
