@file:JvmName("CsvRowJvm")

package net.codinux.csv.reader

import java.math.BigDecimal
import java.text.NumberFormat


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


private fun String.mapToFloat(format: NumberFormat): Float = format.parse(this).toFloat()

private fun String.mapToDouble(format: NumberFormat): Double = format.parse(this).toDouble()

// TODO: is there a better way to map a Number to BigDecimal?
private fun String.mapToBigDecimal(format: NumberFormat): BigDecimal = format.parse(this).toDouble().toBigDecimal()

private fun String.replaceDecimalSeparatorAndMapToBigDecimal(decimalSeparator: Char) =
  this.replace(decimalSeparator, '.').toBigDecimal()