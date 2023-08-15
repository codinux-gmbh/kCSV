package net.codinux.csv.reader

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import net.codinux.csv.reader.FieldMapper.mapToBoolean
import net.codinux.csv.reader.FieldMapper.mapToDouble
import net.codinux.csv.reader.FieldMapper.mapToFloat
import net.codinux.csv.reader.FieldMapper.mapToInt
import net.codinux.csv.reader.FieldMapper.mapToLong

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
  val originalLineNumber: Long = row.originalLineNumber

  private val fieldMap: Map<String, String> = header
      .mapIndexed { index, header -> header to row.getString(index) }
      .toMap()
      .toImmutableMap()

  /**
   * Gets an unmodifiable map of header names and field values of this row.
   *
   *
   * The map will always contain all header names - even if their value is `null`.
   *
   * @return an unmodifiable map of header names and field values of this row
   */
  val fields: Map<String, String>
    get() = fieldMap

  operator fun get(name: String): String = getField(name)

  /**
   * Gets a field value by its name.
   *
   * @param name field name
   * @return field value, never `null`
   * @throws NoSuchElementException if this row has no such field
   */
  fun getField(name: String): String {
    return fieldMap[name]
      ?: throw NoSuchElementException("No element with name '$name' found. Valid names are: ${fieldMap.keys}")
  }

  fun getString(name: String): String =
    this.getField(name)

  fun getStringOrNull(name: String): String? =
    this.getField(name)
      .takeIf { field -> fieldIsNotNull(field) }

  fun getBoolean(name: String): Boolean =
    this.getString(name).mapToBoolean()

  fun getBooleanOrNull(name: String): Boolean? =
    this.getStringOrNull(name)?.mapToBoolean()

  fun getInt(name: String): Int =
    this.getString(name).mapToInt()

  fun getIntOrNull(name: String): Int? =
    this.getStringOrNull(name)?.mapToInt()

  fun getLong(name: String): Long =
    this.getString(name).mapToLong()

  fun getLongOrNull(name: String): Long? =
    this.getStringOrNull(name)?.mapToLong()

  fun getFloat(name: String): Float =
    this.getString(name).mapToFloat()

  fun getFloatOrNull(name: String): Float? =
    this.getStringOrNull(name)?.mapToFloat()

  fun getDouble(name: String): Double =
    this.getString(name).mapToDouble()

  fun getDoubleOrNull(name: String): Double? =
    this.getStringOrNull(name)?.mapToDouble()

  fun getInstant(name: String): Instant =
    Instant.parse(this.getString(name))

  fun getInstantOrNull(name: String): Instant? =
    this.getStringOrNull(name)?.let { Instant.parse(it) }

  fun getLocalDateTime(name: String): LocalDateTime =
    LocalDateTime.parse(this.getString(name))

  fun getLocalDateTimeOrNull(name: String): LocalDateTime? =
    this.getStringOrNull(name)?.let { LocalDateTime.parse(it) }

  private fun fieldIsNotNull(field: String): Boolean =
    field.isNotBlank() && field.equals("null", ignoreCase = true) == false

  override fun toString(): String {
    return NamedCsvRow::class.simpleName + "[" +
      "originalLineNumber=$originalLineNumber, " +
      "fieldMap=$fieldMap" +
      "]"
  }
}