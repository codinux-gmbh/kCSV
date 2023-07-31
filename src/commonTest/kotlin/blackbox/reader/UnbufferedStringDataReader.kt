package blackbox.reader

import net.codinux.csv.kcsv.reader.datareader.DataReader

/**
 * Just for test reasons, a version of [net.codinux.csv.kcsv.reader.datareader.StringDataReader] that returns false
 * for [areAllDataBuffered] so that [read] gets called instead of [getBufferedData].
 */
class UnbufferedStringDataReader(private val data: CharArray) : DataReader {

  constructor(data: String) : this(data.toCharArray())

  override val areAllDataBuffered = false

  override fun getBufferedData() = null

  override fun read(buffer: CharArray, offset: Int, length: Int): Int {
    for (i in offset until offset + length) {
      buffer[i] = data[i]
    }

    return length
  }

  override fun close() {
    // nothing to do
  }

}