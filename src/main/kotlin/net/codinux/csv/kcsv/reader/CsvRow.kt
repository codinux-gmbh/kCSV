package net.codinux.csv.kcsv.reader

import java.util.*

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
    @JvmField val originalLineNumber: Long,
    private val fields: Array<String>,
    /**
   * Provides the information if the row is a commented row.
   *
   * @return `true` if the row is a commented row
   * @see CsvReader.CsvReaderBuilder.commentStrategy
   */
  val isComment: Boolean
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
    return Collections.unmodifiableList(Arrays.asList(*fields))
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

  override fun toString(): String {
    return StringJoiner(", ", CsvRow::class.java.simpleName + "[", "]")
      .add("originalLineNumber=$originalLineNumber")
      .add("fields=" + Arrays.toString(fields))
      .add("comment=" + isComment)
      .toString()
  }

  companion object {
    private val EMPTY = arrayOf("")
  }
}