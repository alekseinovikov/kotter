package org.kotter.file.engine.impl.node

import org.kotter.core.Record
import java.io.File

interface FileNode {
    fun addData(record: Record)
    fun readData(): Sequence<Record>
    fun flush()
    fun partitionNumber(): Int
    val file: File
    val partitionNumber: Int
}