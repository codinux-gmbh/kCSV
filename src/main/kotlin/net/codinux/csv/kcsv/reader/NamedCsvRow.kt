package net.codinux.csv.kcsv.reader

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal
import java.util.*
import kotlin.NoSuchElementException
import kotlin.jvm.JvmField

/**
 * Name (header) based CSV-row.
 */
class NamedCsvRow internal constructor(header: Set<String>, row: CsvRow) {
  /**
   * Returns the original line number (starting with 1). On multi-line rows this is the starting
   * line number.
   * Empty lines (and maybe commented lines) have been skipped.
   *
   * @return the original line number
   */
  @JvmField
  val originalLineNumber: Long
  private val fieldMap: MutableMap<String, String>

  init {
    originalLineNumber = row.originalLineNumber
    fieldMap = LinkedHashMap(header.size)
    var i = 0
    for (h in header) {
      fieldMap[h] = row.getField(i++)
    }
  }

  /**
   * Gets an unmodifiable map of header names and field values of this row.
   *
   *
   * The map will always contain all header names - even if their value is `null`.
   *
   * @return an unmodifiable map of header names and field values of this row
   */
  val fields: Map<String, String>
    get() = Collections.unmodifiableMap(fieldMap)

  /**
   * Gets a field value by its name.
   *
   * @param name field name
   * @return field value, never `null`
   * @throws NoSuchElementException if this row has no such field
   */
  fun getField(name: String): String {
    return fieldMap[name]
      ?: throw NoSuchElementException(
        "No element with name '" + name + "' found. "
          + "Valid names are: " + fieldMap.keys
      )
  }

  fun getString(name: String): String =
    this.getField(name)

  fun getNullableString(name: String): String? =
    this.getField(name)
      .ifEmpty { null }

  fun getBoolean(name: String): Boolean =
    this.getString(name).toBoolean()

  fun getNullableBoolean(name: String): Boolean? =
    this.getNullableString(name)?.toBoolean()

  fun getInt(name: String): Int =
    this.getString(name).toInt()

  fun getNullableInt(name: String): Int? =
    this.getNullableString(name)?.toIntOrNull()

  fun getLong(name: String): Long =
    this.getString(name).toLong()

  fun getNullableLong(name: String): Long? =
    this.getNullableString(name)?.toLongOrNull()

  fun getFloat(name: String): Float =
    this.getString(name).toFloat()

  fun getNullableFloat(name: String): Float? =
    this.getNullableString(name)?.toFloatOrNull()

  fun getDouble(name: String): Double =
    this.getString(name).toDouble()

  fun getNullableDouble(name: String): Double? =
    this.getNullableString(name)?.toDoubleOrNull()

  fun getBigDecimal(name: String): BigDecimal =
    this.getString(name).toBigDecimal()

  fun getNullableBigDecimal(name: String): BigDecimal? =
    this.getNullableString(name)?.toBigDecimalOrNull()

  fun getInstant(name: String): Instant =
    Instant.parse(this.getString(name))

  fun getNullableInstant(name: String): Instant? =
    this.getNullableString(name)?.let { Instant.parse(it) }

  fun getLocalDateTime(name: String): LocalDateTime =
    LocalDateTime.parse(this.getString(name))

  fun getNullableLocalDateTime(name: String): LocalDateTime? =
    this.getNullableString(name)?.let { LocalDateTime.parse(it) }

  override fun toString(): String {
    return NamedCsvRow::class.java.simpleName + "[" +
      "originalLineNumber=$originalLineNumber, " +
      "fieldMap=$fieldMap" +
      "]"
  }
}