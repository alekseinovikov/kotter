package org.kotter.file.engine.api

import java.nio.file.Path

interface FileEngineFactory {
    fun create(filePath: Path): FileEngine
}