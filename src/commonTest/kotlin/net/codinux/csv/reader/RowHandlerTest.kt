package net.codinux.csv.reader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RowHandlerTest {
  @Test
  fun test() {
    val rh = RowHandler(1, false, emptySet(), false)
    val buffer ="foo,bar".toCharArray()

    rh.add(buffer, 0, 3, 0, '"')
    rh.add(buffer, 4, 7, 0, '"')

    val csvRow = rh.buildAndReset()
    assertNotNull(csvRow)
    assertEquals(
      "CsvRow[originalLineNumber=1, fields=[foo, bar], comment=false]",
      csvRow.toString()
    )
  }
}