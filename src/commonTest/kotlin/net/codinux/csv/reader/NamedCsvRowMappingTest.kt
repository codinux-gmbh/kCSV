package net.codinux.csv.reader

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class NamedCsvRowMappingTest : FunSpec({

    CsvRowMappingTest.NullTestCases.mapValues { (_, value) -> if (value == ",") "" else value }.forEach { (name, fieldValue) ->

        test("stringOrNull_$name") {
            NamedCsvReader().read("${HeaderLine}$fieldValue").forEach { row ->
                row.getStringOrNull(Header).shouldBeNull()
            }
        }

        test("booleanOrNull_$name") {
            NamedCsvReader().read("${HeaderLine}$fieldValue").forEach { row ->
                row.getBooleanOrNull(Header).shouldBeNull()
            }
        }

        test("intOrNull_$name") {
            NamedCsvReader().read("${HeaderLine}$fieldValue").forEach { row ->
                row.getIntOrNull(Header).shouldBeNull()
            }
        }

        test("longOrNull_$name") {
            NamedCsvReader().read("${HeaderLine}$fieldValue").forEach { row ->
                row.getLongOrNull(Header).shouldBeNull()
            }
        }

        test("floatOrNull_$name") {
            NamedCsvReader().read("${HeaderLine}$fieldValue").forEach { row ->
                row.getFloatOrNull(Header).shouldBeNull()
            }
        }

        test("doubleOrNull_$name") {
            NamedCsvReader().read("${HeaderLine}$fieldValue").forEach { row ->
                row.getDoubleOrNull(Header).shouldBeNull()
            }
        }

    }

    (1..100).forEach { index ->

        test("[$index] Random Int test") {
            val expected = Random.nextInt()

            NamedCsvReader().read("$HeaderLine$expected").forEach { row ->
                row.getInt(Header).shouldBe(expected)
            }
        }

        test("[$index] Random Long test") {
            val expected = Random.nextLong()

            NamedCsvReader().read("$HeaderLine$expected").forEach { row ->
                row.getLong(Header).shouldBe(expected)
            }
        }

        test("[$index] Random Float test") {
            val expected = Random.nextFloat()

            NamedCsvReader().read("$HeaderLine$expected").forEach { row ->
                row.getFloat(Header).shouldBe(expected)
            }
        }

        test("[$index] Random Double test") {
            val expected = Random.nextDouble()

            NamedCsvReader().read("$HeaderLine$expected").forEach { row ->
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
        NamedCsvReader().read("${HeaderLine}string").forEach { row ->
            row.getString(Header).shouldBe("string")
        }
    }


    @Test
    fun boolean_TrueLowercase() {
        NamedCsvReader().read("${HeaderLine}true").forEach { row ->
            row.getBoolean(Header).shouldBe(true)
        }
    }

    @Test
    fun boolean_TrueUppercase() {
        NamedCsvReader().read("${HeaderLine}TRUE").forEach { row ->
            row.getBoolean(Header).shouldBe(true)
        }
    }

    @Test
    fun boolean_TrueMixedCase() {
        NamedCsvReader().read("${HeaderLine}tRuE").forEach { row ->
            row.getBoolean(Header).shouldBe(true)
        }
    }

    @Test
    fun boolean_FalseLowercase() {
        NamedCsvReader().read("${HeaderLine}false").forEach { row ->
            row.getBoolean(Header).shouldBe(false)
        }
    }

    @Test
    fun boolean_FalseUppercase() {
        NamedCsvReader().read("${HeaderLine}FALSE").forEach { row ->
            row.getBoolean(Header).shouldBe(false)
        }
    }

    @Test
    fun boolean_FalseMixedCase() {
        NamedCsvReader().read("${HeaderLine}fAlSe").forEach { row ->
            row.getBoolean(Header).shouldBe(false)
        }
    }

    @Test
    fun boolean_OtherString() {
        NamedCsvReader().read("${HeaderLine}Truthy").forEach { row ->
            shouldThrowAny {
                row.getBoolean(Header)
            }
        }
    }


    @Test
    fun int_Max() {
        NamedCsvReader().read("$HeaderLine${Int.MAX_VALUE}").forEach { row ->
            row.getInt(Header).shouldBe(Int.MAX_VALUE)
        }
    }

    @Test
    fun int_MaxPlus1() {
        NamedCsvReader().read("$HeaderLine${Int.MAX_VALUE.toLong() + 1}").forEach { row ->
            shouldThrowAny {
                row.getInt(Header)
            }
        }
    }

    @Test
    fun int_Min() {
        NamedCsvReader().read("$HeaderLine${Int.MIN_VALUE}").forEach { row ->
            row.getInt(Header).shouldBe(Int.MIN_VALUE)
        }
    }

    @Test
    fun int_MinMinus1() {
        NamedCsvReader().read("$HeaderLine${Int.MIN_VALUE.toLong() - 1}").forEach { row ->
            shouldThrowAny {
                row.getInt(Header)
            }
        }
    }


    @Test
    fun long_Max() {
        NamedCsvReader().read("$HeaderLine${Long.MAX_VALUE}").forEach { row ->
            row.getLong(Header).shouldBe(Long.MAX_VALUE)
        }
    }

    @Test
    fun long_MaxPlus1() {
        NamedCsvReader().read("${HeaderLine}9223372036854775808").forEach { row ->
            shouldThrowAny {
                row.getLong(Header)
            }
        }
    }

    @Test
    fun long_Min() {
        NamedCsvReader().read("$HeaderLine${Long.MIN_VALUE}").forEach { row ->
            row.getLong(Header).shouldBe(Long.MIN_VALUE)
        }
    }

    @Test
    fun long_MinMinus1() {
        NamedCsvReader().read("${HeaderLine}-9223372036854775809").forEach { row ->
            shouldThrowAny {
                row.getLong(Header)
            }
        }
    }


    @Test
    fun float_Max() {
        NamedCsvReader().read("$HeaderLine${Float.MAX_VALUE}").forEach { row ->
            row.getFloat(Header).shouldBe(Float.MAX_VALUE)
        }
    }

    @Test
    fun float_Min() {
        NamedCsvReader().read("$HeaderLine${Float.MIN_VALUE}").forEach { row ->
            row.getFloat(Header).shouldBe(Float.MIN_VALUE)
        }
    }


    @Test
    fun double_Max() {
        NamedCsvReader().read("$HeaderLine${Double.MAX_VALUE}").forEach { row ->
            row.getDouble(Header).shouldBe(Double.MAX_VALUE)
        }
    }

    @Test
    fun double_Min() {
        NamedCsvReader().read("$HeaderLine${Double.MIN_VALUE}").forEach { row ->
            row.getDouble(Header).shouldBe(Double.MIN_VALUE)
        }
    }

}