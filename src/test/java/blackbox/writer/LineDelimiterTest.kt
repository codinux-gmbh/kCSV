package blackbox.writer

import net.codinux.csv.kcsv.writer.LineDelimiter
import net.codinux.csv.kcsv.writer.LineDelimiter.Companion.of
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LineDelimiterTest {
  @Test
  fun test() {
    Assertions.assertEquals("\n", LineDelimiter.LF.toString())
    Assertions.assertEquals("\r", LineDelimiter.CR.toString())
    Assertions.assertEquals("\r\n", LineDelimiter.CRLF.toString())
    Assertions.assertEquals(System.lineSeparator(), LineDelimiter.PLATFORM.toString())
  }

  @Test
  fun testOf() {
    Assertions.assertEquals(LineDelimiter.CRLF, of("\r\n"))
    Assertions.assertEquals(LineDelimiter.LF, of("\n"))
    Assertions.assertEquals(LineDelimiter.CR, of("\r"))
    val e = Assertions.assertThrows(IllegalArgumentException::class.java) { of(";") }
    Assertions.assertEquals("Unknown line delimiter: ;", e.message)
  }
}