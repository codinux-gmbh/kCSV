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
class CsvRow internal constructor(
  private val header: Set<String>,
  fields: List<String>,
  originalLineNumber: Long,
  isComment: Boolean,
  isEmpty: Boolean
) {

  private val headerIndices = header.mapIndexed { index, header -> header to index }.toMap().toImmutableMap()

  /**
   * Gets the number of fields of this row.
   *
   * @return the number of fields of this row
   * @see CsvReader.CsvReaderBuilder.errorOnDifferentFieldCount
   */
  var fieldCount: Int = fields.size
    private set

  /**
   * Gets all fields of this row as an unmodifiable list.
   *
   * @return all fields of this row, never `null`
   */
  var fields: List<String> = ImmutableList(fields)
    private set

  /**
   * Returns the original line number (starting with 1). On multi-line rows this is the starting
   * line number.
   * Empty lines could be skipped via [CsvReader.CsvReaderBuilder.skipEmptyRows].
   *
   * @return the original line number
   */
  var originalLineNumber: Long = originalLineNumber
    private set

  /**
   * Provides the information if the row is a commented row.
   *
   * @return `true` if the row is a commented row
   * @see CsvReader.CsvReaderBuilder.commentStrategy
   */
  var isComment: Boolean = isComment
    private set

  /**
   * Provides the information if the row is an empty row.
   *
   * @return `true` if the row is an empty row
   * @see CsvReader.CsvReaderBuilder.skipEmptyRows
   */
  var isEmpty: Boolean = isEmpty
    private set

  internal fun updateRow(fields: List<String>, originalLineNumber: Long, isComment: Boolean, isEmpty: Boolean) {
    this.fieldCount = fields.size
    this.fields = ImmutableList(fields)
    this.originalLineNumber = originalLineNumber
    this.isComment = isComment
    this.isEmpty = isEmpty
  }


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
    this.getString(fieldIndex)
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

  private inline fun getFieldIndex(name: String): Int =
    headerIndices[name]
      ?: throw NoSuchElementException("No element with name '$name' found. Valid names are: $header")

  operator fun get(name: String): String = getField(name)

  /**
   * Gets a field value by its name.
   *
   * @param name field name
   * @return field value, never `null`
   * @throws NoSuchElementException if this row has no such field
   */
  fun getField(name: String): String =
    getString(getFieldIndex(name))

  fun getString(name: String): String =
    this.getField(name)

  fun getStringOrNull(name: String): String? =
    this.getString(name)
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

  override fun toString(): String {
    return CsvRow::class.simpleName + "[" +
      "originalLineNumber=$originalLineNumber, " +
      "fields=$fields, " +
      "comment=$isComment" +
      "]"
  }
}