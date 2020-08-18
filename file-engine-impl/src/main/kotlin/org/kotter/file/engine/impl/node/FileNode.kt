package org.kotter.file.engine.impl.node

import org.kotter.core.Record

interface FileNode {
    fun addData(record: Record)
    fun readData(): Sequence<Record>
    fun flush()
    fun partitionNumber(): Int
}