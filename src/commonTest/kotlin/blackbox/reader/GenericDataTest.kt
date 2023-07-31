package blackbox.reader

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.codinux.csv.kcsv.reader.CommentStrategy
import net.codinux.csv.kcsv.reader.CsvReader

class GenericDataTest : FunSpec({

  DataProvider.loadTestData().forEachIndexed { index, data ->
    test("[$index] ${data.line}") {
      val expected = CharacterConv.print(data.expected)
      val commentStrategy = if (data.isReadComments) CommentStrategy.READ else if (data.isSkipComments) CommentStrategy.SKIP else CommentStrategy.NONE

      val actual = CharacterConv.print(
        readAll(
          CharacterConv.parse(data.input), data.isSkipEmptyLines,
          commentStrategy
        )
      )

      actual.shouldBe(expected)
    }
  }

}) {

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
        .map { it.fields }
    }

  }
}