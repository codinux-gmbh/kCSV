package net.codinux.csv.reader

internal class RowHandler(
  private var len: Int,
  private val reuseRowInstance: Boolean
) {

  companion object {
    private val EMPTY = arrayOf("")
  }

  private var row: Array<String> = Array(len) { "" }
  private var idx = 0
  private var lines = 1
  var isCommentMode = false
    private set
  private var originalLineNumber: Long = 1

  internal var header: Set<String> = ImmutableSet(emptySet())
    set(value) {
      field = value
      reusedCsvRowInstance = CsvRow(value, EMPTY, originalLineNumber, isCommentMode, true)
    }

  private var reusedCsvRowInstance = CsvRow(header, EMPTY, originalLineNumber, isCommentMode, true)

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
    val fields = if (isEmpty) {
      EMPTY
    } else {
      Array(idx) { index -> row[index] }
    }

    return if (reuseRowInstance) {
      reusedCsvRowInstance.updateRow(fields, originalLineNumber, isCommentMode, isEmpty)
      reusedCsvRowInstance
    } else {
      CsvRow(header, fields, originalLineNumber, isCommentMode, isEmpty)
    }
  }

  fun enableCommentMode() {
    isCommentMode = true
  }

  fun incLines() {
    lines++
  }
}