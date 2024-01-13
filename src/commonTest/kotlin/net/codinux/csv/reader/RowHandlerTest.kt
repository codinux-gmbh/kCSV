package net.codinux.csv.reader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RowHandlerTest {
  @Test
  fun test() {
    val rh = RowHandler(1, false, false)
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