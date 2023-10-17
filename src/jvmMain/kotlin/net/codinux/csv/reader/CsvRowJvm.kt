@file:JvmName("CsvRowJvm")

package net.codinux.csv.reader

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun CsvRow.getFloat(fieldIndex: Int, format: NumberFormat): Float =
  this.getString(fieldIndex).mapToFloat(format)

fun CsvRow.getFloatOrNull(fieldIndex: Int, format: NumberFormat): Float? =
  this.getStringOrNull(fieldIndex)?.mapToFloat(format)

fun CsvRow.getDouble(fieldIndex: Int, format: NumberFormat): Double =
  this.getString(fieldIndex).mapToDouble(format)

fun CsvRow.getDoubleOrNull(fieldIndex: Int, format: NumberFormat): Double? =
  this.getStringOrNull(fieldIndex)?.mapToDouble(format)

fun CsvRow.getFloat(name: String, format: NumberFormat): Float =
  this.getString(name).mapToFloat(format)

@JvmOverloads
fun CsvRow.getFloatOrNull(name: String, format: NumberFormat, throwIfColumnDoesNotExist: Boolean = true): Float? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToFloat(format)

fun CsvRow.getDouble(name: String, format: NumberFormat): Double =
  this.getString(name).mapToDouble(format)

@JvmOverloads
fun CsvRow.getDoubleOrNull(name: String, format: NumberFormat, throwIfColumnDoesNotExist: Boolean = true): Double? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToDouble(format)


fun CsvRow.getBigDecimal(fieldIndex: Int): BigDecimal =
  this.getString(fieldIndex).toBigDecimal()

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.toBigDecimalOrNull()

fun CsvRow.getBigDecimal(fieldIndex: Int, decimalSeparator: Char): BigDecimal =
  this.getString(fieldIndex).replaceDecimalSeparatorAndMapToBigDecimal(decimalSeparator)

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int, decimalSeparator: Char): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.replaceDecimalSeparatorAndMapToBigDecimal(decimalSeparator)

fun CsvRow.getBigDecimal(fieldIndex: Int, format: NumberFormat): BigDecimal =
  this.getString(fieldIndex).mapToBigDecimal(format)

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int, format: NumberFormat): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.mapToBigDecimal(format)

fun CsvRow.getBigDecimal(name: String): BigDecimal =
  this.getString(name).toBigDecimal()

@JvmOverloads
fun CsvRow.getBigDecimalOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): BigDecimal? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toBigDecimalOrNull()

fun CsvRow.getBigDecimal(name: String, decimalSeparator: Char): BigDecimal =
  this.getString(name).replaceDecimalSeparatorAndMapToBigDecimal(decimalSeparator)

@JvmOverloads
fun CsvRow.getBigDecimalOrNull(name: String, decimalSeparator: Char, throwIfColumnDoesNotExist: Boolean = true): BigDecimal? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.replaceDecimalSeparatorAndMapToBigDecimal(decimalSeparator)

fun CsvRow.getBigDecimal(name: String, format: NumberFormat): BigDecimal =
  this.getString(name).mapToBigDecimal(format)

@JvmOverloads
fun CsvRow.getBigDecimalOrNull(name: String, format: NumberFormat, throwIfColumnDoesNotExist: Boolean = true): BigDecimal? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToBigDecimal(format)


fun CsvRow.getInstant(fieldIndex: Int): Instant =
  this.getString(fieldIndex).mapToInstant()

fun CsvRow.getInstantOrNull(fieldIndex: Int): Instant? =
  this.getStringOrNull(fieldIndex)?.mapToInstant()

fun CsvRow.getInstant(name: String): Instant =
  this.getString(name).mapToInstant()

@JvmOverloads
fun CsvRow.getInstantOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Instant? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToInstant()


fun CsvRow.getLocalDate(fieldIndex: Int): LocalDate =
  this.getString(fieldIndex).mapToLocalDate()

fun CsvRow.getLocalDateOrNull(fieldIndex: Int): LocalDate? =
  this.getStringOrNull(fieldIndex)?.mapToLocalDate()

fun CsvRow.getLocalDate(fieldIndex: Int, formatter: DateTimeFormatter): LocalDate =
  this.getString(fieldIndex).mapToLocalDate(formatter)

fun CsvRow.getLocalDateOrNull(fieldIndex: Int, formatter: DateTimeFormatter): LocalDate? =
  this.getStringOrNull(fieldIndex)?.mapToLocalDate(formatter)

fun CsvRow.getLocalDate(name: String): LocalDate =
  this.getString(name).mapToLocalDate()

