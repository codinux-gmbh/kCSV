@file:JvmName("CsvRowJvm")

package net.codinux.csv.reader

import net.codinux.csv.reader.FieldMapper.replaceDecimalSeparator
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun CsvRow.getFloat(fieldIndex: Int, format: NumberFormat): Float =
  this.getString(fieldIndex).toFloat(format)

fun CsvRow.getFloatOrNull(fieldIndex: Int, format: NumberFormat): Float? =
  this.getStringOrNull(fieldIndex)?.toFloatOrNull(format)

fun CsvRow.getDouble(fieldIndex: Int, format: NumberFormat): Double =
  this.getString(fieldIndex).toDouble(format)

fun CsvRow.getDoubleOrNull(fieldIndex: Int, format: NumberFormat): Double? =
  this.getStringOrNull(fieldIndex)?.toDoubleOrNull(format)

fun CsvRow.getFloat(name: String, format: NumberFormat): Float =
  this.getString(name).toFloat(format)

@JvmOverloads
fun CsvRow.getFloatOrNull(name: String, format: NumberFormat, throwIfColumnDoesNotExist: Boolean = true): Float? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toFloatOrNull(format)

fun CsvRow.getDouble(name: String, format: NumberFormat): Double =
  this.getString(name).toDouble(format)

@JvmOverloads
fun CsvRow.getDoubleOrNull(name: String, format: NumberFormat, throwIfColumnDoesNotExist: Boolean = true): Double? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toDoubleOrNull(format)


fun CsvRow.getBigDecimal(fieldIndex: Int): BigDecimal =
  this.getString(fieldIndex).toBigDecimal()

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.toBigDecimalOrNull()

fun CsvRow.getBigDecimal(fieldIndex: Int, decimalSeparator: Char): BigDecimal =
  this.getString(fieldIndex).replaceDecimalSeparator(decimalSeparator).toBigDecimal()

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int, decimalSeparator: Char): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.replaceDecimalSeparator(decimalSeparator)?.toBigDecimalOrNull()

fun CsvRow.getBigDecimal(fieldIndex: Int, format: NumberFormat): BigDecimal =
  this.getString(fieldIndex).toBigDecimal(format)

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int, format: NumberFormat): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.toBigDecimalOrNull(format)

fun CsvRow.getBigDecimal(name: String): BigDecimal =
  this.getString(name).toBigDecimal()

@JvmOverloads
fun CsvRow.getBigDecimalOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): BigDecimal? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toBigDecimalOrNull()

fun CsvRow.getBigDecimal(name: String, decimalSeparator: Char): BigDecimal =
  this.getString(name).replaceDecimalSeparator(decimalSeparator).toBigDecimal()

@JvmOverloads
fun CsvRow.getBigDecimalOrNull(name: String, decimalSeparator: Char, throwIfColumnDoesNotExist: Boolean = true): BigDecimal? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.replaceDecimalSeparator(decimalSeparator)?.toBigDecimalOrNull()

fun CsvRow.getBigDecimal(name: String, format: NumberFormat): BigDecimal =
  this.getString(name).toBigDecimal(format)

@JvmOverloads
fun CsvRow.getBigDecimalOrNull(name: String, format: NumberFormat, throwIfColumnDoesNotExist: Boolean = true): BigDecimal? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toBigDecimalOrNull(format)


fun CsvRow.getInstant(fieldIndex: Int): Instant =
  this.getString(fieldIndex).toInstant()

fun CsvRow.getInstantOrNull(fieldIndex: Int): Instant? =
  this.getStringOrNull(fieldIndex)?.toInstantOrNull()

fun CsvRow.getInstant(name: String): Instant =
  this.getString(name).toInstant()

@JvmOverloads
fun CsvRow.getInstantOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Instant? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toInstantOrNull()


fun CsvRow.getLocalDate(fieldIndex: Int): LocalDate =
  this.getString(fieldIndex).toLocalDate()

fun CsvRow.getLocalDateOrNull(fieldIndex: Int): LocalDate? =
  this.getStringOrNull(fieldIndex)?.toLocalDateOrNull()

fun CsvRow.getLocalDate(fieldIndex: Int, formatter: DateTimeFormatter): LocalDate =
  this.getString(fieldIndex).toLocalDate(formatter)

fun CsvRow.getLocalDateOrNull(fieldIndex: Int, formatter: DateTimeFormatter): LocalDate? =
  this.getStringOrNull(fieldIndex)?.toLocalDateOrNull(formatter)

fun CsvRow.getLocalDate(name: String): LocalDate =
  this.getString(name).toLocalDate()

@JvmOverloads
fun CsvRow.getLocalDateOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): LocalDate? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLocalDateOrNull()

fun CsvRow.getLocalDate(name: String, formatter: DateTimeFormatter): LocalDate =
  this.getString(name).toLocalDate(formatter)

@JvmOverloads
fun CsvRow.getLocalDateOrNull(name: String, formatter: DateTimeFormatter, throwIfColumnDoesNotExist: Boolean = true): LocalDate? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLocalDateOrNull(formatter)


