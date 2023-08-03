package net.codinux.csv.writer.datawriter

class StringBuilderDataWriter(private val builder: StringBuilder) : DataWriter {

  override fun write(char: Char) {
    builder.append(char)
  }

  override fun write(charArray: CharArray, offset: Int, length: Int) {
    builder.append(charArray.concatToString(offset, offset + length))
  }

  fun write(string: String) {
    builder.append(string)
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

  override fun toString() = builder.toString()

}