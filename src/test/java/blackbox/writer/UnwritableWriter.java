package blackbox.writer

import java.io.IOException
import java.io.Writer

internal class UnwritableWriter : Writer() {
  @Throws(IOException::class)
  override fun write(cbuf: CharArray, off: Int, len: Int) {
    throw IOException("Cannot write")
  }

  @Throws(IOException::class)
  override fun flush() {
    throw IOException("Cannot flush")
  }

  @Throws(IOException::class)
  override fun close() {
    throw IOException("Cannot close")
  }
}