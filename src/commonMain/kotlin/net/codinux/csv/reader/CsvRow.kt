package net.codinux.csv.reader

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import net.codinux.csv.reader.FieldMapper.fieldIsNotNull
import net.codinux.csv.reader.FieldMapper.mapToBoolean
import net.codinux.csv.reader.FieldMapper.mapToDouble
import net.codinux.csv.reader.FieldMapper.mapToFloat
import net.codinux.csv.reader.FieldMapper.mapToInt
import net.codinux.csv.reader.FieldMapper.mapToLong

/**
 * Index based CSV-row.
 */
class CsvRow private constructor(
  /**
   * Returns the original line number (starting with 1). On multi-line rows this is the starting
   * line number.
   * Empty lines could be skipped via [CsvReader.CsvReaderBuilder.skipEmptyRows].
   *
   * @return the original line number
   */
  val originalLineNumber: Long,

  /**
   * Gets all fields of this row as an unmodifiable list.
   *
   * @return all fields of this row, never `null`
   */
  val fields: List<String>,
  /**
   * Provides the information if the row is a commented row.
   *
   * @return `true` if the row is a commented row
   * @see CsvReader.CsvReaderBuilder.commentStrategy
   */
  val isComment: Boolean,

  /**
   * Provides the information if the row is an empty row.
   *
   * @return `true` if the row is an empty row
   * @see CsvReader.CsvReaderBuilder.skipEmptyRows
   */
  val isEmpty: Boolean
) {

  internal constructor(originalLineNumber: Long, fields: Array<String>, comment: Boolean)
          : this(originalLineNumber, ImmutableList(fields.asList()), comment, false)

  internal constructor(originalLineNumber: Long, comment: Boolean) : this(originalLineNumber, EMPTY, comment, true)


  /**
   * Gets the number of fields of this row.
   *
   * @return the number of fields of this row
   * @see CsvReader.CsvReaderBuilder.errorOnDifferentFieldCount
   */
  val fieldCount: Int = fields.size

  operator fun get(index: Int) = getField(index)

  /**
   * Gets a field value by its index (starting with 0).
   *
   * @param index index of the field to return
   * @return field value, never `null`
   * @throws IndexOutOfBoundsException if index is out of range
   */
  fun getField(index: Int): String {
    if (index !in fields.indices) { // for JavaScript we need to throw exception manually
      throw IndexOutOfBoundsException("Index $index is not within bound 0 - ${fields.size - 1} (= getFieldCount()).")
    }

    return fields[index]
  }

  fun getString(fieldIndex: Int): String =
    this.getField(fieldIndex)

  fun getStringOrNull(fieldIndex: Int): String? =
    this.getField(fieldIndex)
      .takeIf { field -> fieldIsNotNull(field) }

  fun getBoolean(fieldIndex: Int): Boolean =
    this.getString(fieldIndex).mapToBoolean()

  fun getBooleanOrNull(fieldIndex: Int): Boolean? =
    this.getStringOrNull(fieldIndex)?.mapToBoolean()

  fun getInt(fieldIndex: Int): Int =
    this.getString(fieldIndex).mapToInt()

  fun getIntOrNull(fieldIndex: Int): Int? =
    this.getStringOrNull(fieldIndex)?.mapToInt()

  fun getLong(fieldIndex: Int): Long =
    this.getString(fieldIndex).mapToLong()

  fun getLongOrNull(fieldIndex: Int): Long? =
    this.getStringOrNull(fieldIndex)?.mapToLong()

  fun getFloat(fieldIndex: Int): Float =
    this.getString(fieldIndex).mapToFloat()

  fun getFloatOrNull(fieldIndex: Int): Float? =
    this.getStringOrNull(fieldIndex)?.mapToFloat()

  fun getDouble(fieldIndex: Int): Double =
    this.getString(fieldIndex).mapToDouble()

  fun getDoubleOrNull(fieldIndex: Int): Double? =
    this.getStringOrNull(fieldIndex)?.mapToDouble()

  fun getInstant(fieldIndex: Int): Instant =
    Instant.parse(this.getString(fieldIndex))

  fun getInstantOrNull(fieldIndex: Int): Instant? =
    this.getStringOrNull(fieldIndex)?.let { Instant.parse(it) }

  fun getLocalDateTime(fieldIndex: Int): LocalDateTime =
    LocalDateTime.parse(this.getString(fieldIndex))

  fun getLocalDateTimeOrNull(fieldIndex: Int): LocalDateTime? =
    this.getStringOrNull(fieldIndex)?.let { LocalDateTime.parse(it) }

  override fun toString(): String {
    return CsvRow::class.simpleName + "[" +
      "originalLineNumber=$originalLineNumber, " +
      "fields=$fields, " +
      "comment=$isComment" +
      "]"
  }

  companion object {
    private val EMPTY = ImmutableList("")
  }
}