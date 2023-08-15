package net.codinux.csv.reader

import net.codinux.csv.IOException
import net.codinux.csv.UncheckedIOException
import net.codinux.csv.reader.datareader.DataReader

class CsvRowIterator(
  reader: DataReader,
  fieldSeparator: Char,
  quoteCharacter: Char,
  private val commentStrategy: CommentStrategy,
  commentCharacter: Char,
  private val skipEmptyRows: Boolean,
  private val errorOnDifferentFieldCount: Boolean,
  hasHeaderRow: Boolean,
  ignoreInvalidQuoteChars: Boolean
) : CloseableIterator<CsvRow>, Iterable<CsvRow> {

  companion object {
    private val CsvHasNoHeader = ImmutableSet(emptySet<String>())
  }


  private var fetchedRow: CsvRow? = null
  private var fetched = false
  private var firstLineFieldCount = -1

  private val rowReader = RowReader(
    reader, fieldSeparator, quoteCharacter, commentStrategy,
    commentCharacter, ignoreInvalidQuoteChars
  )

  val header: Set<String> = readHeader(hasHeaderRow)

  private fun readHeader(hasHeaderRow: Boolean) =
    if (hasHeaderRow == false || hasNext() == false) {
      CsvHasNoHeader
    } else {
      val firstRow = next()
      val headerSet: MutableSet<String> = LinkedHashSet(firstRow.fieldCount)
      for (field in firstRow.fields) {
        check(headerSet.add(field)) { "Duplicate header field '$field' found" }
      }

      ImmutableSet(headerSet)
    }


  override fun iterator(): CloseableIterator<CsvRow> {
    return this
  }

  override fun hasNext(): Boolean {
    if (!fetched) {
      fetch()
    }
    return fetchedRow != null
  }

  override fun next(): CsvRow {
    if (!fetched) {
      fetch()
    }

    fetchedRow?.let { row ->
      fetched = false
      return row
    }

    throw NoSuchElementException()
  }

  private fun fetch() {
    try {
      fetchedRow = fetchRow()

      fetched = true
    } catch (e: IOException) {
      val lastFetchedRow = fetchedRow
      if (lastFetchedRow != null) {
        throw UncheckedIOException("IOException when reading record that started in line ${lastFetchedRow.originalLineNumber + 1}", e)
      } else {
        throw UncheckedIOException("IOException when reading first record", e)
      }
    }
  }

  private fun fetchRow(): CsvRow? {
    while (true) {
      val csvRow = rowReader.fetchAndRead() ?: break
      // skip commented rows
      if (commentStrategy == CommentStrategy.SKIP && csvRow.isComment) {
        continue
      }

      // skip empty rows
      if (csvRow.isEmpty) {
        if (skipEmptyRows) {
          continue
        }
      } else if (errorOnDifferentFieldCount) {
        val fieldCount = csvRow.fieldCount

        // check the field count consistency on every row
        if (firstLineFieldCount == -1) {
          firstLineFieldCount = fieldCount
        } else if (fieldCount != firstLineFieldCount) {
          throw MalformedCsvException(
              "Row ${csvRow.originalLineNumber} has $fieldCount fields, but first row had $firstLineFieldCount fields"
          )
        }
      }
      return csvRow
    }

    return null
  }

  override fun close() {
    rowReader.close()
  }
}