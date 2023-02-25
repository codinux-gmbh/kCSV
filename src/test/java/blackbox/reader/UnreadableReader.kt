package blackbox.reader

import java.io.IOException
import java.io.Reader

internal class UnreadableReader : Reader() {
  @Throws(IOException::class)
  override fun read(cbuf: CharArray, off: Int, len: Int): Int {
    throw IOException("Cannot read")
  }

  @Throws(IOException::class)
  override fun close() {
    throw IOException("Cannot close")
  }
}