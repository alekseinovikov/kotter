package org.kotter.file.engine.impl

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.kotter.core.Record
import org.kotter.file.engine.impl.node.FileNodeImpl
import org.kotter.file.engine.impl.serialization.Serializer
import org.kotter.file.engine.impl.serialization.impl.SerializerV1
import java.io.File
import java.util.*
import kotlin.random.Random

internal class FileNodeImplTest {

    lateinit var node: FileNodeImpl
    lateinit var file: File

    @BeforeEach
    fun setUp() {
        val fileName = UUID.randomUUID().toString()
        val filePath = "$tempDirPath/$fileName"
        val serializer: Serializer = SerializerV1()

        file = File(filePath)
        node = FileNodeImpl(file, 1, serializer)
    }

    @AfterEach
    fun clean() {
        node.close()
        file.delete()
    }

    @Test
    fun testWritingAndReadingCase1() {
        //arrange
        val record = Record("1", 1)

        //act
        node.addData(record)
        node.flush()
        val records = node.readData().toList()

        //assert
        assertThat(records).hasSize(1)
        val result = records[0]

        assertThat(result.key).isEqualTo("1")
        assertThat(result.value).isEqualTo(1)
    }

    @Test
    fun testWritingAndReadingCase2() {
        //arrange
        val record1 = Record("1", 1)
        val record2 = Record("2", 2)

        //act
        node.addData(record1)
        node.flush()
        val records = node.readData().toList()

        assertThat(records).hasSize(1)
        val result = records[0]

        assertThat(result.key).isEqualTo("1")
        assertThat(result.value).isEqualTo(1)

        node.addData(record2)
        node.flush()

        val records2 = node.readData().toList()

        assertThat(records2).hasSize(2)
        val result2 = records2[0]

        assertThat(result2.key).isEqualTo("1")
        assertThat(result2.value).isEqualTo(1)

        val result3 = records2[1]
        assertThat(result3.key).isEqualTo("2")
        assertThat(result3.value).isEqualTo(2)
    }

    @Test
    fun testWritingAndReadingCase3() {
        val random = Random.Default
        generateRandomRecords(random, 100_000)

        val readData = node.readData()

        assertThat(readData.count()).isEqualTo(100_000)
    }

    @Test
    fun testConcurrentWriting(): Unit = runBlocking {
        val random = Random.Default

        val job1 = launch {
            generateRandomRecords(random, 100_000)
        }

        val job2 = launch {
            generateRandomRecords(random, 100_000)
        }

        val job3 = launch {
            generateRandomRecords(random, 100_000)
        }

        job1.join()
        job2.join()
        job3.join()

        val data = node.readData()

        assertThat(data.count()).isEqualTo(300_000)

        Unit
    }

    private fun generateRandomRecords(random: Random.Default, count: Int) {
        repeat(count) {
            Record(UUID.randomUUID().toString(), random.nextLong()).also { node.addData(it) }
        }

        node.flush()
    }

}