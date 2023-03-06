package net.codinux.csv.kcsv.reader

import java.io.IOException

class NamedCsvRowIterator(
  private val csvIterator: CloseableIterator<CsvRow>,
  private val header: Set<String>
) : CloseableIterator<NamedCsvRow> {
  override fun hasNext(): Boolean {
    return csvIterator.hasNext()
  }

  override fun next(): NamedCsvRow {
    return NamedCsvRow(header, csvIterator.next())
  }

  @Throws(IOException::class)
  override fun close() {
    csvIterator.close()
  }
}