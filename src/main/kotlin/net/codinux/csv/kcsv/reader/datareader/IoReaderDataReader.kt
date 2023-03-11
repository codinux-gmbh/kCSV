package net.codinux.csv.kcsv.reader.datareader

import java.io.Reader

class IoReaderDataReader(private val reader: Reader) : DataReader {

  override val areAllDataBuffered: Boolean = false

  override fun getBufferedData() = null

  override fun read(buffer: CharArray, offset: Int, length: Int) = reader.read(buffer, offset, length)

  override fun close() {
    reader.close()
  }

}