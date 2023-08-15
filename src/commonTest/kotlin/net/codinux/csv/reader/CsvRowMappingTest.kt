package net.codinux.csv.reader

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CsvRowMappingTest {

    @Test
    fun string() {
        CsvReader("string").forEach { row ->
            row.getString(0).shouldBe("string")
        }
    }

    @Test
    fun stringOrNull_EmptyString() {
        CsvReader(",").forEach { row ->
            row.getStringOrNull(0).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_BlankString() {
        CsvReader(" \u00A0 \t \u000C \u1680 \u2000").forEach { row ->
            row.getStringOrNull(0).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_NullLowercase() {
        CsvReader("null").forEach { row ->
            row.getStringOrNull(0).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_NullUppercase() {
        CsvReader("NULL").forEach { row ->
            row.getStringOrNull(0).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_NullMixedCase() {
        CsvReader("nUlL").forEach { row ->
            row.getStringOrNull(0).shouldBeNull()
        }
    }

}