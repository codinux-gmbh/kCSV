package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.random.Random
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

    (1..100).forEach { index ->

        test("[$index] Random Int test") {
            val expected = Random.nextInt()

            CsvReader(expected.toString()).forEach { row ->
                row.getInt(0).shouldBe(expected)
            }
        }

        test("[$index] Random Long test") {
            val expected = Random.nextLong()

            CsvReader(expected.toString()).forEach { row ->
                row.getLong(0).shouldBe(expected)
            }
        }

        test("[$index] Random Float test") {
            val expected = Random.nextFloat()

            CsvReader(expected.toString()).forEach { row ->
                row.getFloat(0).shouldBe(expected)
            }
        }

        test("[$index] Random Double test") {
            val expected = Random.nextDouble()

            CsvReader(expected.toString()).forEach { row ->
                row.getDouble(0).shouldBe(expected)
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


    @Test
    fun boolean_TrueLowercase() {
        CsvReader("true").forEach { row ->
            row.getBoolean(0).shouldBe(true)
        }
    }

    @Test
    fun boolean_TrueUppercase() {
        CsvReader("TRUE").forEach { row ->
            row.getBoolean(0).shouldBe(true)
        }
    }

    @Test
    fun boolean_TrueMixedCase() {
        CsvReader("tRuE").forEach { row ->
            row.getBoolean(0).shouldBe(true)
        }
    }

    @Test
    fun boolean_FalseLowercase() {
        CsvReader("false").forEach { row ->
            row.getBoolean(0).shouldBe(false)
        }
    }

    @Test
    fun boolean_FalseUppercase() {
        CsvReader("FALSE").forEach { row ->
            row.getBoolean(0).shouldBe(false)
        }
    }

    @Test
    fun boolean_FalseMixedCase() {
        CsvReader("fAlSe").forEach { row ->
            row.getBoolean(0).shouldBe(false)
        }
    }

    @Test
    fun boolean_OtherString() {
        CsvReader("Truthy").forEach { row ->
            shouldThrowAny {
                row.getBoolean(0)
            }
        }
    }


    @Test
    fun int_Max() {
        CsvReader(Int.MAX_VALUE.toString()).forEach { row ->
            row.getInt(0).shouldBe(Int.MAX_VALUE)
        }
    }

    @Test
    fun int_MaxPlus1() {
        CsvReader((Int.MAX_VALUE.toLong() + 1).toString()).forEach { row ->
            shouldThrowAny {
                row.getInt(0)
            }
        }
    }

    @Test
    fun int_Min() {
        CsvReader(Int.MIN_VALUE.toString()).forEach { row ->
            row.getInt(0).shouldBe(Int.MIN_VALUE)
        }
    }

    @Test
    fun int_MinMinus1() {
        CsvReader((Int.MIN_VALUE.toLong() - 1).toString()).forEach { row ->
            shouldThrowAny {
                row.getInt(0)
            }
        }
    }


    @Test
    fun long_Max() {
        CsvReader(Long.MAX_VALUE.toString()).forEach { row ->
            row.getLong(0).shouldBe(Long.MAX_VALUE)
        }
    }

    @Test
    fun long_MaxPlus1() {
        CsvReader("9223372036854775808").forEach { row ->
            shouldThrowAny {
                row.getLong(0)
            }
        }
    }

    @Test
    fun long_Min() {
        CsvReader(Long.MIN_VALUE.toString()).forEach { row ->
            row.getLong(0).shouldBe(Long.MIN_VALUE)
        }
    }

    @Test
    fun long_MinMinus1() {
        CsvReader("-9223372036854775809").forEach { row ->
            shouldThrowAny {
                row.getLong(0)
            }
        }
    }


    @Test
    fun float_Max() {
        CsvReader(Float.MAX_VALUE.toString()).forEach { row ->
            row.getFloat(0).shouldBe(Float.MAX_VALUE)
        }
    }

    @Test
    fun float_Min() {
        CsvReader(Float.MIN_VALUE.toString()).forEach { row ->
            row.getFloat(0).shouldBe(Float.MIN_VALUE)
        }
    }


    @Test
    fun double_Max() {
        CsvReader(Double.MAX_VALUE.toString()).forEach { row ->
            row.getDouble(0).shouldBe(Double.MAX_VALUE)
        }
    }

    @Test
    fun double_Min() {
        CsvReader(Double.MIN_VALUE.toString()).forEach { row ->
            row.getDouble(0).shouldBe(Double.MIN_VALUE)
        }
    }

}