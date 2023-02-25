package blackbox.reader

import blackbox.reader.DataProvider.TestData
import net.codinux.csv.kcsv.reader.CommentStrategy
import net.codinux.csv.kcsv.reader.CsvReader
import net.codinux.csv.kcsv.reader.CsvRow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.IOException
import java.util.stream.Collectors

class GenericDataTest {
  @ParameterizedTest
  @MethodSource("dataProvider")
  fun dataTest(data: TestData) {
    val expected = CharacterConv.print(data.expected)
    val commentStrategy = if (data.isReadComments) CommentStrategy.READ else if (data.isSkipComments) CommentStrategy.SKIP else CommentStrategy.NONE
    val actual = CharacterConv.print(
      readAll(
        CharacterConv.parse(data.input), data.isSkipEmptyLines,
        commentStrategy
      )
    )
    Assertions.assertEquals(expected, actual) { String.format("Error in line: '%s'", data) }
  }

  companion object {
    @JvmStatic
    @Throws(IOException::class)
    fun dataProvider(): List<TestData?>? {
      return DataProvider.loadTestData("/test.txt")
    }

    fun readAll(
      data: String?, skipEmptyLines: Boolean,
      commentStrategy: CommentStrategy?
    ): List<List<String?>> {
      return CsvReader.builder()
        .skipEmptyRows(skipEmptyLines)
        .commentCharacter(';')
        .commentStrategy(commentStrategy!!)
        .build(data!!)
        .stream()
        .map<List<String?>> { obj: CsvRow -> obj.getFields() }
        .collect(Collectors.toList())
    }
  }
}