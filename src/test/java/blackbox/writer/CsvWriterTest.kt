package blackbox.writer

import net.codinux.csv.kcsv.UncheckedIOException
import net.codinux.csv.kcsv.writer.CsvWriter
import net.codinux.csv.kcsv.writer.LineDelimiter
import net.codinux.csv.kcsv.writer.QuoteStrategy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer
import java.util.stream.Stream

class CsvWriterTest {
  private val crw = CsvWriter.builder()
    .lineDelimiter(LineDelimiter.LF)

  @ParameterizedTest
  @ValueSource(chars = ['\r', '\n'])
  fun configBuilder(c: Char) {
    val e = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvWriter.builder().fieldSeparator(c).build(StringWriter()) }
    Assertions.assertEquals("fieldSeparator must not be a newline char", e.message)
    val e2 = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvWriter.builder().quoteCharacter(c).build(StringWriter()) }
    Assertions.assertEquals("quoteCharacter must not be a newline char", e2.message)
    val e3 = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvWriter.builder().commentCharacter(c).build(StringWriter()) }
    Assertions.assertEquals("commentCharacter must not be a newline char", e3.message)
  }

  @Test
  fun configWriter() {
    val e = Assertions.assertThrows(IllegalArgumentException::class.java) { crw.fieldSeparator(',').quoteCharacter(',').build(StringWriter()) }
    Assertions.assertTrue(e.message!!.contains("Control characters must differ"))
    val e2 = Assertions.assertThrows(IllegalArgumentException::class.java) { crw.fieldSeparator(',').commentCharacter(',').build(StringWriter()) }
    Assertions.assertTrue(e2.message!!.contains("Control characters must differ"))
    val e3 = Assertions.assertThrows(IllegalArgumentException::class.java) { crw.quoteCharacter(',').commentCharacter(',').build(StringWriter()) }
    Assertions.assertTrue(e3.message!!.contains("Control characters must differ"))
  }

  @Test
  fun nullQuote() {
    Assertions.assertEquals("foo,,bar\n", write("foo", null, "bar"))
    Assertions.assertEquals("foo,,bar\n", write("foo", "", "bar"))
    Assertions.assertEquals("foo,\",\",bar\n", write("foo", ",", "bar"))
  }

  @Test
  fun emptyQuote() {
    crw.quoteStrategy(QuoteStrategy.EMPTY)
    Assertions.assertEquals("foo,,bar\n", write("foo", null, "bar"))
    Assertions.assertEquals("foo,\"\",bar\n", write("foo", "", "bar"))
    Assertions.assertEquals("foo,\",\",bar\n", write("foo", ",", "bar"))
  }

  @Test
  fun oneLineSingleValue() {
    Assertions.assertEquals("foo\n", write("foo"))
  }

  @Test
  fun oneLineTwoValues() {
    Assertions.assertEquals("foo,bar\n", write("foo", "bar"))
  }

  @Test
  fun oneLineTwoValuesAsList() {
    val cols: MutableList<String?> = ArrayList()
    cols.add("foo")
    cols.add("bar")
    Assertions.assertEquals("foo,bar\nfoo,bar\n",
      write { w: CsvWriter -> w.writeRow(cols).writeRow(cols) })
  }

  @Test
  fun twoLinesTwoValues() {
    Assertions.assertEquals("foo,bar\n", write("foo", "bar"))
  }

  @Test
  fun delimitText() {
    Assertions.assertEquals(
      "a,\"b,c\",\"d\ne\",\"f\"\"g\",,\n",
      write("a", "b,c", "d\ne", "f\"g", "", null)
    )
  }

  @Test
  fun alwaysQuoteText() {
    crw.quoteStrategy(QuoteStrategy.ALWAYS)
    Assertions.assertEquals(
      "\"a\",\"b,c\",\"d\ne\",\"f\"\"g\",\"\",\"\"\n",
      write("a", "b,c", "d\ne", "f\"g", "", null)
    )
  }

  @Test
  fun fieldSeparator() {
    crw.fieldSeparator(';')
    Assertions.assertEquals("foo;bar\n", write("foo", "bar"))
  }

  @Test
  fun quoteCharacter() {
    crw.quoteCharacter('\'')
    Assertions.assertEquals("'foo,bar'\n", write("foo,bar"))
  }

  @Test
  fun escapeQuotes() {
    Assertions.assertEquals("foo,\"\"\"bar\"\"\"\n", write("foo", "\"bar\""))
  }

  @Test
  fun commentCharacter() {
    Assertions.assertEquals("\"#foo\",#bar\n", write("#foo", "#bar"))
    Assertions.assertEquals(" #foo,#bar\n", write(" #foo", "#bar"))
  }

  @Test
  fun commentCharacterDifferentChar() {
    Assertions.assertEquals(";foo,bar\n", write(";foo", "bar"))
    crw.commentCharacter(';')
    Assertions.assertEquals("\";foo\",bar\n", write(";foo", "bar"))
  }

  @Test
  fun writeComment() {
    Assertions.assertEquals("#this is a comment\n", write { w: CsvWriter -> w.writeComment("this is a comment") })
  }

  @Test
  fun writeCommentWithNewlines() {
    Assertions.assertEquals("#\n#line 2\n#line 3\n#line 4\n#\n",
      write { w: CsvWriter -> w.writeComment("\rline 2\nline 3\r\nline 4\n") })
  }

  @Test
  fun writeEmptyComment() {
    Assertions.assertEquals("#\n#\n", write { w: CsvWriter -> w.writeComment("").writeComment(null) })
  }

  @Test
  fun writeCommentDifferentChar() {
    crw.commentCharacter(';')
    Assertions.assertEquals(";this is a comment\n", write { w: CsvWriter -> w.writeComment("this is a comment") })
  }

  @Test
  fun appending() {
    Assertions.assertEquals("foo,bar\nfoo2,bar2\n",
      write { w: CsvWriter -> w.writeRow("foo", "bar").writeRow("foo2", "bar2") })
  }

  @Test
  fun path(@TempDir tempDir: Path) {
    val file = tempDir.resolve("fastcsv.csv")
    CsvWriter.builder().build(file).use { csv -> csv.writeRow("value1", "value2") }
    Assertions.assertEquals(
      "value1,value2\r\n",
      String(Files.readAllBytes(file), StandardCharsets.UTF_8)
    )
  }

  @Test
  fun chained() {
    val writer = CsvWriter.builder()
      .fieldSeparator(',')
      .quoteCharacter('"')
      .quoteStrategy(QuoteStrategy.REQUIRED)
      .lineDelimiter(LineDelimiter.CRLF)
      .build(StringWriter())
    Assertions.assertNotNull(writer)
  }

  @Test
  fun streaming() {
    val stream = Stream.of(listOf("header1", "header2"), listOf("value1", "value2"))
    val sw = StringWriter()
    val csvWriter = CsvWriter.builder().build(sw)
    stream.forEach { values: List<String> -> csvWriter.writeRow(values) }
    Assertions.assertEquals("header1,header2\r\nvalue1,value2\r\n", sw.toString())
  }

  @Test
  fun mixedWriterUsage() {
    val stringWriter = StringWriter()
    val csvWriter = CsvWriter.builder().build(stringWriter)
    csvWriter.writeRow("foo", "bar")
    stringWriter.write("# my comment\r\n")
    csvWriter.writeRow("1", "2")
    Assertions.assertEquals("foo,bar\r\n# my comment\r\n1,2\r\n", stringWriter.toString())
  }

  @Test
  fun unwritableArray() {
    val e = Assertions.assertThrows(UncheckedIOException::class.java) { crw.build(UnwritableWriter()).writeRow("foo") }
    Assertions.assertEquals("net.codinux.csv.kcsv.IOException: Cannot write", e.message)
  }

  @Test
  fun unwritableIterable() {
    val e = Assertions.assertThrows(UncheckedIOException::class.java) { crw.build(UnwritableWriter()).writeRow(listOf("foo")) }
    Assertions.assertEquals("net.codinux.csv.kcsv.IOException: Cannot write", e.message)
    val e2 = Assertions.assertThrows(UncheckedIOException::class.java) { crw.build(UnwritableWriter()).writeComment("foo") }
    Assertions.assertEquals("net.codinux.csv.kcsv.IOException: Cannot write", e2.message)
  }

  // buffer
  @Test
  fun invalidBuffer() {
    Assertions.assertThrows(IllegalArgumentException::class.java) { crw.bufferSize(-1) }
  }

  @Test
  fun disableBuffer() {
    val stringWriter = StringWriter()
    crw.bufferSize(0).build(stringWriter).writeRow("foo", "bar")
    Assertions.assertEquals("foo,bar\n", stringWriter.toString())
  }

  // toString()
  @Test
  fun builderToString() {
    Assertions.assertEquals(
      """
  CsvWriterBuilder[fieldSeparator=,, quoteCharacter=", commentCharacter=#, quoteStrategy=REQUIRED, lineDelimiter=
  , bufferSize=8192]
  """.trimIndent(), crw.toString()
    )
  }

  @Test
  fun writerToString() {
    Assertions.assertEquals(
      """
  CsvWriter[fieldSeparator=,, quoteCharacter=", commentCharacter=#, quoteStrategy=REQUIRED, lineDelimiter='
  ']
  """.trimIndent(), crw.build(StringWriter()).toString()
    )
  }

  private fun write(vararg cols: String?): String {
    return write { w: CsvWriter -> w.writeRow(*cols) }
  }

  private fun write(c: Consumer<CsvWriter>): String {
    val sw = StringWriter()
    val to = crw.build(sw)
    c.accept(to)
    return sw.toString()
  }
}