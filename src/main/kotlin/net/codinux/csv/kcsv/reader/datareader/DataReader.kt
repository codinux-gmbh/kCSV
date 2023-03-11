package net.codinux.csv.kcsv.reader.datareader

interface DataReader {

  companion object {

    fun reader(data: String) = StringDataReader(data)

  }


  val areAllDataBuffered: Boolean

  fun getBufferedData(): CharArray?

  fun read(buffer: CharArray, offset: Int, length: Int): Int

  fun close()

}