package net.codinux.csv.reader

import org.junit.jupiter.api.Test

import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class CsvRowJvmTest {

    companion object {
        private val GermanNumberFormat = DecimalFormat.getNumberInstance(Locale.GERMANY)

        private const val InstantString = "2023-10-11T16:23:48.123456Z"

        private const val LocalDateString = "2023-11-28"
        private const val LocalDateStringForFormatter = "28.11.23"
        private val LocalDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")

        private const val LocalTimeString = "04:12:56.378034"
        private const val LocalTimeStringForFormatter = "7:26 PM"
        private val LocalTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

        private const val LocalDateTimeString = "2023-10-11T16:23:48.123456"
        private const val LocalDateTimeStringForFormatter = "10/16/2023 7:26 PM"
        private val LocalDateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a")
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
    fun getBigDecimalOrNull_ByColumnIndex_SpecifyDecimalSeparator_ValueIsNotABigDecimal() {
        val row = row("2;0")

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
    fun getFloatOrNull_ByColumnIndex_SpecifyNumberFormat_ValueIsNotAFloat() {
        val row = row("true")

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
    fun getFloatOrNull_ByColumnName_SpecifyNumberFormat_ValueIsNotAFloat() {
        val row = row("Float", "true")

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
    fun getDoubleOrNull_ByColumnIndex_SpecifyNumberFormat_ValueIsNotADouble() {
        val row = row("true")

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
    fun getDoubleOrNull_ByColumnName_SpecifyNumberFormat_ValueIsNotADouble() {
        val row = row("Double", "true")

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
        val row = row("BigDecimal", "3,14")

        val value = row.getBigDecimal("BigDecimal", GermanNumberFormat)

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimal_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("BigDecimal", "")

        assertFails { row.getBigDecimal("BigDecimal", GermanNumberFormat) }
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
    fun getBigDecimalOrNull_ByColumnIndex_SpecifyNumberFormat_ValueIsNotABigDecimal() {
        val row = row("true")

        val value = row.getBigDecimalOrNull(0, GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyNumberFormat() {
        val row = row("BigDecimal", "3,14")

        val value = row.getBigDecimalOrNull("BigDecimal", GermanNumberFormat)

        assertEquals(value, BigDecimal("3.14"))
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyNumberFormat_ValueNotSet() {
        val row = row("BigDecimal", "")

        val value = row.getBigDecimalOrNull("BigDecimal", GermanNumberFormat)

        assertNull(value)
    }

    @Test
    fun getBigDecimalOrNull_ByColumnName_SpecifyNumberFormat_ValueIsNotABigDecimal() {
        val row = row("BigDecimal", "true")

        val value = row.getBigDecimalOrNull("BigDecimal", GermanNumberFormat)

        assertNull(value)
    }


    /*      Instant, Date and Time      */

    @Test
    fun getInstant_ByColumnIndex() {
        val row = row(InstantString)

        val value = row.getInstant(0)

        assertEquals(value, Instant.parse(InstantString))
    }

    @Test
    fun getInstant_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getInstant(0) }
    }

    @Test
    fun getInstant_ByColumnName() {
        val row = row("Instant", InstantString)

        val value = row.getInstant("Instant")

        assertEquals(value, Instant.parse(InstantString))
    }

    @Test
    fun getInstant_ByColumnName_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getInstant("any") }
    }

    @Test
    fun getInstantOrNull_ByColumnIndex() {
        val row = row(InstantString)

        val value = row.getInstantOrNull(0)

        assertEquals(value, Instant.parse(InstantString))
    }

    @Test
    fun getInstantOrNull_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getInstantOrNull(0) }
    }

    @Test
    fun getInstantOrNull_ByColumnIndex_ValueIsNotAnInstant() {
        val row = row("true")

        val value = row.getInstantOrNull(0)

        assertNull(value)
    }

    @Test
    fun getInstantOrNull_ByColumnName() {
        val row = row("Instant", InstantString)

        val value = row.getInstantOrNull("Instant")

        assertEquals(value, Instant.parse(InstantString))
    }

    @Test
    fun getInstantOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getInstantOrNull("any") }
    }

    @Test
    fun getInstantOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getInstantOrNull("any", false)

        assertNull(value)
    }

    @Test
    fun getInstantOrNull_ByColumnName_ValueIsNotAnInstant() {
        val row = row("Instant", "true")

        val value = row.getInstantOrNull("Instant")

        assertNull(value)
    }


    @Test
    fun getLocalDate_ByColumnIndex() {
        val row = row(LocalDateString)

        val value = row.getLocalDate(0)

        assertEquals(value, LocalDate.parse(LocalDateString))
    }

    @Test
    fun getLocalDate_ByColumnIndex_SpecifyDateTimeFormatter() {
        val row = row(LocalDateStringForFormatter)

        val value = row.getLocalDate(0, LocalDateFormatter)

        assertEquals(value, LocalDate.parse(LocalDateStringForFormatter, LocalDateFormatter))
    }

    @Test
    fun getLocalDate_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalDate(0) }
    }

    @Test
    fun getLocalDate_ByColumnName() {
        val row = row("LocalDate", LocalDateString)

        val value = row.getLocalDate("LocalDate")

        assertEquals(value, LocalDate.parse(LocalDateString))
    }

    @Test
    fun getLocalDate_ByColumnName_SpecifyDateTimeFormatter() {
        val row = row("LocalDate", LocalDateStringForFormatter)

        val value = row.getLocalDate("LocalDate", LocalDateFormatter)

        assertEquals(value, LocalDate.parse(LocalDateStringForFormatter, LocalDateFormatter))
    }

    @Test
    fun getLocalDate_ByColumnName_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalDate("any") }
    }

    @Test
    fun getLocalDateOrNull_ByColumnIndex() {
        val row = row(LocalDateString)

        val value = row.getLocalDateOrNull(0)

        assertEquals(value, LocalDate.parse(LocalDateString))
    }

    @Test
    fun getLocalDateOrNull_ByColumnIndex_EmptyValue() {
        val row = row("")

        val value = row.getLocalDateOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLocalDateOrNull_ByColumnIndex_ValueIsNotALocalDate() {
        val row = row("2")

        val value = row.getLocalDateOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLocalDateOrNull_ByColumnIndex_SpecifyDateTimeFormatter() {
        val row = row(LocalDateStringForFormatter)

        val value = row.getLocalDateOrNull(0, LocalDateFormatter)

        assertEquals(value, LocalDate.parse(LocalDateStringForFormatter, LocalDateFormatter))
    }

    @Test
    fun getLocalDateOrNull_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalDateOrNull(0) }
    }

    @Test
    fun getLocalDateOrNull_ByColumnName() {
        val row = row("LocalDate", LocalDateString)

        val value = row.getLocalDateOrNull("LocalDate")

        assertEquals(value, LocalDate.parse(LocalDateString))
    }

    @Test
    fun getLocalDateOrNull_ByColumnName_EmptyValue() {
        val row = row("LocalDate", "")

        val value = row.getLocalDateOrNull("LocalDate")

        assertNull(value)
    }

    @Test
    fun getLocalDateOrNull_ByColumnName_ValueIsNotALocalDate() {
        val row = row("LocalDate", "2")

        val value = row.getLocalDateOrNull("LocalDate")

        assertNull(value)
    }

    @Test
    fun getLocalDateOrNull_ByColumnName_SpecifyDateTimeFormatter() {
        val row = row("LocalDate", LocalDateStringForFormatter)

        val value = row.getLocalDateOrNull("LocalDate", LocalDateFormatter)

        assertEquals(value, LocalDate.parse(LocalDateStringForFormatter, LocalDateFormatter))
    }

    @Test
    fun getLocalDateOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getLocalDateOrNull("any") }
    }

    @Test
    fun getLocalDateOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getLocalDateOrNull("any", false)

        assertNull(value)
    }


    @Test
    fun getLocalTime_ByColumnIndex() {
        val row = row(LocalTimeString)

        val value = row.getLocalTime(0)

        assertEquals(value, LocalTime.parse(LocalTimeString))
    }

    @Test
    fun getLocalTime_ByColumnIndex_SpecifyDateTimeFormatter() {
        val row = row(LocalTimeStringForFormatter)

        val value = row.getLocalTime(0, LocalTimeFormatter)

        assertEquals(value, LocalTime.parse(LocalTimeStringForFormatter, LocalTimeFormatter))
    }

    @Test
    fun getLocalTime_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalTime(0) }
    }

    @Test
    fun getLocalTime_ByColumnName() {
        val row = row("LocalTime", LocalTimeString)

        val value = row.getLocalTime("LocalTime")

        assertEquals(value, LocalTime.parse(LocalTimeString))
    }

    @Test
    fun getLocalTime_ByColumnName_SpecifyDateTimeFormatter() {
        val row = row("LocalTime", LocalTimeStringForFormatter)

        val value = row.getLocalTime("LocalTime", LocalTimeFormatter)

        assertEquals(value, LocalTime.parse(LocalTimeStringForFormatter, LocalTimeFormatter))
    }

    @Test
    fun getLocalTime_ByColumnName_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalTime("any") }
    }

    @Test
    fun getLocalTimeOrNull_ByColumnIndex() {
        val row = row(LocalTimeString)

        val value = row.getLocalTimeOrNull(0)

        assertEquals(value, LocalTime.parse(LocalTimeString))
    }

    @Test
    fun getLocalTimeOrNull_ByColumnIndex_EmptyValue() {
        val row = row("")

        val value = row.getLocalTimeOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLocalTimeOrNull_ByColumnIndex_ValueIsNotALocalDate() {
        val row = row("2")

        val value = row.getLocalTimeOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLocalTimeOrNull_ByColumnIndex_SpecifyDateTimeFormatter() {
        val row = row(LocalTimeStringForFormatter)

        val value = row.getLocalTimeOrNull(0, LocalTimeFormatter)

        assertEquals(value, LocalTime.parse(LocalTimeStringForFormatter, LocalTimeFormatter))
    }

    @Test
    fun getLocalTimeOrNull_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalTimeOrNull(0) }
    }

    @Test
    fun getLocalTimeOrNull_ByColumnName() {
        val row = row("LocalTime", LocalTimeString)

        val value = row.getLocalTimeOrNull("LocalTime")

        assertEquals(value, LocalTime.parse(LocalTimeString))
    }

    @Test
    fun getLocalTimeOrNull_ByColumnName_EmptyValue() {
        val row = row("LocalTime", "")

        val value = row.getLocalTimeOrNull("LocalTime")

        assertNull(value)
    }

    @Test
    fun getLocalTimeOrNull_ByColumnName_ValueIsNotALocalDate() {
        val row = row("LocalTime", "2")

        val value = row.getLocalTimeOrNull("LocalTime")

        assertNull(value)
    }

    @Test
    fun getLocalTimeOrNull_ByColumnName_SpecifyDateTimeFormatter() {
        val row = row("LocalTime", LocalTimeStringForFormatter)

        val value = row.getLocalTimeOrNull("LocalTime", LocalTimeFormatter)

        assertEquals(value, LocalTime.parse(LocalTimeStringForFormatter, LocalTimeFormatter))
    }

    @Test
    fun getLocalTimeOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getLocalTimeOrNull("any") }
    }

    @Test
    fun getLocalTimeOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getLocalTimeOrNull("any", false)

        assertNull(value)
    }


    @Test
    fun getLocalDateTime_ByColumnIndex() {
        val row = row(LocalDateTimeString)

        val value = row.getLocalDateTime(0)

        assertEquals(value, LocalDateTime.parse(LocalDateTimeString))
    }

    @Test
    fun getLocalDateTime_ByColumnIndex_SpecifyDateTimeFormatter() {
        val row = row(LocalDateTimeStringForFormatter)

        val value = row.getLocalDateTime(0, LocalDateTimeFormatter)

        assertEquals(value, LocalDateTime.parse(LocalDateTimeStringForFormatter, LocalDateTimeFormatter))
    }

    @Test
    fun getLocalDateTime_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalDateTime(0) }
    }

    @Test
    fun getLocalDateTime_ByColumnName() {
        val row = row("LocalDateTime", LocalDateTimeString)

        val value = row.getLocalDateTime("LocalDateTime")

        assertEquals(value, LocalDateTime.parse(LocalDateTimeString))
    }

    @Test
    fun getLocalDateTime_ByColumnName_SpecifyDateTimeFormatter() {
        val row = row("LocalDateTime", LocalDateTimeStringForFormatter)

        val value = row.getLocalDateTime("LocalDateTime", LocalDateTimeFormatter)

        assertEquals(value, LocalDateTime.parse(LocalDateTimeStringForFormatter, LocalDateTimeFormatter))
    }

    @Test
    fun getLocalDateTime_ByColumnName_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalDateTime("any") }
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnIndex() {
        val row = row(LocalDateTimeString)

        val value = row.getLocalDateTimeOrNull(0)

        assertEquals(value, LocalDateTime.parse(LocalDateTimeString))
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnIndex_EmptyValue() {
        val row = row("")

        val value = row.getLocalDateTimeOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnIndex_ValueIsNotALocalDate() {
        val row = row("2")

        val value = row.getLocalDateTimeOrNull(0)

        assertNull(value)
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnIndex_SpecifyDateTimeFormatter() {
        val row = row(LocalDateTimeStringForFormatter)

        val value = row.getLocalDateTimeOrNull(0, LocalDateTimeFormatter)

        assertEquals(value, LocalDateTime.parse(LocalDateTimeStringForFormatter, LocalDateTimeFormatter))
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnIndex_ColumnDoesNotExist() {
        val row = emptyRow()

        assertFails { row.getLocalDateTimeOrNull(0) }
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnName() {
        val row = row("LocalDateTime", LocalDateTimeString)

        val value = row.getLocalDateTimeOrNull("LocalDateTime")

        assertEquals(value, LocalDateTime.parse(LocalDateTimeString))
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnName_EmptyValue() {
        val row = row("LocalDateTime", "")

        val value = row.getLocalDateTimeOrNull("LocalDateTime")

        assertNull(value)
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnName_ValueIsNotALocalDate() {
        val row = row("LocalDateTime", "2")

        val value = row.getLocalDateTimeOrNull("LocalDateTime")

        assertNull(value)
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnName_SpecifyDateTimeFormatter() {
        val row = row("LocalDateTime", LocalDateTimeStringForFormatter)

        val value = row.getLocalDateTimeOrNull("LocalDateTime", LocalDateTimeFormatter)

        assertEquals(value, LocalDateTime.parse(LocalDateTimeStringForFormatter, LocalDateTimeFormatter))
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsTrue() {
        val row = emptyRow()

        assertFails { row.getLocalDateTimeOrNull("any") }
    }

    @Test
    fun getLocalDateTimeOrNull_ByColumnName_ColumnDoesNotExist_throwIfColumnDoesNotExistIsFalse() {
        val row = emptyRow()

        val value = row.getLocalDateTimeOrNull("any", false)

        assertNull(value)
    }


    private fun emptyRow() = row(emptySet(), emptyArray())

    private fun row(value: String) = row(emptySet(), arrayOf(value))

    private fun row(header: String, value: String) = row(setOf(header), arrayOf(value))

    private fun row(headers: Set<String> = emptySet(), values: Array<String>) = CsvRow(
        headers, values, 1, false, false
    )
}