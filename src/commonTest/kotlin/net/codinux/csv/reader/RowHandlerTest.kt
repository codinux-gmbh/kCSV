package net.codinux.csv.reader

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RowHandlerTest {

  @Test
  fun materializeAndAddValue() {
    val rh = RowHandler(1, false, false, emptySet(), false)
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

  @Test
  fun ignoreColumns() {
    val rh = RowHandler(1, false, false, setOf(0, 2), false)
    val buffer ="foo,bar,baz".toCharArray()

    rh.add(buffer, 0, 3, 0, '"')
    rh.add(buffer, 4, 7, 0, '"')
    rh.add(buffer, 8, 11, 0, '"')

    val csvRow = rh.buildAndReset()
    assertNotNull(csvRow)
    assertEquals(
      "CsvRow[originalLineNumber=1, fields=[, bar, ], comment=false]",
      csvRow.toString()
    )
  }

  @Test
  fun doNotIgnoreColumnsOfHeaderRow() {
    val rh = RowHandler(1, true, false, setOf(0, 2), false)
    val buffer ="Header1,Header2,Header3\nfoo,bar,baz".toCharArray()

    // in the header row first and third columns are not ignored even though we set to ignore these columns via ignoreColumns parameter
    rh.add(buffer, 0, 7, 0, '"')
    rh.add(buffer, 8, 15, 0, '"')
    rh.add(buffer, 16, 23, 0, '"')

    val headerRow = rh.buildAndReset()
    assertNotNull(headerRow)
    assertEquals(
      "CsvRow[originalLineNumber=1, fields=[Header1, Header2, Header3], comment=false]",
      headerRow.toString()
    )

    // whilest in second row first and third column are ignored
    rh.add(buffer, 24, 27, 0, '"')
    rh.add(buffer, 28, 31, 0, '"')
    rh.add(buffer, 32, 35, 0, '"')

    val secondRow = rh.buildAndReset()
    assertNotNull(secondRow)
    assertEquals(
      "CsvRow[originalLineNumber=2, fields=[, bar, ], comment=false]",
      secondRow.toString()
    )
  }
}