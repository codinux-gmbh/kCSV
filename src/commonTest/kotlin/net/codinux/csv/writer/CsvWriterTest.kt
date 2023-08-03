package net.codinux.csv.writer

import io.kotest.core.spec.style.FunSpec
import net.codinux.csv.IOException
import net.codinux.csv.UncheckedIOException
import net.codinux.csv.writer.datawriter.DataWriter
import net.codinux.csv.writer.datawriter.StringBuilderDataWriter
import kotlin.test.*

class CsvWriterTest : FunSpec({

  listOf('\r', '\n').forEachIndexed { index, char ->
    test("[$index] configBuilder for '$char'") {
      val e = assertFailsWith(IllegalArgumentException::class) { CsvWriter.builder().fieldSeparator(char).build(DataWriter.writer()) }
      assertEquals("fieldSeparator must not be a newline char", e.message)
      val e2 = assertFailsWith(IllegalArgumentException::class) { CsvWriter.builder().quoteCharacter(char).build(DataWriter.writer()) }
      assertEquals("quoteCharacter must not be a newline char", e2.message)
      val e3 = assertFailsWith(IllegalArgumentException::class) { CsvWriter.builder().commentCharacter(char).build(DataWriter.writer()) }
      assertEquals("commentCharacter must not be a newline char", e3.message)
    }
  }

  val crw = CsvWriter.builder()
    .lineDelimiter(LineDelimiter.LF)

  listOf(
    crw.fieldSeparator(',').quoteCharacter(','),
    crw.fieldSeparator(',').commentCharacter(','),
    crw.quoteCharacter(',').commentCharacter(',')
  ).forEachIndexed { index, csvWriterBuilder ->
    test("[$index] configWriter for $csvWriterBuilder") {
      val e = assertFailsWith(IllegalArgumentException::class) { csvWriterBuilder.build(DataWriter.writer()) }
      assertTrue(e.message!!.contains("Control characters must differ"))
    }
  }

}) {
  
  private val crw = CsvWriter.builder()
    .lineDelimiter(LineDelimiter.LF)

  @Test
  fun nullQuote() {
    assertEquals("foo,,bar\n", write("foo", null, "bar"))
    assertEquals("foo,,bar\n", write("foo", "", "bar"))
    assertEquals("foo,\",\",bar\n", write("foo", ",", "bar"))
  }

  @Test
  fun emptyQuote() {
    crw.quoteStrategy(QuoteStrategy.EMPTY)
    assertEquals("foo,,bar\n", write("foo", null, "bar"))
    assertEquals("foo,\"\",bar\n", write("foo", "", "bar"))
    assertEquals("foo,\",\",bar\n", write("foo", ",", "bar"))
  }

  @Test
  fun oneLineSingleValue() {
    assertEquals("foo\n", write("foo"))
  }

  @Test
  fun oneLineTwoValues() {
    assertEquals("foo,bar\n", write("foo", "bar"))
  }

  @Test
  fun oneLineTwoValuesAsList() {
    val cols: MutableList<String?> = ArrayList()
    cols.add("foo")
    cols.add("bar")
    assertEquals("foo,bar\nfoo,bar\n",
      write { w: CsvWriter -> w.writeRow(cols).writeRow(cols) })
  }

  @Test
  fun twoLinesTwoValues() {
    assertEquals("foo,bar\n", write("foo", "bar"))
  }

  @Test
  fun delimitText() {
    assertEquals(
      "a,\"b,c\",\"d\ne\",\"f\"\"g\",,\n",
      write("a", "b,c", "d\ne", "f\"g", "", null)
    )
  }

  @Test
  fun alwaysQuoteText() {
    crw.quoteStrategy(QuoteStrategy.ALWAYS)
    assertEquals(
      "\"a\",\"b,c\",\"d\ne\",\"f\"\"g\",\"\",\"\"\n",
      write("a", "b,c", "d\ne", "f\"g", "", null)
    )
  }

  @Test
  fun fieldSeparator() {
    crw.fieldSeparator(';')
    assertEquals("foo;bar\n", write("foo", "bar"))
  }

  @Test
  fun quoteCharacter() {
    crw.quoteCharacter('\'')
    assertEquals("'foo,bar'\n", write("foo,bar"))
  }

  @Test
  fun escapeQuotes() {
    assertEquals("foo,\"\"\"bar\"\"\"\n", write("foo", "\"bar\""))
  }

  @Test
  fun commentCharacter() {
    assertEquals("\"#foo\",#bar\n", write("#foo", "#bar"))
    assertEquals(" #foo,#bar\n", write(" #foo", "#bar"))
  }

  @Test
  fun commentCharacterDifferentChar() {
    assertEquals(";foo,bar\n", write(";foo", "bar"))
    crw.commentCharacter(';')
    assertEquals("\";foo\",bar\n", write(";foo", "bar"))
  }

  @Test
  fun writeComment() {
    assertEquals("#this is a comment\n", write { w: CsvWriter -> w.writeComment("this is a comment") })
  }

  @Test
  fun writeCommentWithNewlines() {
    assertEquals("#\n#line 2\n#line 3\n#line 4\n#\n",
      write { w: CsvWriter -> w.writeComment("\rline 2\nline 3\r\nline 4\n") })
  }

  @Test
  fun writeEmptyComment() {
    assertEquals("#\n#\n", write { w: CsvWriter -> w.writeComment("").writeComment(null) })
  }

  @Test
  fun writeCommentDifferentChar() {
    crw.commentCharacter(';')
    assertEquals(";this is a comment\n", write { w: CsvWriter -> w.writeComment("this is a comment") })
  }

  @Test
  fun appending() {
    assertEquals("foo,bar\nfoo2,bar2\n",
      write { w: CsvWriter -> w.writeRow("foo", "bar").writeRow("foo2", "bar2") })
  }

  @Test
  fun chained() {
    val writer = CsvWriter.builder()
      .fieldSeparator(',')
      .quoteCharacter('"')
      .quoteStrategy(QuoteStrategy.REQUIRED)
      .lineDelimiter(LineDelimiter.CRLF)
      .build(DataWriter.writer())
    assertNotNull(writer)
  }

  @Test
  fun mixedWriterUsage() {
    val stringWriter = StringBuilderDataWriter(StringBuilder())
    val csvWriter = CsvWriter.builder().build(stringWriter)
    csvWriter.writeRow("foo", "bar")
    stringWriter.write("# my comment\r\n")
    csvWriter.writeRow("1", "2")
    assertEquals("foo,bar\r\n# my comment\r\n1,2\r\n", stringWriter.toString())
  }

  @Test
  fun unwritableArray() {
    val e = assertFailsWith(UncheckedIOException::class) { crw.build(UnwritableWriter()).writeRow("foo") }
    assertEquals("Cannot write", e.cause!!.message)
    assertEquals(IOException::class, e.cause!!::class)
  }

  @Test
  fun unwritableIterable() {
    val e = assertFailsWith(UncheckedIOException::class) { crw.build(UnwritableWriter()).writeRow(listOf("foo")) }
    assertEquals("Cannot write", e.cause!!.message)
    assertEquals(IOException::class, e.cause!!::class)

    val e2 = assertFailsWith(UncheckedIOException::class) { crw.build(UnwritableWriter()).writeComment("foo") }
    assertEquals("Cannot write", e2.cause!!.message)
    assertEquals(IOException::class, e2.cause!!::class)
  }

  // buffer
  @Test
  fun invalidBuffer() {
    assertFailsWith(IllegalArgumentException::class) { crw.bufferSize(-1) }
  }

  @Test
  fun disableBuffer() {
    val stringWriter = DataWriter.writer()
    crw.bufferSize(0).build(stringWriter).writeRow("foo", "bar")
    assertEquals("foo,bar\n", stringWriter.toString())
  }

  // toString()
  @Test
  fun builderToString() {
    assertEquals(
      """
  CsvWriterBuilder[fieldSeparator=,, quoteCharacter=", commentCharacter=#, quoteStrategy=REQUIRED, lineDelimiter=
  , bufferSize=8192]
  """.trimIndent(), crw.toString()
    )
  }

  @Test
  fun writerToString() {
    assertEquals(
      """
  CsvWriter[fieldSeparator=,, quoteCharacter=", commentCharacter=#, quoteStrategy=REQUIRED, lineDelimiter='
  ']
  """.trimIndent(), crw.build(DataWriter.writer()).toString()
    )
  }

  private fun write(vararg cols: String?): String {
    return write { w: CsvWriter -> w.writeRow(*cols) }
  }

  private fun write(c: (CsvWriter) -> Unit): String {
    val stringBuilder = StringBuilder()
    val to = crw.build(DataWriter.writer(stringBuilder))
    c(to)
    return stringBuilder.toString()
  }
}