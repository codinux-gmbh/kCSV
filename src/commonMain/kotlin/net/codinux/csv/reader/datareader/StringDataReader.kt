package net.codinux.csv.reader.datareader

internal class StringDataReader(private val data: CharArray) : DataReader {

  constructor(data: String) : this(data.toCharArray())

  override val areAllDataBuffered = true

  override fun getBufferedData() = data

  override fun read(buffer: CharArray, offset: Int, length: Int): Int {
    // no-op, all data are returned by getBufferedData
    return -1
  }

  override fun close() {
    // nothing to do
  }

}