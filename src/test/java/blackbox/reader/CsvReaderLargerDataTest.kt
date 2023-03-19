package blackbox.reader

import net.codinux.csv.kcsv.reader.CsvReader
import net.codinux.csv.kcsv.writer.CsvWriter
import net.codinux.csv.kcsv.writer.build
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.StringWriter

class CsvReaderLargerDataTest {
  @Test
  fun largerData() {
    val reader = CsvReader(createSampleCSV())
    var i = 0
    for (row in reader) {
      Assertions.assertEquals(6, row.getFieldCount())
      Assertions.assertEquals(TEXTS[0], row.getField(0))
      Assertions.assertEquals(TEXTS[1], row.getField(1))
      Assertions.assertEquals(TEXTS[2], row.getField(2))
      Assertions.assertEquals(TEXTS[3], row.getField(3))
      Assertions.assertEquals(TEXTS[4], row.getField(4))
      Assertions.assertEquals(TEXTS[5], row.getField(5))
      i++
    }
    Assertions.assertEquals(1000, i)
  }

  private fun createSampleCSV(): String {
    val sw = StringWriter()
    val writer = CsvWriter.builder().build(sw)
    for (i in 0..999) {
      writer.writeRow(*TEXTS)
    }
    return sw.toString()
  }

  companion object {
    private val TEXTS = arrayOf(
      "Lorem ipsum dolor sit amet",
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor "
        + "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud "
        + "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute "
        + "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla "
        + "pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia "
        + "deserunt mollit anim id est laborum.",
      "Lorem ipsum \"dolor\" sit amet",
      "Lorem ipsum dolor\rsit amet",
      "Lorem ipsum dolor\r\n sit amet",
      "Lorem ipsum dolor\n sit amet"
    )
  }
}