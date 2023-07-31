package blackbox.reader

import net.codinux.csv.kcsv.reader.NamedCsvReader
import net.codinux.csv.kcsv.reader.NamedCsvRow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class NamedCsvReaderBuilderTest {

  private val crb = NamedCsvReader.builder()

  @Test
  fun nullInput() {
    val text: String? = null

    assertFailsWith(NullPointerException::class) {
      crb.build(text!!)
    }
  }

  @Test
  fun fieldSeparator() {
    val it: Iterator<NamedCsvRow> = crb.fieldSeparator(';')
      .build("h1;h2\nfoo,bar;baz").iterator()
    assertEquals("{h1=foo,bar, h2=baz}", it.next().fields.toString())
  }

  @Test
  fun quoteCharacter() {
    val it: Iterator<NamedCsvRow> = crb.quoteCharacter('_')
      .build("h1,h2\n_foo \", __ bar_,foo \" bar").iterator()
    assertEquals("{h1=foo \", _ bar, h2=foo \" bar}", it.next().fields.toString())
  }

  @Test
  fun commentSkip() {
    val it: Iterator<NamedCsvRow> = crb.commentCharacter(';').skipComments(true)
      .build("h1\n#foo\n;bar\nbaz").iterator()
    assertEquals("{h1=#foo}", it.next().fields.toString())
    assertEquals("{h1=baz}", it.next().fields.toString())
  }

  @Test
  fun builderToString() {
    assertEquals(
      "NamedCsvReaderBuilder[fieldSeparator=,, quoteCharacter=\", "
        + "commentCharacter=#, skipComments=false]", crb.toString()
    )
  }

  @Test
  fun reader() {
    val list = crb.build(DATA).toList()

    assertEquals(EXPECTED, list[0].fields.toString())
  }

  @Test
  fun string() {
    val list = crb.build(DATA).toList()

    assertEquals(EXPECTED, list[0].fields.toString())
  }

  @Test
  fun chained() {
    val reader = NamedCsvReader.builder()
      .fieldSeparator(',')
      .quoteCharacter('"')
      .commentCharacter('#')
      .skipComments(false)
      .build("foo")

    assertNotNull(reader)
  }

  companion object {
    private const val DATA = "header1,header2\nfoo,bar\n"
    private const val EXPECTED = "{header1=foo, header2=bar}"
  }
}