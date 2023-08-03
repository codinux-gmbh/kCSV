package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.maps.shouldHaveSize
import kotlin.test.Test

class ImmutableMapTest {

    @Test
    fun isImmutable() {
        val underTest = ImmutableMap("one" to "two")

        shouldThrowAny {
            underTest as MutableMap<String, String>
        }
    }

    @Test
    fun toMutableMap() {
        val underTest = ImmutableMap("one" to "two")

        val result = underTest.toMutableMap()
        result.put("three", "four")

        result.shouldHaveSize(2)
        underTest.shouldHaveSize(1)
    }

}