package net.codinux.csv.reader

internal class RowHandler(
  private var len: Int,
  private val reuseRowInstance: Boolean
) {

  private var row: Array<String> = Array(len) { "" }
  private var idx = 0
  private var lines = 1
  var isCommentMode = false
    private set
  private var originalLineNumber: Long = 1

  internal var header: Set<String> = ImmutableSet(emptySet())
    set(value) {
      field = value
      reusedCsvRowInstance = CsvRow.empty(header, originalLineNumber, isCommentMode)
    }

  private var reusedCsvRowInstance = CsvRow.empty(header, originalLineNumber, isCommentMode)

  private var reusedFieldsArray: Array<String> = arrayOf("")

  fun add(value: String) {
    if (idx == len) {
      extendCapacity()
    }
    row[idx++] = value
  }

  private fun extendCapacity() {
    len *= 2
    row = Array(len) { index -> if (index < idx) row[index] else "" }
  }

  fun buildAndReset(): CsvRow? {
    val csvRow = if (idx > 0) build() else null
    idx = 0
    originalLineNumber += lines.toLong()
    lines = 1
    isCommentMode = false
    return csvRow
  }

  private fun build(): CsvRow {
    val isEmpty = !!!(idx > 1 || row[0].isNotEmpty())

    return if (reuseRowInstance) {
      buildForReusedRow(isEmpty)
    } else {
      if (isEmpty) {
        CsvRow.empty(header, originalLineNumber, isCommentMode)
      } else {
        val fields = Array(idx) { index -> row[index] }
        CsvRow(header, fields, originalLineNumber, isCommentMode, isEmpty)
      }
    }
  }

  private inline fun buildForReusedRow(isEmpty: Boolean): CsvRow {
    if (isEmpty) {
      reusedCsvRowInstance.updateEmptyRow(originalLineNumber, isCommentMode)
    } else {
      if (idx > reusedFieldsArray.size) { // expand reusedFieldsArray
        reusedFieldsArray = Array(idx) { index -> row[index] }
      } else {
        var index = 0
        while (index < idx) {
          reusedFieldsArray[index] = row[index]
          index++
        }
      }

      reusedCsvRowInstance.updateRow(idx, reusedFieldsArray, originalLineNumber, isCommentMode, isEmpty)
    }

    return reusedCsvRowInstance
  }

  fun enableCommentMode() {
    isCommentMode = true
  }

  fun incLines() {
    lines++
  }
}