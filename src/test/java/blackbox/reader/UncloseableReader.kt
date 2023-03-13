package blackbox.reader

import net.codinux.csv.kcsv.IOException
import java.io.Reader

internal class UncloseableReader(private val reader: Reader) : Reader() {

  override fun read(cbuf: CharArray, off: Int, len: Int): Int {
    return reader.read()
  }

  override fun close() {
    throw IOException("Cannot close")
  }
}