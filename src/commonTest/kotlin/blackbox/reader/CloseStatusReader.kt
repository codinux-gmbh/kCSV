package blackbox.reader

import net.codinux.csv.reader.datareader.DataReader

internal class CloseStatusReader(private val reader: DataReader) : DataReader {

  var isClosed = false
    private set

  override val areAllDataBuffered = reader.areAllDataBuffered

  override fun getBufferedData() = reader.getBufferedData()

  override fun read(buffer: CharArray, offset: Int, length: Int) = reader.read(buffer, offset, length)

  override fun close() {
    reader.close()
    isClosed = true
  }
}