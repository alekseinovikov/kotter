package org.kotter.file.engine.impl.serialization.impl

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kotter.core.DeserializationException
import org.kotter.core.Record
import org.kotter.core.SerializationException
import org.mockito.Mockito.*
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

internal class SerializerV1Test {

    private lateinit var serializer: SerializerV1

    @BeforeEach
    fun init() {
        serializer = SerializerV1()
    }


    @Test
    fun versionMustReturn1() {
        //act
        val result = serializer.version()

        //assert
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun serializeAndWriteToOutputStream() {
        val outputStream = ByteArrayOutputStream()
        val record1 = Record("1", 1L)
        val record2 = Record("2", 2L)
        val record3 = Record("3", 13L)

        serializer.serializeAndWriteToOutputStream(record1, outputStream)
        serializer.serializeAndWriteToOutputStream(record2, outputStream)
        serializer.serializeAndWriteToOutputStream(record3, outputStream)

        val writtenBytes = outputStream.toByteArray()

        val pathString = this.javaClass.classLoader.getResource("ser_test_v1")!!.path
        val expectedBytes = Files.readAllBytes(Path.of(pathString))

        assertThat(writtenBytes).isEqualTo(expectedBytes)
    }

    @Test
    fun serializeAndWriteToOutputStream_brokenOutputStream_exception() {
        class NoMethodShouldBeCalledAnswer : Answer<Any> {
            override fun answer(invocation: InvocationOnMock?): Any {
                throw RuntimeException()
            }
        }

        val outputStream = mock(OutputStream::class.java, NoMethodShouldBeCalledAnswer())

        val record = Record("1", 1L)
        assertThatThrownBy { serializer.serializeAndWriteToOutputStream(record, outputStream) }.isInstanceOf(
            SerializationException::class.java
        )
    }

    @Test
    fun readFromInputStreamAndDeserialize() {
        //arrange
        this.javaClass.classLoader.getResourceAsStream("ser_test_v1").use { input ->
            if (input == null) throw IllegalStateException("Can't open test file ser_test_v1")

            val record1 = serializer.readFromInputStreamAndDeserialize(input)
            val record2 = serializer.readFromInputStreamAndDeserialize(input)
            val record3 = serializer.readFromInputStreamAndDeserialize(input)

            assertThat(input.available()).isEqualTo(0)

            assertThat(record1).isEqualTo(Record("1", 1))
            assertThat(record2).isEqualTo(Record("2", 2))
            assertThat(record3).isEqualTo(Record("3", 13))
        }
    }

    @Test
    fun readFromInputStreamAndDeserialize_wrongFileFormat_exception() {
        //arrange
        this.javaClass.classLoader.getResourceAsStream("wrong_format").use { input ->
            if (input == null) throw IllegalStateException("Can't open test file ser_test_v1")

            assertThatThrownBy { serializer.readFromInputStreamAndDeserialize(input) }.isInstanceOf(
                DeserializationException::class.java
            )
        }
    }
}