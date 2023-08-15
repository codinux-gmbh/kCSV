package net.codinux.csv.reader

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NamedCsvRowMappingTest {

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

    @Test
    fun stringOrNull_EmptyString() {
        NamedCsvReader(HeaderLine).forEach { row ->
            row.getStringOrNull(Header).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_BlankString() {
        NamedCsvReader("$HeaderLine \u00A0 \t \u000C \u1680 \u2000").forEach { row ->
            row.getStringOrNull(Header).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_NullLowercase() {
        NamedCsvReader("${HeaderLine}null").forEach { row ->
            row.getStringOrNull(Header).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_NullUppercase() {
        NamedCsvReader("${HeaderLine}NULL").forEach { row ->
            row.getStringOrNull(Header).shouldBeNull()
        }
    }

    @Test
    fun stringOrNull_NullMixedCase() {
        NamedCsvReader("${HeaderLine}nUlL").forEach { row ->
            row.getStringOrNull(Header).shouldBeNull()
        }
    }

}