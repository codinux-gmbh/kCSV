package net.codinux.csv.reader

internal class RowHandler(
  private var len: Int,
  private val hasHeaderRow: Boolean,
  private val reuseRowInstance: Boolean,
  ignoreColumns: Set<Int>,
  private val ignoreInvalidQuoteChars: Boolean
) {

  private var row: Array<String> = Array(len) { "" }
  private var idx = 0
  private var lines = 1
  var isCommentMode = false
    private set
  private var originalLineNumber: Long = 1
  private val ignoreColumns: IntArray = ignoreColumns.toIntArray()

  internal var header: Set<String> = ImmutableSet(emptySet())
    set(value) {
      field = value
      reusedCsvRowInstance = CsvRow.empty(header, originalLineNumber, isCommentMode)
    }

  private var reusedCsvRowInstance = CsvRow.empty(header, originalLineNumber, isCommentMode)


  fun add(lBuf: CharArray, lBegin: Int, lPos: Int, lStatus: Int, quoteCharacter: Char) {
    checkCapacity()

    if (ignoreColumn()) {
      if (hasHeaderRow && originalLineNumber == 2L) { // replace column's header value of last row (= header) with empty value
        addEmptyField()
      } else {
        idx++
      }
    } else {
      add(materialize(lBuf, lBegin, lPos, lStatus, quoteCharacter))
    }
  }

  fun addEmptyField() {
    add("")
  }

  private fun add(value: String) {
    checkCapacity()

    row[idx++] = value
  }

  private fun checkCapacity() {
    if (idx == len) {
      extendCapacity()
    }
  }

  private fun ignoreColumn(): Boolean {
    if (ignoreColumns.isEmpty() || (hasHeaderRow && originalLineNumber < 2)) { // read all fields of header row // TODO: why?
      return false
    }

    // couldn't believe it, but iterating over an array per index is way faster than Set<Int>.contains(idx)
    @Suppress("ReplaceManualRangeWithIndicesCalls")
    for (i in 0 until ignoreColumns.size) {
      if (ignoreColumns[i] == idx) {
        return true
      }
    }

    return false
  }

  private fun materialize(lBuf: CharArray, lBegin: Int, lPos: Int, lStatus: Int, quoteCharacter: Char): String {
    if (lStatus and RowReader.STATUS_QUOTED_COLUMN == 0) { // column without quotes
      return lBuf.concatToString(lBegin, lPos)
    }

    // column with quotes
    val shift = if (ignoreInvalidQuoteChars) {
      1
    } else {
      cleanDelimiters(lBuf, lBegin + 1, lPos, quoteCharacter)
    }

    return lBuf.concatToString(lBegin + 1, lPos - shift)
  }

  private fun cleanDelimiters(
    buf: CharArray, begin: Int, pos: Int,
    quoteCharacter: Char
  ): Int {
    var shift = 0
    var escape = false

    for (i in begin until pos) {
      val c = buf[i]
      if (c == quoteCharacter) {
        if (!escape) {
          shift++
          escape = true
          continue
        } else {
          escape = false
        }
      }

      if (shift > 0) {
        buf[i - shift] = c
      }
    }

    return shift
  }

  private fun extendCapacity() {
    len *= 2
    // System.arraycopy(row, 0, newRow, 0, idx);
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
      reusedCsvRowInstance.updateRow(row, idx, originalLineNumber, isCommentMode, isEmpty)
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