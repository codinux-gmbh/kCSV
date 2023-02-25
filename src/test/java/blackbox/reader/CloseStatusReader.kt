package blackbox.reader

import java.io.IOException
import java.io.Reader

internal class CloseStatusReader(private val reader: Reader) : Reader() {
  var isClosed = false
    private set

  @Throws(IOException::class)
  override fun read(cbuf: CharArray, off: Int, len: Int): Int {
    return reader.read()
  }

  @Throws(IOException::class)
  override fun close() {
    reader.close()
    isClosed = true
  }
}