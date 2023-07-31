package net.codinux.csv.kcsv.reader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RowHandlerTest {
  @Test
  fun test() {
    val rh = RowHandler(1)
    rh.add("foo")
    rh.add("bar")
    val csvRow = rh.buildAndReset()
    assertNotNull(csvRow)
    assertEquals(
      "CsvRow[originalLineNumber=1, fields=[foo, bar], comment=false]",
      csvRow.toString()
    )
  }
}