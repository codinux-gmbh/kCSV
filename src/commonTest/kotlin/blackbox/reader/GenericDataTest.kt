package blackbox.reader

import blackbox.reader.DataProvider.TestData
import net.codinux.csv.kcsv.reader.CommentStrategy
import net.codinux.csv.kcsv.reader.CsvReader
import kotlin.test.*

class GenericDataTest {

  @Test
  fun dataTest() {
    val allData = DataProvider.loadTestData()
    allData.forEach { data ->
      assertTestData(data)
    }
  }

  private fun assertTestData(data: TestData) {
    val expected = CharacterConv.print(data.expected)
    val commentStrategy = if (data.isReadComments) CommentStrategy.READ else if (data.isSkipComments) CommentStrategy.SKIP else CommentStrategy.NONE
    val actual = CharacterConv.print(
      readAll(
        CharacterConv.parse(data.input), data.isSkipEmptyLines,
        commentStrategy
      )
    )
    assertEquals(expected, actual, "Error in line: '$data'" )
  }

  companion object {

    fun readAll(
      data: String?, skipEmptyLines: Boolean,
      commentStrategy: CommentStrategy?
    ): List<List<String?>> {
      return CsvReader.builder()
        .skipEmptyRows(skipEmptyLines)
        .commentCharacter(';')
        .commentStrategy(commentStrategy!!)
        .build(data!!)
        .map { it.getFields() }
    }
  }
}