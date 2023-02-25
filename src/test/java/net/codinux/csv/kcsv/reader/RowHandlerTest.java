package net.codinux.csv.kcsv.reader

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RowHandlerTest {
  @Test
  fun test() {
    val rh = RowHandler(1)
    rh.add("foo")
    rh.add("bar")
    val csvRow = rh.buildAndReset()
    Assertions.assertNotNull(csvRow)
    Assertions.assertEquals(
      "CsvRow[originalLineNumber=1, fields=[foo, bar], comment=false]",
      csvRow.toString()
    )
  }
}