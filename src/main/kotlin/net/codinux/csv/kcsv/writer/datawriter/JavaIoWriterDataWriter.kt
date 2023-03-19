package net.codinux.csv.kcsv.writer.datawriter

import java.io.Writer

class JavaIoWriterDataWriter(private val writer: Writer) : DataWriter {

  override fun write(char: Char) {
    writer.write(char.code)
  }

  override fun write(charArray: CharArray, offset: Int, length: Int) {
    writer.write(charArray, offset, length)
  }

  override fun write(string: String, offset: Int, length: Int) {
    writer.write(string, offset, length)
  }

  override fun flush() {
    writer.flush()
  }

  override fun close() {
    writer.close()
  }

}