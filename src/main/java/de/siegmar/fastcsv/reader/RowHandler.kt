package de.siegmar.fastcsv.reader

internal class RowHandler(private var len: Int) {
  private var row: Array<String?>
  private var idx = 0
  private var lines = 1
  var isCommentMode = false
    private set
  private var originalLineNumber: Long = 1

  init {
    row = arrayOfNulls(len)
  }

  fun add(value: String?) {
    if (idx == len) {
      extendCapacity()
    }
    row[idx++] = value
  }

  private fun extendCapacity() {
    len *= 2
    val newRow = arrayOfNulls<String>(len)
    System.arraycopy(row, 0, newRow, 0, idx)
    row = newRow
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
    if (idx > 1 || !row[0]!!.isEmpty()) {
      val ret = arrayOfNulls<String>(idx)
      System.arraycopy(row, 0, ret, 0, idx)
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