fun CsvRow.getLocalTime(fieldIndex: Int): LocalTime =
  this.getString(fieldIndex).toLocalTime()

fun CsvRow.getLocalTimeOrNull(fieldIndex: Int): LocalTime? =
  this.getStringOrNull(fieldIndex)?.toLocalTimeOrNull()

fun CsvRow.getLocalTime(fieldIndex: Int, formatter: DateTimeFormatter): LocalTime =
  this.getString(fieldIndex).toLocalTime(formatter)

fun CsvRow.getLocalTimeOrNull(fieldIndex: Int, formatter: DateTimeFormatter): LocalTime? =
  this.getStringOrNull(fieldIndex)?.toLocalTimeOrNull(formatter)

fun CsvRow.getLocalTime(name: String): LocalTime =
  this.getString(name).toLocalTime()

@JvmOverloads
fun CsvRow.getLocalTimeOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): LocalTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLocalTimeOrNull()

fun CsvRow.getLocalTime(name: String, formatter: DateTimeFormatter): LocalTime =
  this.getString(name).toLocalTime(formatter)

@JvmOverloads
fun CsvRow.getLocalTimeOrNull(name: String, formatter: DateTimeFormatter, throwIfColumnDoesNotExist: Boolean = true): LocalTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLocalTimeOrNull(formatter)


fun CsvRow.getLocalDateTime(fieldIndex: Int): LocalDateTime =
  this.getString(fieldIndex).toLocalDateTime()

fun CsvRow.getLocalDateTimeOrNull(fieldIndex: Int): LocalDateTime? =
  this.getStringOrNull(fieldIndex)?.toLocalDateTimeOrNull()

fun CsvRow.getLocalDateTime(fieldIndex: Int, formatter: DateTimeFormatter): LocalDateTime =
  this.getString(fieldIndex).toLocalDateTime(formatter)

fun CsvRow.getLocalDateTimeOrNull(fieldIndex: Int, formatter: DateTimeFormatter): LocalDateTime? =
  this.getStringOrNull(fieldIndex)?.toLocalDateTimeOrNull(formatter)

fun CsvRow.getLocalDateTime(name: String): LocalDateTime =
  this.getString(name).toLocalDateTime()

@JvmOverloads
fun CsvRow.getLocalDateTimeOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): LocalDateTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLocalDateTimeOrNull()

fun CsvRow.getLocalDateTime(name: String, formatter: DateTimeFormatter): LocalDateTime =
  this.getString(name).toLocalDateTime(formatter)

@JvmOverloads
fun CsvRow.getLocalDateTimeOrNull(name: String, formatter: DateTimeFormatter, throwIfColumnDoesNotExist: Boolean = true): LocalDateTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLocalDateTimeOrNull(formatter)



private fun String.toFloat(format: NumberFormat): Float = format.parse(this).toFloat()

private fun String.toFloatOrNull(format: NumberFormat): Float? = toObjectOrNull { this.toFloat(format) }

private fun String.toDouble(format: NumberFormat): Double = format.parse(this).toDouble()

private fun String.toDoubleOrNull(format: NumberFormat): Double? = toObjectOrNull { this.toDouble(format) }

// TODO: is there a better way to map a Number to BigDecimal?
private fun String.toBigDecimal(format: NumberFormat): BigDecimal = format.parse(this).toDouble().toBigDecimal()

private fun String.toBigDecimalOrNull(format: NumberFormat): BigDecimal? = toObjectOrNull { this.toBigDecimal(format) }


private fun String.toInstant(): Instant = Instant.parse(this)

private fun String.toInstantOrNull(): Instant? = toObjectOrNull { this.toInstant() }

private fun String.toLocalDate(): LocalDate = LocalDate.parse(this)

private fun String.toLocalDateOrNull(): LocalDate? = toObjectOrNull { this.toLocalDate() }

private fun String.toLocalDate(formatter: DateTimeFormatter): LocalDate = LocalDate.parse(this, formatter)

private fun String.toLocalDateOrNull(formatter: DateTimeFormatter): LocalDate? = toObjectOrNull { this.toLocalDate(formatter) }

private fun String.toLocalTime(): LocalTime = LocalTime.parse(this)

private fun String.toLocalTimeOrNull(): LocalTime? = toObjectOrNull { this.toLocalTime() }

private fun String.toLocalTime(formatter: DateTimeFormatter): LocalTime = LocalTime.parse(this, formatter)

private fun String.toLocalTimeOrNull(formatter: DateTimeFormatter): LocalTime? = toObjectOrNull { this.toLocalTime(formatter) }

private fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this)

private fun String.toLocalDateTimeOrNull(): LocalDateTime? = toObjectOrNull { this.toLocalDateTime() }

private fun String.toLocalDateTime(formatter: DateTimeFormatter): LocalDateTime = LocalDateTime.parse(this, formatter)

private fun String.toLocalDateTimeOrNull(formatter: DateTimeFormatter): LocalDateTime? = toObjectOrNull { this.toLocalDateTime(formatter) }

private inline fun <T> toObjectOrNull(mapper: () -> T): T? = try {
  mapper()
} catch (ignored: Throwable) {
  null
}