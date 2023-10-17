package net.codinux.csv.reader

import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertNull

class CsvRowTest {

    @Test
    fun getStringOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = row(emptySet(), emptyArray())

        assertFails { row.getStringOrNull("any") }
    }

    @Test
    fun getStringOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = row(emptySet(), emptyArray())

        val value = row.getStringOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getBooleanOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = row(emptySet(), emptyArray())

        assertFails { row.getBooleanOrNull("any") }
    }

    @Test
    fun getBooleanOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = row(emptySet(), emptyArray())

        val value = row.getBooleanOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getIntOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = row(emptySet(), emptyArray())

        assertFails { row.getIntOrNull("any") }
    }

    @Test
    fun getIntOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = row(emptySet(), emptyArray())

        val value = row.getIntOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getLongOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = row(emptySet(), emptyArray())

        assertFails { row.getLongOrNull("any") }
    }

    @Test
    fun getLongOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = row(emptySet(), emptyArray())

        val value = row.getLongOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = row(emptySet(), emptyArray())

        assertFails { row.getFloatOrNull("any") }
    }

    @Test
    fun getFloatOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = row(emptySet(), emptyArray())

        val value = row.getFloatOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = row(emptySet(), emptyArray())

        assertFails { row.getDoubleOrNull("any") }
    }

    @Test
    fun getDoubleOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = row(emptySet(), emptyArray())

        val value = row.getDoubleOrNull("any", false)

        assertNull(value)
    }

    private fun row(headers: Set<String> = emptySet(), values: Array<String>) = CsvRow(
        headers, values, 1, false, false
    )
}