package blackbox.reader

import net.codinux.csv.kcsv.reader.*
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
      crb.build(text!!)
    }
  }

  @Test
  fun fieldSeparator() {
    val it: Iterator<CsvRow> = crb.fieldSeparator(';')
      .build("foo,bar;baz").iterator()
    assertElementsEqual(listOf("foo,bar", "baz"), it.next().fields)
  }

  @Test
  fun quoteCharacter() {
    val it: Iterator<CsvRow> = crb.quoteCharacter('_')
      .build("_foo \", __ bar_,foo \" bar").iterator()
    assertElementsEqual(listOf("foo \", _ bar", "foo \" bar"), it.next().fields)
  }

  @Test
  fun commentSkip() {
    val it: Iterator<CsvRow> = crb.commentCharacter(';').commentStrategy(CommentStrategy.SKIP)
      .build("#foo\n;bar\nbaz").iterator()
    assertElementsEqual(listOf("#foo"), it.next().fields)
    assertElementsEqual(listOf("baz"), it.next().fields)
  }

  @Test
  fun builderToString() {
    assertEquals(
      "CsvReaderBuilder[fieldSeparator=,, quoteCharacter=\", "
        + "commentStrategy=NONE, commentCharacter=#, skipEmptyRows=true, "
        + "errorOnDifferentFieldCount=false, hasHeader=false]", crb.toString()
    )
  }

  @Test
  fun reader() {
    val list = crb.build(DATA).toList()

    assertElementsEqual(EXPECTED, list[0].fields)
  }

  @Test
  fun string() {
    val list = crb.build(DATA).toList()

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
      .build("foo")

    assertNotNull(reader)
  }

  companion object {
    private const val DATA = "foo,bar\n"
    private val EXPECTED: List<String> = mutableListOf("foo", "bar")
  }
}