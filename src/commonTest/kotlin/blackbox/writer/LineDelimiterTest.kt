package blackbox.writer

import net.codinux.csv.writer.LineDelimiter
import net.codinux.csv.writer.LineDelimiter.Companion.of
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LineDelimiterTest {
  @Test
  fun test() {
    assertEquals("\n", LineDelimiter.LF.toString())
    assertEquals("\r", LineDelimiter.CR.toString())
    assertEquals("\r\n", LineDelimiter.CRLF.toString())
//    Assertions.assertEquals(System.lineSeparator(), LineDelimiter.PLATFORM.toString())
  }

  @Test
  fun testOf() {
    assertEquals(LineDelimiter.CRLF, of("\r\n"))
    assertEquals(LineDelimiter.LF, of("\n"))
    assertEquals(LineDelimiter.CR, of("\r"))
    val e = assertFailsWith(IllegalArgumentException::class) { of(";") }
    assertEquals("Unknown line delimiter: ;", e.message)
  }
}