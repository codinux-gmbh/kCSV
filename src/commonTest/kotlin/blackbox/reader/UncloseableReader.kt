package blackbox.reader

import net.codinux.csv.IOException
import net.codinux.csv.reader.datareader.DataReader

internal class UncloseableReader(private val reader: DataReader) : DataReader {

  override val areAllDataBuffered = reader.areAllDataBuffered

  override fun getBufferedData() = reader.getBufferedData()

  override fun read(buffer: CharArray, offset: Int, length: Int) = reader.read(buffer, offset, length)

  override fun close() {
    throw IOException("Cannot close")
  }

}