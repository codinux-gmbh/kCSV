package net.codinux.csv.reader

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

  override fun close() {
    csvIterator.close()
  }
}