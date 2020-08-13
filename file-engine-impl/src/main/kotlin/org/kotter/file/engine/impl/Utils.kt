package org.kotter.file.engine.impl

import java.nio.file.Path

val tempDirPath: Path by lazy {
    val tempDirPathString = System.getProperty("java.io.tmpdir")
    Path.of(tempDirPathString)
}