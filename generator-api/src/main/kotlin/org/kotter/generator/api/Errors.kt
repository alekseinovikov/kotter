package org.kotter.generator.api

sealed class GeneratorException(val message: String)

class UnexpectedException: GeneratorException("Unexpected exception!")
class SequenceNotFoundException: GeneratorException("Sequence not found!")
class SequenceAlreadyExistsException: GeneratorException("Sequence already exists!")