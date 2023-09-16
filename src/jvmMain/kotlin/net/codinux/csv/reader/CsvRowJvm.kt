@file:JvmName("CsvRowJvm")

package net.codinux.csv.reader

import java.math.BigDecimal

fun CsvRow.getBigDecimal(fieldIndex: Int): BigDecimal =
  this.getString(fieldIndex).toBigDecimal()

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.toBigDecimalOrNull()