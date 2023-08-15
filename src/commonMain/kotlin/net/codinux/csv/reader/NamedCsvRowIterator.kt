package net.codinux.csv.reader

class NamedCsvRowIterator(
  private val csvIterator: CsvRowIterator
) : CloseableIterator<NamedCsvRow>, Iterable<NamedCsvRow> {

  val header = csvIterator.header

  override fun iterator(): CloseableIterator<NamedCsvRow> = this

  override fun hasNext() = csvIterator.hasNext()

  override fun next(): NamedCsvRow =
    NamedCsvRow(header, csvIterator.next())

  override fun close() = csvIterator.close()

}