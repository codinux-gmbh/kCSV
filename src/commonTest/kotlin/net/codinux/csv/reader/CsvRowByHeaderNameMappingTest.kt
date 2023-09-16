package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class CsvRowByHeaderNameMappingTest : FunSpec({

    CsvRowMappingTest.NullTestCases.mapValues { (_, value) -> if (value == ",") "" else value }.forEach { (name, fieldValue) ->

        test("stringOrNull_$name") {
            CsvReader(hasHeaderRow = true).read("${HeaderLine}$fieldValue").forEach { row ->
                row.getStringOrNull(Header).shouldBeNull()
            }
        }

        test("booleanOrNull_$name") {
            CsvReader(hasHeaderRow = true).read("${HeaderLine}$fieldValue").forEach { row ->
                row.getBooleanOrNull(Header).shouldBeNull()
            }
        }

        test("intOrNull_$name") {
            CsvReader(hasHeaderRow = true).read("${HeaderLine}$fieldValue").forEach { row ->
                row.getIntOrNull(Header).shouldBeNull()
            }
        }

        test("longOrNull_$name") {
            CsvReader(hasHeaderRow = true).read("${HeaderLine}$fieldValue").forEach { row ->
                row.getLongOrNull(Header).shouldBeNull()
            }
        }

        test("floatOrNull_$name") {
            CsvReader(hasHeaderRow = true).read("${HeaderLine}$fieldValue").forEach { row ->
                row.getFloatOrNull(Header).shouldBeNull()
            }
        }

        test("doubleOrNull_$name") {
            CsvReader(hasHeaderRow = true).read("${HeaderLine}$fieldValue").forEach { row ->
                row.getDoubleOrNull(Header).shouldBeNull()
            }
        }

    }

    (1..100).forEach { index ->

        test("[$index] Random Int test") {
            val expected = Random.nextInt()

            CsvReader(hasHeaderRow = true).read("$HeaderLine$expected").forEach { row ->
                row.getInt(Header).shouldBe(expected)
            }
        }

        test("[$index] Random Long test") {
            val expected = Random.nextLong()

            CsvReader(hasHeaderRow = true).read("$HeaderLine$expected").forEach { row ->
                row.getLong(Header).shouldBe(expected)
            }
        }

        test("[$index] Random Float test") {
            val expected = Random.nextFloat()

            CsvReader(hasHeaderRow = true).read("$HeaderLine$expected").forEach { row ->
                row.getFloat(Header).shouldBe(expected)
            }
        }

        test("[$index] Random Double test") {
            val expected = Random.nextDouble()

            CsvReader(hasHeaderRow = true).read("$HeaderLine$expected").forEach { row ->
                row.getDouble(Header).shouldBe(expected)
            }
        }

    }

})  {

    companion object {
        private const val Header = "header"
        private const val HeaderLine = "$Header\n"
    }

    @Test
    fun string() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}string").forEach { row ->
            row.getString(Header).shouldBe("string")
        }
    }


    @Test
    fun boolean_TrueLowercase() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}true").forEach { row ->
            row.getBoolean(Header).shouldBe(true)
        }
    }

    @Test
    fun boolean_TrueUppercase() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}TRUE").forEach { row ->
            row.getBoolean(Header).shouldBe(true)
        }
    }

    @Test
    fun boolean_TrueMixedCase() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}tRuE").forEach { row ->
            row.getBoolean(Header).shouldBe(true)
        }
    }

    @Test
    fun boolean_FalseLowercase() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}false").forEach { row ->
            row.getBoolean(Header).shouldBe(false)
        }
    }

    @Test
    fun boolean_FalseUppercase() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}FALSE").forEach { row ->
            row.getBoolean(Header).shouldBe(false)
        }
    }

    @Test
    fun boolean_FalseMixedCase() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}fAlSe").forEach { row ->
            row.getBoolean(Header).shouldBe(false)
        }
    }

    @Test
    fun boolean_OtherString() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}Truthy").forEach { row ->
            shouldThrowAny {
                row.getBoolean(Header)
            }
        }
    }


    @Test
    fun int_Max() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Int.MAX_VALUE}").forEach { row ->
            row.getInt(Header).shouldBe(Int.MAX_VALUE)
        }
    }

    @Test
    fun int_MaxPlus1() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Int.MAX_VALUE.toLong() + 1}").forEach { row ->
            shouldThrowAny {
                row.getInt(Header)
            }
        }
    }

    @Test
    fun int_Min() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Int.MIN_VALUE}").forEach { row ->
            row.getInt(Header).shouldBe(Int.MIN_VALUE)
        }
    }

    @Test
    fun int_MinMinus1() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Int.MIN_VALUE.toLong() - 1}").forEach { row ->
            shouldThrowAny {
                row.getInt(Header)
            }
        }
    }


    @Test
    fun long_Max() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Long.MAX_VALUE}").forEach { row ->
            row.getLong(Header).shouldBe(Long.MAX_VALUE)
        }
    }

    @Test
    fun long_MaxPlus1() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}9223372036854775808").forEach { row ->
            shouldThrowAny {
                row.getLong(Header)
            }
        }
    }

    @Test
    fun long_Min() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Long.MIN_VALUE}").forEach { row ->
            row.getLong(Header).shouldBe(Long.MIN_VALUE)
        }
    }

    @Test
    fun long_MinMinus1() {
        CsvReader(hasHeaderRow = true).read("${HeaderLine}-9223372036854775809").forEach { row ->
            shouldThrowAny {
                row.getLong(Header)
            }
        }
    }


    @Test
    fun float_Max() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Float.MAX_VALUE}").forEach { row ->
            row.getFloat(Header).shouldBe(Float.MAX_VALUE)
        }
    }

    @Test
    fun float_Min() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Float.MIN_VALUE}").forEach { row ->
            row.getFloat(Header).shouldBe(Float.MIN_VALUE)
        }
    }


    @Test
    fun double_Max() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Double.MAX_VALUE}").forEach { row ->
            row.getDouble(Header).shouldBe(Double.MAX_VALUE)
        }
    }

    @Test
    fun double_Min() {
        CsvReader(hasHeaderRow = true).read("$HeaderLine${Double.MIN_VALUE}").forEach { row ->
            row.getDouble(Header).shouldBe(Double.MIN_VALUE)
        }
    }

}