package org.kotter.core

typealias FileRecordKey = String
typealias FileRecordValue = Long

data class FileRecord(val key: FileRecordKey, val value: FileRecordValue)