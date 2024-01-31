package net.codinux.csv.reader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class CsvRowTest {

    /*      throwIfColumnDoesNotExist       */

    @Test
    fun getStringOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getStringOrNull("any") }
    }

    @Test
    fun getStringOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getStringOrNull("any", false)

        assertNull(value)
    }


    @Test
    fun getBooleanOrNull_ByColumnIndex_ValueIsNotABoolean() {
        val row = row("1")

        val value = row.getBooleanOrNull(0)

        assertNull(value)
    }

    @Test
    fun getBooleanOrNull_ByColumnName_ValueIsNotABoolean() {
        val row = row("Boolean", "1")

        val value = row.getBooleanOrNull("Boolean")

        assertNull(value)
    }

    @Test
    fun getBooleanOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getBooleanOrNull("any") }
    }

    @Test
    fun getBooleanOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getBooleanOrNull("any", false)

        assertNull(value)
    }


    @Test
    fun getIntOrNull_ByColumnIndex_ValueIsNotAnInteger() {
        val row = row("2.0")

        val value = row.getIntOrNull(0)

        assertNull(value)
    }

    @Test
    fun getIntOrNull_ByColumnName_ValueIsNotAnInteger() {
        val row = row("Int", "2.0")

        val value = row.getIntOrNull("Int")

        assertNull(value)
    }

    @Test
    fun getIntOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getIntOrNull("any") }
    }

    @Test
    fun getIntOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getIntOrNull("any", false)

        assertNull(value)
    }


    @Test
    fun getLongOrNull_ByColumnIndex_ValueIsNotALong() {
        val row = row("2.0")

        val value = row.getLongOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLongOrNull_ByColumnName_ValueIsNotALong() {
        val row = row("Long", "2.0")

        val value = row.getLongOrNull("Long")

        assertNull(value)
    }

    @Test
    fun getLongOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getLongOrNull("any") }
    }

    @Test
    fun getLongOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getLongOrNull("any", false)

        assertNull(value)
    }


    @Test
    fun getFloatOrNull_ByColumnIndex_ValueIsNotAFloat() {
        val row = row("true")

        val value = row.getFloatOrNull(0)

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_ValueIsNotAFloat() {
        val row = row("Float", "true")

        val value = row.getFloatOrNull("Float")

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getFloatOrNull("any") }
    }

    @Test
    fun getFloatOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getFloatOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnIndex_SpecifyDecimalSeparator_ValueIsNotAFloat() {
        val row = row("2;0")

        val value = row.getFloatOrNull(0, ',')

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyDecimalSeparator_ValueIsNotAFloat() {
        val row = row("Float", "2;0")

        val value = row.getFloatOrNull("Float", ',')

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyDecimalSeparator_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getFloatOrNull("any", ',') }
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyDecimalSeparator_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getFloatOrNull("any", ',', false)

        assertNull(value)
    }


    @Test
    fun getDoubleOrNull_ByColumnIndex_ValueIsNotADouble() {
        val row = row("true")

        val value = row.getDoubleOrNull(0)

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_ValueIsNotADouble() {
        val row = row("Double", "true")

        val value = row.getDoubleOrNull("Double")

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getDoubleOrNull("any") }
    }

    @Test
    fun getDoubleOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getDoubleOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnIndex_SpecifyDecimalSeparator_ValueIsNotADouble() {
        val row = row("2;0")

        val value = row.getDoubleOrNull(0, ',')

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyDecimalSeparator_ValueIsNotADouble() {
        val row = row("Double", "2;0")

        val value = row.getDoubleOrNull("Double", ',')

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyDecimalSeparator_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getDoubleOrNull("any", ',') }
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyDecimalSeparator_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getDoubleOrNull("any", ',', false)

        assertNull(value)
    }


    /*      Specify Decimal Separator       */

    @Test
    fun getFloat_ByColumnIndex_SpecifyDecimalSeparator() {
        val row = row("3,14")

        val value = row.getFloat(0, ',')

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloat_ByColumnIndex_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("")

        assertFails { row.getFloat(0, ',') }
    }

    @Test
    fun getFloat_ByColumnName_SpecifyDecimalSeparator() {
        val row = row("Float", "3,14")

        val value = row.getFloat("Float", ',')

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloat_ByColumnName_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("Float", "")

        assertFails { row.getFloat("Float", ',') }
    }

    @Test
    fun getDouble_ByColumnIndex_SpecifyDecimalSeparator() {
        val row = row("3,14")

        val value = row.getDouble(0, ',')

        assertEquals(value, 3.14)
    }

    @Test
    fun getDouble_ByColumnIndex_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("")

        assertFails { row.getDouble(0, ',') }
    }

    @Test
    fun getDouble_ByColumnName_SpecifyDecimalSeparator() {
        val row = row("Double", "3,14")

        val value = row.getDouble("Double", ',')

        assertEquals(value, 3.14)
    }

    @Test
    fun getDouble_ByColumnName_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("Double", "")

        assertFails { row.getDouble("Double", ',') }
    }

    @Test
    fun getFloatOrNull_ByColumnIndex_SpecifyDecimalSeparator() {
        val row = row("3,14")

        val value = row.getFloatOrNull(0, ',')

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloatOrNull_ByColumnIndex_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("")

        val value = row.getFloatOrNull(0, ',')

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyDecimalSeparator() {
        val row = row("Float", "3,14")

        val value = row.getFloatOrNull("Float", ',')

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("Float", "")

        val value = row.getFloatOrNull("Float", ',')

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnIndex_SpecifyDecimalSeparator() {
        val row = row("3,14")

        val value = row.getDoubleOrNull(0, ',')

        assertEquals(value, 3.14)
    }

    @Test
    fun getDoubleOrNull_ByColumnIndex_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("")

        val value = row.getDoubleOrNull(0, ',')

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyDecimalSeparator() {
        val row = row("Double", "3,14")

        val value = row.getDoubleOrNull("Double", ',')

        assertEquals(value, 3.14)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("Double", "")

        val value = row.getDoubleOrNull("Double", ',')

        assertNull(value)
    }


    private fun emptyRow() = row(emptySet(), emptyArray())

    private fun row(value: String) = row(emptySet(), arrayOf(value))

    private fun row(header: String, value: String) = row(setOf(header), arrayOf(value))

    private fun row(headers: Set<String> = emptySet(), values: Array<String>) = CsvRow(
        headers, values, 1, false, false
    )
}