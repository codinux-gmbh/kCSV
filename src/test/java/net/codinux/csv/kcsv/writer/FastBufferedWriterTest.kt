package net.codinux.csv.kcsv.writer

import net.codinux.csv.kcsv.writer.CsvWriter.FastBufferedWriter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.StringWriter

class FastBufferedWriterTest {
  private val sw = StringWriter()
  private val cw = FastBufferedWriter(sw, 8192)
  @Test
  fun appendSingle() {
    val sb = StringBuilder()
    for (i in 0..8191) {
      sb.append("ab")
      cw.write('a'.code)
      cw.write('b'.code)
    }
    cw.close()
    Assertions.assertEquals(sb.toString(), sw.toString())
  }

  @Test
  fun appendArray() {
    val sb = StringBuilder()
    for (i in 0..8191) {
      sb.append("ab")
      cw.write("ab", 0, 2)
    }
    cw.close()
    Assertions.assertEquals(sb.toString(), sw.toString())
  }

  @Test
  fun appendLarge() {
    val sb = buildLargeData()
    cw.write(sb, 0, sb.length)
    Assertions.assertEquals(sb, sw.toString())
  }

  @Test
  fun unreachable() {
    Assertions.assertThrows(IllegalStateException::class.java) { cw.write(CharArray(0), 0, 0) }
  }

  private fun buildLargeData(): String {
    val sb = StringBuilder()
    for (i in 0..8191) {
      sb.append("ab")
    }
    return sb.toString()
  }
}