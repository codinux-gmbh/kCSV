package net.codinux.csv.kcsv.reader

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.jvm.JvmField

/**
 * Index based CSV-row.
 */
class CsvRow internal constructor(
  /**
   * Returns the original line number (starting with 1). On multi-line rows this is the starting
   * line number.
   * Empty lines could be skipped via [CsvReader.CsvReaderBuilder.skipEmptyRows].
   *
   * @return the original line number
   */
  val originalLineNumber: Long,

  private val fields: Array<String>,
  /**
   * Provides the information if the row is a commented row.
   *
   * @return `true` if the row is a commented row
   * @see CsvReader.CsvReaderBuilder.commentStrategy
   */
  val isComment: Boolean,
) {

  internal constructor(originalLineNumber: Long, comment: Boolean) : this(originalLineNumber, EMPTY, comment)

  /**
   * Gets a field value by its index (starting with 0).
   *
   * @param index index of the field to return
   * @return field value, never `null`
   * @throws IndexOutOfBoundsException if index is out of range
   */
  fun getField(index: Int): String {
    return fields[index]
  }

  /**
   * Gets all fields of this row as an unmodifiable list.
   *
   * @return all fields of this row, never `null`
   */
  fun getFields(): List<String> {
    return fields.toList()
  }

  /**
   * Gets the number of fields of this row.
   *
   * @return the number of fields of this row
   * @see CsvReader.CsvReaderBuilder.errorOnDifferentFieldCount
   */
  fun getFieldCount(): Int {
    return fields.size
  }

  /**
   * Provides the information if the row is an empty row.
   *
   * @return `true` if the row is an empty row
   * @see CsvReader.CsvReaderBuilder.skipEmptyRows
   */
  fun isEmpty(): Boolean {
    return fields == EMPTY
  }

  fun getString(fieldIndex: Int): String =
    this.getField(fieldIndex)

  fun getNullableString(fieldIndex: Int): String? =
    this.getField(fieldIndex)
      .ifEmpty { null }

  fun getBoolean(fieldIndex: Int): Boolean =
    this.getString(fieldIndex).toBoolean()

  fun getNullableBoolean(fieldIndex: Int): Boolean? =
    this.getNullableString(fieldIndex)?.toBoolean()

  fun getInt(fieldIndex: Int): Int =
    this.getString(fieldIndex).toInt()

  fun getNullableInt(fieldIndex: Int): Int? =
    this.getNullableString(fieldIndex)?.toIntOrNull()

  fun getLong(fieldIndex: Int): Long =
    this.getString(fieldIndex).toLong()

  fun getNullableLong(fieldIndex: Int): Long? =
    this.getNullableString(fieldIndex)?.toLongOrNull()

  fun getFloat(fieldIndex: Int): Float =
    this.getString(fieldIndex).toFloat()

  fun getNullableFloat(fieldIndex: Int): Float? =
    this.getNullableString(fieldIndex)?.toFloatOrNull()

  fun getDouble(fieldIndex: Int): Double =
    this.getString(fieldIndex).toDouble()

  fun getNullableDouble(fieldIndex: Int): Double? =
    this.getNullableString(fieldIndex)?.toDoubleOrNull()

  fun getInstant(fieldIndex: Int): Instant =
    Instant.parse(this.getString(fieldIndex))

  fun getNullableInstant(fieldIndex: Int): Instant? =
    this.getNullableString(fieldIndex)?.let { Instant.parse(it) }

  fun getLocalDateTime(fieldIndex: Int): LocalDateTime =
    LocalDateTime.parse(this.getString(fieldIndex))

  fun getNullableLocalDateTime(fieldIndex: Int): LocalDateTime? =
    this.getNullableString(fieldIndex)?.let { LocalDateTime.parse(it) }

  override fun toString(): String {
    return CsvRow::class.java.simpleName + "[" +
      "originalLineNumber=$originalLineNumber, " +
      "fields=${fields.contentToString()}, " +
      "comment=$isComment" +
      "]"
  }

  companion object {
    private val EMPTY = arrayOf("")
  }
}