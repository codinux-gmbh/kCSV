package blackbox.reader

import java.io.IOException
import java.io.Reader

internal class UnreadableReader : Reader() {

  override fun read(cbuf: CharArray, off: Int, len: Int): Int {
    throw IOException("Cannot read")
  }

  override fun close() {
    throw IOException("Cannot close")
  }
}