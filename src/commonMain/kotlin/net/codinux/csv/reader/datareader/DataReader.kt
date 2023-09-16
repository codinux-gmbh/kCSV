package net.codinux.csv.reader.datareader

import net.codinux.csv.Closeable

internal interface DataReader : Closeable {

  companion object {

    fun reader(data: String) = StringDataReader(data)

  }


  val areAllDataBuffered: Boolean

  fun getBufferedData(): CharArray?

  fun read(buffer: CharArray, offset: Int, length: Int): Int

}