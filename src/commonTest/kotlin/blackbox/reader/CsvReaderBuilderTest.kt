package blackbox.reader

import net.codinux.csv.reader.CommentStrategy
import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.CsvRow
import test.assertElementsEqual
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CsvReaderBuilderTest {

  private val crb = CsvReader.builder()

  @Test
  fun nullInput() {
    val text: String? = null
    assertFailsWith(NullPointerException::class) {
      crb.build().read(text!!)
    }
  }

  @Test
  fun fieldSeparator() {
    val it: Iterator<CsvRow> = crb.fieldSeparator(';')
      .build().read("foo,bar;baz").iterator()
    assertElementsEqual(listOf("foo,bar", "baz"), it.next().fields)
  }

  @Test
  fun quoteCharacter() {
    val it: Iterator<CsvRow> = crb.quoteCharacter('_')
      .build().read("_foo \", __ bar_,foo \" bar").iterator()
    assertElementsEqual(listOf("foo \", _ bar", "foo \" bar"), it.next().fields)
  }

  @Test
  fun commentSkip() {
    val it: Iterator<CsvRow> = crb.commentCharacter(';').commentStrategy(CommentStrategy.SKIP)
      .build().read("#foo\n;bar\nbaz").iterator()
    assertElementsEqual(listOf("#foo"), it.next().fields)
    assertElementsEqual(listOf("baz"), it.next().fields)
  }

  @Test
  fun builderToString() {
    assertEquals(
      "CsvReaderBuilder[fieldSeparator=,, quoteCharacter=\", "
        + "commentStrategy=NONE, commentCharacter=#, skipEmptyRows=true, "
        + "errorOnDifferentFieldCount=false, hasHeaderRow=false]", crb.toString()
    )
  }

  @Test
  fun reader() {
    val list = crb.build().read(DATA).toList()

    assertElementsEqual(EXPECTED, list[0].fields)
  }

  @Test
  fun string() {
    val list = crb.build().read(DATA).toList()

    assertElementsEqual(EXPECTED, list[0].fields)
  }

  @Test
  fun chained() {
    val reader = CsvReader.builder()
      .fieldSeparator(',')
      .quoteCharacter('"')
      .commentStrategy(CommentStrategy.NONE)
      .commentCharacter('#')
      .skipEmptyRows(true)
      .errorOnDifferentFieldCount(false)
      .build()
      .read("foo")

    assertNotNull(reader)
  }

  companion object {
    private const val DATA = "foo,bar\n"
    private val EXPECTED: List<String> = mutableListOf("foo", "bar")
  }
}