package net.codinux.csv.reader

import net.codinux.csv.Platform
import net.codinux.csv.reader.FieldMapper.fieldIsNotNull
import net.codinux.csv.reader.FieldMapper.toBoolean
import net.codinux.csv.reader.FieldMapper.replaceDecimalSeparator
import net.codinux.csv.reader.FieldMapper.toBooleanOrNull
import kotlin.jvm.JvmOverloads

/**
 * Index based CSV-row.
 */
class CsvRow internal constructor(
  private val header: Set<String>,
  private var _fields: Array<String>,
  originalLineNumber: Long,
  isComment: Boolean,
  isEmpty: Boolean
) {

  companion object {
    internal val EMPTY = arrayOf("")

    fun empty(header: Set<String>, originalLineNumber: Long, isComment: Boolean) =
      CsvRow(header, EMPTY, originalLineNumber, isComment, true)
  }


  private val headerIndices by lazy { header.mapIndexed { index, header -> header to index }.toMap() }

  /**
   * Gets the number of fields of this row.
   *
   * @return the number of fields of this row
   * @see CsvReader.CsvReaderBuilder.errorOnDifferentFieldCount
   */
  var fieldCount: Int = _fields.size
    private set

  /**
   * Gets all fields of this row as an unmodifiable list.
   *
   * @return all fields of this row, never `null`
   */
  val fields: List<String>
    get() = ImmutableList(_fields)

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

  internal fun updateEmptyRow(originalLineNumber: Long, isComment: Boolean) =
    updateRow(EMPTY, 1, originalLineNumber, isComment, true)

  internal fun updateRow(uncopiedFields: Array<String>, columnIndex: Int, originalLineNumber: Long, isComment: Boolean, isEmpty: Boolean) {
    // if arrays have the same size, simply copy the fields over instead of creating a new array for each row (saves a lot of memory for larger CSVs)
    if (this._fields.size == uncopiedFields.size) {
      for (i in 0 until columnIndex) {
        this.fieldCount = columnIndex
        this._fields[i] = uncopiedFields[i]
      }
    } else {
      val fields = Array(columnIndex) { index -> uncopiedFields[index] }
      this.fieldCount = fields.size
      this._fields = fields
    }

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
    if (Platform.isJavaScript) {
      if (index !in _fields.indices) { // for JavaScript we need to throw exception manually
        throw IndexOutOfBoundsException("Index $index is not within bound 0 - ${_fields.size - 1} (= getFieldCount()).")
      }
    }

    return _fields[index]
  }

  fun getString(fieldIndex: Int): String =
    this.getField(fieldIndex)

  fun getStringOrNull(fieldIndex: Int): String? =
    this.getString(fieldIndex)
      .takeIf { field -> fieldIsNotNull(field) }

  fun getBoolean(fieldIndex: Int): Boolean =
    this.getString(fieldIndex).toBoolean()

  fun getBooleanOrNull(fieldIndex: Int): Boolean? =
    this.getStringOrNull(fieldIndex)?.toBooleanOrNull()

  fun getInt(fieldIndex: Int): Int =
    this.getString(fieldIndex).toInt()

  fun getIntOrNull(fieldIndex: Int): Int? =
    this.getStringOrNull(fieldIndex)?.toIntOrNull()

  fun getLong(fieldIndex: Int): Long =
    this.getString(fieldIndex).toLong()

  fun getLongOrNull(fieldIndex: Int): Long? =
    this.getStringOrNull(fieldIndex)?.toLongOrNull()

  fun getFloat(fieldIndex: Int): Float =
    this.getString(fieldIndex).toFloat()

  fun getFloatOrNull(fieldIndex: Int): Float? =
    this.getStringOrNull(fieldIndex)?.toFloatOrNull()

  fun getFloat(fieldIndex: Int, decimalSeparator: Char): Float =
    this.getString(fieldIndex).replaceDecimalSeparator(decimalSeparator).toFloat()

  fun getFloatOrNull(fieldIndex: Int, decimalSeparator: Char): Float? =
    this.getStringOrNull(fieldIndex)?.replaceDecimalSeparator(decimalSeparator)?.toFloatOrNull()

  fun getDouble(fieldIndex: Int): Double =
    this.getString(fieldIndex).toDouble()

  fun getDoubleOrNull(fieldIndex: Int): Double? =
    this.getStringOrNull(fieldIndex)?.toDoubleOrNull()

  fun getDouble(fieldIndex: Int, decimalSeparator: Char): Double =
    this.getString(fieldIndex).replaceDecimalSeparator(decimalSeparator).toDouble()

  fun getDoubleOrNull(fieldIndex: Int, decimalSeparator: Char): Double? =
    this.getStringOrNull(fieldIndex)?.replaceDecimalSeparator(decimalSeparator)?.toDoubleOrNull()

  private inline fun getFieldIndexOrNull(name: String): Int? =
    headerIndices[name]

  private inline fun getFieldIndex(name: String): Int =
    getFieldIndexOrNull(name)
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

  @JvmOverloads
  fun getStringOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): String? {
    val value = if (throwIfColumnDoesNotExist) {
      this.getString(name)
    } else {
      getFieldIndexOrNull(name)?.let { index -> getString(index) }
    }

    return value?.takeIf { field -> fieldIsNotNull(field) }
  }

  fun getBoolean(name: String): Boolean =
    this.getString(name).toBoolean()

  @JvmOverloads
  fun getBooleanOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Boolean? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toBooleanOrNull()

  fun getInt(name: String): Int =
    this.getString(name).toInt()

  @JvmOverloads
  fun getIntOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Int? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toIntOrNull()

  fun getLong(name: String): Long =
    this.getString(name).toLong()

  @JvmOverloads
  fun getLongOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Long? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toLongOrNull()

  fun getFloat(name: String): Float =
    this.getString(name).toFloat()

  @JvmOverloads
  fun getFloatOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Float? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toFloatOrNull()

  fun getFloat(name: String, decimalSeparator: Char): Float =
    this.getString(name).replaceDecimalSeparator(decimalSeparator).toFloat()

  @JvmOverloads
  fun getFloatOrNull(name: String, decimalSeparator: Char, throwIfColumnDoesNotExist: Boolean = true): Float? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.replaceDecimalSeparator(decimalSeparator)?.toFloatOrNull()

  fun getDouble(name: String): Double =
    this.getString(name).toDouble()

  @JvmOverloads
  fun getDoubleOrNull(name: String, throwIfColumnDoesNotExist: Boolean = true): Double? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.toDoubleOrNull()

  fun getDouble(name: String, decimalSeparator: Char): Double =
    this.getString(name).replaceDecimalSeparator(decimalSeparator).toDouble()

  @JvmOverloads
  fun getDoubleOrNull(name: String, decimalSeparator: Char, throwIfColumnDoesNotExist: Boolean = true): Double? =
    this.getStringOrNull(name, throwIfColumnDoesNotExist)?.replaceDecimalSeparator(decimalSeparator)?.toDoubleOrNull()

  override fun toString(): String {
    return CsvRow::class.simpleName + "[" +
      "originalLineNumber=$originalLineNumber, " +
      "fields=$fields, " +
      "comment=$isComment" +
      "]"
  }
}