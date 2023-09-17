package net.codinux.csv.writer.datawriter

import java.io.Closeable
import java.lang.Appendable
import java.lang.StringBuilder

internal class JavaAppendableDataWriter(private val appendable: Appendable) : DataWriter {

  override fun write(char: Char) {
    appendable.append(char)
  }

  override fun write(charArray: CharArray, offset: Int, length: Int) {
    if (appendable is StringBuilder) {
      appendable.appendRange(charArray, offset, offset + length)
    } else {
      appendable.append(charArray.concatToString(offset, offset + length))
    }
  }

  fun write(string: String) {
    appendable.append(string)
  }

  override fun write(string: String, offset: Int, length: Int) {
    appendable.append(string, offset, length)
  }

  override fun flush() {
    // no-op
  }

  override fun close() {
    // no-op
    (appendable as? Closeable)?.close()
  }

  override fun toString() = appendable.toString()

}