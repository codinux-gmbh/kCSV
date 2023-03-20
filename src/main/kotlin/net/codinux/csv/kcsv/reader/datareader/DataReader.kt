package net.codinux.csv.kcsv.reader.datareader

import net.codinux.csv.kcsv.Closeable

interface DataReader : Closeable {

  companion object {

    fun reader(data: String) = StringDataReader(data)

  }


  val areAllDataBuffered: Boolean

  fun getBufferedData(): CharArray?

  fun read(buffer: CharArray, offset: Int, length: Int): Int

}