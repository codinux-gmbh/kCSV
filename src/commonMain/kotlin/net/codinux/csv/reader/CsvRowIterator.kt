package net.codinux.csv.reader

import net.codinux.csv.IOException
import net.codinux.csv.UncheckedIOException
import kotlin.NoSuchElementException

class CsvRowIterator(
  private val rowReader: RowReader,
  private val commentStrategy: CommentStrategy,
  private val skipEmptyRows: Boolean,
  private val errorOnDifferentFieldCount: Boolean
) : CloseableIterator<CsvRow> {

  private var fetchedRow: CsvRow? = null
  private var fetched = false
  private var firstLineFieldCount = -1

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