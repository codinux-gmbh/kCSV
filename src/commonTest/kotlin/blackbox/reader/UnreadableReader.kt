package blackbox.reader

import net.codinux.csv.IOException
import net.codinux.csv.reader.datareader.DataReader

internal class UnreadableReader : DataReader {

  override val areAllDataBuffered = false

  override fun getBufferedData() = null

  override fun read(buffer: CharArray, offset: Int, length: Int): Int {
    throw IOException("Cannot read")
  }

  override fun close() {
    throw IOException("Cannot close")
  }
}