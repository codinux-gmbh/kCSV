package net.codinux.csv.kcsv.reader

import java.util.*

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
  private val fieldMap: MutableMap<String?, String?>

  init {
    originalLineNumber = row.originalLineNumber
    fieldMap = LinkedHashMap(header.size)
    var i = 0
    for (h in header) {
      fieldMap[h] = row.getField(i++)
    }
  }

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

  val fields: Map<String?, String?>
    /**
     * Gets an unmodifiable map of header names and field values of this row.
     *
     *
     * The map will always contain all header names - even if their value is `null`.
     *
     * @return an unmodifiable map of header names and field values of this row
     */
    get() = Collections.unmodifiableMap(fieldMap)

  override fun toString(): String {
    return StringJoiner(", ", NamedCsvRow::class.java.simpleName + "[", "]")
      .add("originalLineNumber=$originalLineNumber")
      .add("fieldMap=$fieldMap")
      .toString()
  }
}