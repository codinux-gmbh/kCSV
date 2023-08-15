package net.codinux.csv.reader

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NamedCsvRowMappingTest : FunSpec({

    CsvRowMappingTest.NullTestCases.mapValues { (_, value) -> if (value == ",") "" else value }.forEach { (name, fieldValue) ->

        test("stringOrNull_$name") {
            NamedCsvReader("${HeaderLine}$fieldValue").forEach { row ->
                row.getStringOrNull(Header).shouldBeNull()
            }
        }

        test("booleanOrNull_$name") {
            NamedCsvReader("${HeaderLine}$fieldValue").forEach { row ->
                row.getBooleanOrNull(Header).shouldBeNull()
            }
        }

        test("intOrNull_$name") {
            NamedCsvReader("${HeaderLine}$fieldValue").forEach { row ->
                row.getIntOrNull(Header).shouldBeNull()
            }
        }

        test("longOrNull_$name") {
            NamedCsvReader("${HeaderLine}$fieldValue").forEach { row ->
                row.getLongOrNull(Header).shouldBeNull()
            }
        }

        test("floatOrNull_$name") {
            NamedCsvReader("${HeaderLine}$fieldValue").forEach { row ->
                row.getFloatOrNull(Header).shouldBeNull()
            }
        }

        test("doubleOrNull_$name") {
            NamedCsvReader("${HeaderLine}$fieldValue").forEach { row ->
                row.getDoubleOrNull(Header).shouldBeNull()
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
        NamedCsvReader("${HeaderLine}string").forEach { row ->
            row.getString(Header).shouldBe("string")
        }
    }

}