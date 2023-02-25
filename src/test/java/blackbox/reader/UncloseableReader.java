package blackbox.reader

import java.io.IOException
import java.io.Reader

internal class UncloseableReader(private val reader: Reader) : Reader() {
  @Throws(IOException::class)
  override fun read(cbuf: CharArray, off: Int, len: Int): Int {
    return reader.read()
  }

  @Throws(IOException::class)
  override fun close() {
    throw IOException("Cannot close")
  }
}