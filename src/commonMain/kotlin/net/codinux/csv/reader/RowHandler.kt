package net.codinux.csv.reader

internal class RowHandler(private var len: Int) {

  companion object {
    private val EMPTY = ImmutableList("")
  }

  private var row: Array<String> = Array(len) { "" }
  private var idx = 0
  private var lines = 1
  var isCommentMode = false
    private set
  private var originalLineNumber: Long = 1

  internal var header: Set<String> = ImmutableSet(emptySet())

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
    val isNotEmpty = idx > 1 || row[0].isNotEmpty()
    val fields = if (isNotEmpty) {
      ImmutableList((0 until idx).map { index -> row[index] })
    } else {
      EMPTY
    }

    return CsvRow(originalLineNumber, header, fields, isCommentMode, !!!isNotEmpty)
  }

  fun enableCommentMode() {
    isCommentMode = true
  }

  fun incLines() {
    lines++
  }
}