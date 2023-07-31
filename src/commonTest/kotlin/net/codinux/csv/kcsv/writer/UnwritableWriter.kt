package net.codinux.csv.kcsv.writer

import net.codinux.csv.kcsv.IOException
import net.codinux.csv.kcsv.writer.datawriter.DataWriter

internal class UnwritableWriter : DataWriter {

  override fun write(char: Char) {
    throw IOException("Cannot write")
  }

  override fun write(charArray: CharArray, offset: Int, length: Int) {
    throw IOException("Cannot write")
  }

  override fun write(string: String, offset: Int, length: Int) {
    throw IOException("Cannot write")
  }

  override fun flush() {
    throw IOException("Cannot flush")
  }

  override fun close() {
    throw IOException("Cannot close")
  }
}