@JvmOverloads
fun CsvRow.getLocalDateOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): LocalDate? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToLocalDate()

fun CsvRow.getLocalDate(name: String, formatter: DateTimeFormatter): LocalDate =
  this.getString(name).mapToLocalDate(formatter)

@JvmOverloads
fun CsvRow.getLocalDateOrNull(name: String, formatter: DateTimeFormatter, throwIfColumnDoesNotExist: Boolean = true): LocalDate? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToLocalDate(formatter)


fun CsvRow.getLocalTime(fieldIndex: Int): LocalTime =
  this.getString(fieldIndex).mapToLocalTime()

fun CsvRow.getLocalTimeOrNull(fieldIndex: Int): LocalTime? =
  this.getStringOrNull(fieldIndex)?.mapToLocalTime()

fun CsvRow.getLocalTime(fieldIndex: Int, formatter: DateTimeFormatter): LocalTime =
  this.getString(fieldIndex).mapToLocalTime(formatter)

fun CsvRow.getLocalTimeOrNull(fieldIndex: Int, formatter: DateTimeFormatter): LocalTime? =
  this.getStringOrNull(fieldIndex)?.mapToLocalTime(formatter)

fun CsvRow.getLocalTime(name: String): LocalTime =
  this.getString(name).mapToLocalTime()

@JvmOverloads
fun CsvRow.getLocalTimeOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): LocalTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToLocalTime()

fun CsvRow.getLocalTime(name: String, formatter: DateTimeFormatter): LocalTime =
  this.getString(name).mapToLocalTime(formatter)

@JvmOverloads
fun CsvRow.getLocalTimeOrNull(name: String, formatter: DateTimeFormatter, throwIfColumnDoesNotExist: Boolean = true): LocalTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToLocalTime(formatter)


fun CsvRow.getLocalDateTime(fieldIndex: Int): LocalDateTime =
  this.getString(fieldIndex).mapToLocalDateTime()

fun CsvRow.getLocalDateTimeOrNull(fieldIndex: Int): LocalDateTime? =
  this.getStringOrNull(fieldIndex)?.mapToLocalDateTime()

fun CsvRow.getLocalDateTime(fieldIndex: Int, formatter: DateTimeFormatter): LocalDateTime =
  this.getString(fieldIndex).mapToLocalDateTime(formatter)

fun CsvRow.getLocalDateTimeOrNull(fieldIndex: Int, formatter: DateTimeFormatter): LocalDateTime? =
  this.getStringOrNull(fieldIndex)?.mapToLocalDateTime(formatter)

fun CsvRow.getLocalDateTime(name: String): LocalDateTime =
  this.getString(name).mapToLocalDateTime()

@JvmOverloads
fun CsvRow.getLocalDateTimeOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): LocalDateTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToLocalDateTime()

fun CsvRow.getLocalDateTime(name: String, formatter: DateTimeFormatter): LocalDateTime =
  this.getString(name).mapToLocalDateTime(formatter)

@JvmOverloads
fun CsvRow.getLocalDateTimeOrNull(name: String, formatter: DateTimeFormatter, throwIfColumnDoesNotExist: Boolean = true): LocalDateTime? =
  this.getStringOrNull(name, throwIfColumnDoesNotExist)?.mapToLocalDateTime(formatter)



private fun String.mapToFloat(format: NumberFormat): Float = format.parse(this).toFloat()

private fun String.mapToDouble(format: NumberFormat): Double = format.parse(this).toDouble()

// TODO: is there a better way to map a Number to BigDecimal?
private fun String.mapToBigDecimal(format: NumberFormat): BigDecimal = format.parse(this).toDouble().toBigDecimal()

private fun String.replaceDecimalSeparatorAndMapToBigDecimal(decimalSeparator: Char) =
  this.replace(decimalSeparator, '.').toBigDecimal()


private fun String.mapToInstant(): Instant = Instant.parse(this)

private fun String.mapToLocalDate(): LocalDate = LocalDate.parse(this)

private fun String.mapToLocalDate(formatter: DateTimeFormatter): LocalDate = LocalDate.parse(this, formatter)

private fun String.mapToLocalTime(): LocalTime = LocalTime.parse(this)

private fun String.mapToLocalTime(formatter: DateTimeFormatter): LocalTime = LocalTime.parse(this, formatter)

private fun String.mapToLocalDateTime(): LocalDateTime = LocalDateTime.parse(this)

private fun String.mapToLocalDateTime(formatter: DateTimeFormatter): LocalDateTime = LocalDateTime.parse(this, formatter)