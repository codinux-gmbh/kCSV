package net.codinux.csv.reader

import org.junit.jupiter.api.Test

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class CsvRowJvmTest {

    companion object {
        private val GermanNumberFormat = DecimalFormat.getNumberInstance(Locale.GERMANY)
    }


    /*      Specify Decimal Separator       */

    @Test
    fun getBigDecimal_ByColumnIndex_SpecifyDecimalSeparator() {
        val row = row("3,14")

        val value = row.getBigDecimal(0, ',')

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimal_ByColumnIndex_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("")

        assertFails { row.getBigDecimal(0, ',') }
    }

    @Test
    fun getBigDecimal_ByColumnName_SpecifyDecimalSeparator() {
        val row = row("Double", "3,14")

        val value = row.getBigDecimal("Double", ',')

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimal_ByColumnName_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("Double", "")

        assertFails { row.getBigDecimal("Double", ',') }
    }

    @Test
    fun getBigDecimalOrNull_ByColumnIndex_SpecifyDecimalSeparator() {
        val row = row("3,14")

        val value = row.getBigDecimalOrNull(0, ',')

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimalOrNull_ByColumnIndex_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("")

        val value = row.getBigDecimalOrNull(0, ',')

        assertNull(value)
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyDecimalSeparator() {
        val row = row("Double", "3,14")

        val value = row.getBigDecimalOrNull("Double", ',')

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyDecimalSeparator_ValueNotSet() {
        val row = row("Double", "")

        val value = row.getBigDecimalOrNull("Double", ',')

        assertNull(value)
    }


    /*      Specify NumberFormat       */

    @Test
    fun getFloat_ByColumnIndex_SpecifyNumberFormat() {
        val row = row("3,14")

        val value = row.getFloat(0, GermanNumberFormat)

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloat_ByColumnIndex_SpecifyNumberFormat_ValueNotSet() {
        val row = row("")

        assertFails { row.getFloat(0, GermanNumberFormat) }
    }

    @Test
    fun getFloat_ByColumnName_SpecifyNumberFormat() {
        val row = row("Float", "3,14")

        val value = row.getFloat("Float", GermanNumberFormat)

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloat_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("Float", "")

        assertFails { row.getFloat("Float", GermanNumberFormat) }
    }

    @Test
    fun getDouble_ByColumnIndex_SpecifyNumberFormat() {
        val row = row("3,14")

        val value = row.getDouble(0, GermanNumberFormat)

        assertEquals(value, 3.14)
    }

    @Test
    fun getDouble_ByColumnIndex_SpecifyNumberFormat_ValueNotSet() {
        val row = row("")

        assertFails { row.getDouble(0, GermanNumberFormat) }
    }

    @Test
    fun getDouble_ByColumnName_SpecifyNumberFormat() {
        val row = row("Double", "3,14")

        val value = row.getDouble("Double", GermanNumberFormat)

        assertEquals(value, 3.14)
    }

    @Test
    fun getDouble_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("Double", "")

        assertFails { row.getDouble("Double", GermanNumberFormat) }
    }

    @Test
    fun getFloatOrNull_ByColumnIndex_SpecifyNumberFormat() {
        val row = row("3,14")

        val value = row.getFloatOrNull(0, GermanNumberFormat)

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloatOrNull_ByColumnIndex_SpecifyNumberFormat_ValueNotSet() {
        val row = row("")

        val value = row.getFloatOrNull(0, GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyNumberFormat() {
        val row = row("Float", "3,14")

        val value = row.getFloatOrNull("Float", GermanNumberFormat)

        assertEquals(value, 3.14f)
    }

    @Test
    fun getFloatOrNull_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("Float", "")

        val value = row.getFloatOrNull("Float", GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnIndex_SpecifyNumberFormat() {
        val row = row("3,14")

        val value = row.getDoubleOrNull(0, GermanNumberFormat)

        assertEquals(value, 3.14)
    }

    @Test
    fun getDoubleOrNull_ByColumnIndex_SpecifyNumberFormat_ValueNotSet() {
        val row = row("")

        val value = row.getDoubleOrNull(0, GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyNumberFormat() {
        val row = row("Double", "3,14")

        val value = row.getDoubleOrNull("Double", GermanNumberFormat)

        assertEquals(value, 3.14)
    }

    @Test
    fun getDoubleOrNull_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("Double", "")

        val value = row.getDoubleOrNull("Double", GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getBigDecimal_ByColumnIndex_SpecifyNumberFormat() {
        val row = row("3,14")

        val value = row.getBigDecimal(0, GermanNumberFormat)

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimal_ByColumnIndex_SpecifyNumberFormat_ValueNotSet() {
        val row = row("")

        assertFails { row.getBigDecimal(0, GermanNumberFormat) }
    }

    @Test
    fun getBigDecimal_ByColumnName_SpecifyNumberFormat() {
        val row = row("Double", "3,14")

        val value = row.getBigDecimal("Double", GermanNumberFormat)

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimal_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("Double", "")

        assertFails { row.getBigDecimal("Double", GermanNumberFormat) }
    }

    @Test
    fun getBigDecimalOrNull_ByColumnIndex_SpecifyNumberFormat() {
        val row = row("3,14")

        val value = row.getBigDecimalOrNull(0, GermanNumberFormat)

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimalOrNull_ByColumnIndex_SpecifyNumberFormat_ValueNotSet() {
        val row = row("")

        val value = row.getBigDecimalOrNull(0, GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyNumberFormat() {
        val row = row("Double", "3,14")

        val value = row.getBigDecimalOrNull("Double", GermanNumberFormat)

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("Double", "")

        val value = row.getBigDecimalOrNull("Double", GermanNumberFormat)

        assertNull(value)
    }


    private fun emptyRow() = row(emptySet(), emptyArray())

    private fun row(value: String) = row(emptySet(), arrayOf(value))

    private fun row(header: String, value: String) = row(setOf(header), arrayOf(value))

    private fun row(headers: Set<String> = emptySet(), values: Array<String>) = CsvRow(
        headers, values, 1, false, false
    )
}