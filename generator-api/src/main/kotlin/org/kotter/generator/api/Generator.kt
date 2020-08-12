package org.kotter.generator.api

interface Generator<T : SeqValue> {
    fun generate(name: SeqName): T
    fun createSequence(name: SeqName)
}