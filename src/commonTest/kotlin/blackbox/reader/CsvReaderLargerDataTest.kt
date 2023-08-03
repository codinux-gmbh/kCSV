package blackbox.reader

import net.codinux.csv.reader.CsvReader
import net.codinux.csv.writer.CsvWriter
import net.codinux.csv.writer.datawriter.StringBuilderDataWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class CsvReaderLargerDataTest {
  @Test
  fun largerData() {
    val reader = CsvReader(createSampleCSV())
    var i = 0
    for (row in reader) {
      assertEquals(6, row.fieldCount)
      assertEquals(TEXTS[0], row.getField(0))
      assertEquals(TEXTS[1], row.getField(1))
      assertEquals(TEXTS[2], row.getField(2))
      assertEquals(TEXTS[3], row.getField(3))
      assertEquals(TEXTS[4], row.getField(4))
      assertEquals(TEXTS[5], row.getField(5))
      i++
    }
    assertEquals(1000, i)
  }

  private fun createSampleCSV(): String {
    val sw = StringBuilder()
    val writer = CsvWriter.builder().build(StringBuilderDataWriter(sw))
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