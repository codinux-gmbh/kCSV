package net.codinux.csv.writer

import net.codinux.csv.writer.CsvWriter.FastBufferedWriter
import net.codinux.csv.writer.datawriter.StringBuilderDataWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class FastBufferedWriterTest {
  private val sw = StringBuilder()
  private val cw = FastBufferedWriter(StringBuilderDataWriter(sw), 8192)

  @Test
  fun appendSingle() {
    val sb = StringBuilder()
    for (i in 0..8191) {
      sb.append("ab")
      cw.write('a')
      cw.write('b')
    }
    cw.close()
    assertEquals(sb.toString(), sw.toString())
  }

  @Test
  fun appendArray() {
    val sb = StringBuilder()
    for (i in 0..8191) {
      sb.append("ab")
      cw.write("ab", 0, 2)
    }
    cw.close()
    assertEquals(sb.toString(), sw.toString())
  }

  @Test
  fun appendLarge() {
    val sb = buildLargeData()
    cw.write(sb, 0, sb.length)
    assertEquals(sb, sw.toString())
  }

  @Test
  fun unreachable() {
    // TODO:
//    assertDoesNotThrow { cw.write(CharArray(0), 0, 0) }
  }

  private fun buildLargeData(): String {
    val sb = StringBuilder()
    for (i in 0..8191) {
      sb.append("ab")
    }
    return sb.toString()
  }
}