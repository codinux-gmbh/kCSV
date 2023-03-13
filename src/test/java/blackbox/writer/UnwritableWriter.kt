package blackbox.writer

import net.codinux.csv.kcsv.IOException
import java.io.Writer

internal class UnwritableWriter : Writer() {

  override fun write(cbuf: CharArray, off: Int, len: Int) {
    throw IOException("Cannot write")
  }

  override fun flush() {
    throw IOException("Cannot flush")
  }

  override fun close() {
    throw IOException("Cannot close")
  }
}