package net.codinux.csv.reader

internal class RowHandler(private var len: Int) {
  private var row: Array<String> = Array(len) { "" }
  private var idx = 0
  private var lines = 1
  var isCommentMode = false
    private set
  private var originalLineNumber: Long = 1

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
    if (idx > 1 || row[0].isNotEmpty()) {
      val ret = Array(idx) { index -> row[index] }
      return CsvRow(originalLineNumber, ret, isCommentMode)
    }
    return CsvRow(originalLineNumber, isCommentMode)
  }

  fun enableCommentMode() {
    isCommentMode = true
  }

  fun incLines() {
    lines++
  }
}