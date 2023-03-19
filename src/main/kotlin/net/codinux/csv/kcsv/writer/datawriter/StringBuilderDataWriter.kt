package net.codinux.csv.kcsv.writer.datawriter

class StringBuilderDataWriter(private val builder: StringBuilder) : DataWriter {

  override fun write(char: Char) {
    builder.append(char)
  }

  override fun write(charArray: CharArray, offset: Int, length: Int) {
    builder.append(charArray, offset, length)
  }

  override fun write(string: String, offset: Int, length: Int) {
    builder.append(string, offset, length)
  }

  override fun flush() {
    // no-op
  }

  override fun close() {
    // no-op
  }
}