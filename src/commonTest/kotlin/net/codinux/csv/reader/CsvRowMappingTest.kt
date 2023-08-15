package net.codinux.csv.reader

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CsvRowMappingTest : FunSpec({

    NullTestCases.forEach { (name, fieldValue) ->

        test("stringOrNull_$name") {
            CsvReader(fieldValue).forEach { row ->
                row.getStringOrNull(0).shouldBeNull()
            }
        }

        test("booleanOrNull_$name") {
            CsvReader(fieldValue).forEach { row ->
                row.getBooleanOrNull(0).shouldBeNull()
            }
        }

        test("intOrNull_$name") {
            CsvReader(fieldValue).forEach { row ->
                row.getIntOrNull(0).shouldBeNull()
            }
        }

        test("longOrNull_$name") {
            CsvReader(fieldValue).forEach { row ->
                row.getLongOrNull(0).shouldBeNull()
            }
        }

        test("floatOrNull_$name") {
            CsvReader(fieldValue).forEach { row ->
                row.getFloatOrNull(0).shouldBeNull()
            }
        }

        test("doubleOrNull_$name") {
            CsvReader(fieldValue).forEach { row ->
                row.getDoubleOrNull(0).shouldBeNull()
            }
        }

    }

}) {

    companion object {
        val NullTestCases = mapOf(
            "EmptyString" to ",",
            // https://en.wikipedia.org/wiki/Whitespace_character
            "BlankString" to " \u00A0 \t \u000C \u1680 \u2000",
            "NullLowercase" to "null",
            "NullUppercase" to "NULL",
            "NullMixedCase" to "nUlL"
        )
    }

    @Test
    fun string() {
        CsvReader("string").forEach { row ->
            row.getString(0).shouldBe("string")
        }
    }

}