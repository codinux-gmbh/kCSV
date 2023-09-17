package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class ImmutableListTest {

    @Test
    fun isImmutable() {
        val underTest = ImmutableList(arrayOf("one", "two"))

        shouldThrowAny {
            underTest as MutableList<String>
        }
    }

    @Test
    fun toMutableList() {
        val underTest = ImmutableList(arrayOf("one", "two"))

        val result = underTest.toMutableList()
        result.add("three")

        result.shouldHaveSize(3)
        underTest.shouldHaveSize(2)
    }

}