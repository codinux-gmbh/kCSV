package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class ImmutableSetTest {

    @Test
    fun isImmutable() {
        val underTest = ImmutableSet("one", "two")

        shouldThrowAny {
            underTest as MutableSet<String>
        }
    }

    @Test
    fun toMutableList() {
        val underTest = ImmutableSet("one", "two")

        val result = underTest.toMutableSet()
        result.add("three")

        result.shouldHaveSize(3)
        underTest.shouldHaveSize(2)
    }

}