package blackbox.reader

import net.codinux.csv.kcsv.reader.*
import net.codinux.csv.kcsv.reader.CsvReader.CsvReaderBuilder
import net.codinux.csv.kcsv.reader.datareader.DataReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.CharArrayReader
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.io.UncheckedIOException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.Stream

class CsvReaderTest {
  private val crb = CsvReader.builder()
  @ParameterizedTest
  @ValueSource(chars = ['\r', '\n'])
  fun configBuilder(c: Char) {
    val e = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvReader("foo", fieldSeparator = c) }
    assertEquals("fieldSeparator must not be a newline char", e.message)
    val e2 = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvReader("foo", quoteCharacter = c) }
    assertEquals("quoteCharacter must not be a newline char", e2.message)
    val e3 = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvReader("foo", commentCharacter = c) }
    assertEquals("commentCharacter must not be a newline char", e3.message)
  }

  @ParameterizedTest
  @MethodSource("provideBuilderForMisconfiguration")
  fun configReader(builder: CsvReaderBuilder) {
    val e = Assertions.assertThrows(IllegalArgumentException::class.java) { builder.build("foo") }
    assertTrue(e.message!!.contains("Control characters must differ"))
  }

  @Test
  fun empty() {
    val it: Iterator<CsvRow> = CsvReader("").iterator()
    Assertions.assertFalse(it.hasNext())
    Assertions.assertThrows(NoSuchElementException::class.java) { it.next() }
  }

  @Test
  fun immutableResponse() {
    val fields = CsvReader("foo").iterator().next().getFields()
    Assertions.assertThrows(UnsupportedOperationException::class.java) { (fields as MutableList).add("bar") }
  }

  // toString()
  @Test
  fun readerToString() {
    assertEquals(
      "CsvReader[commentStrategy=NONE, skipEmptyRows=true, "
        + "errorOnDifferentFieldCount=false]", CsvReader("").toString()
    )
  }

  // skipped rows
  @Test
  fun singleRowNoSkipEmpty() {
    val reader = CsvReader("", skipEmptyRows = false)
    Assertions.assertFalse(reader.iterator().hasNext())
  }

  @Test
  fun multipleRowsNoSkipEmpty() {
    val reader = CsvReader("\n\na", skipEmptyRows = false)
    val it: Iterator<CsvRow> = reader.iterator()
    var row = it.next()
    assertTrue(row.isEmpty())
    assertEquals(1, row.getFieldCount())
    assertEquals(1, row.originalLineNumber)
    assertEquals(listOf(""), row.getFields())
    row = it.next()
    assertTrue(row.isEmpty())
    assertEquals(1, row.getFieldCount())
    assertEquals(2, row.originalLineNumber)
    assertEquals(listOf(""), row.getFields())
    row = it.next()
    Assertions.assertFalse(row.isEmpty())
    assertEquals(1, row.getFieldCount())
    assertEquals(3, row.originalLineNumber)
    assertEquals(listOf("a"), row.getFields())
    Assertions.assertFalse(it.hasNext())
  }

  @Test
  fun skippedRows() {
    val csv = readAll("\n\nfoo\n\nbar\n\n")
    assertEquals(2, csv.size)
    val it = csv.iterator()
    var row = it.next()
    assertEquals(3, row.originalLineNumber)
    assertEquals(listOf("foo"), row.getFields())
    row = it.next()
    assertEquals(5, row.originalLineNumber)
    assertEquals(listOf("bar"), row.getFields())
  }

  // different field count
  @Test
  fun differentFieldCountSuccess() {
    crb.errorOnDifferentFieldCount(true)
    Assertions.assertDoesNotThrow<List<CsvRow>> { readAll("foo\nbar") }
    Assertions.assertDoesNotThrow<List<CsvRow>> { readAll("foo\nbar\n") }
    Assertions.assertDoesNotThrow<List<CsvRow>> { readAll("foo,bar\nfaz,baz") }
    Assertions.assertDoesNotThrow<List<CsvRow>> { readAll("foo,bar\nfaz,baz\n") }
    Assertions.assertDoesNotThrow<List<CsvRow>> { readAll("foo,bar\n,baz") }
    Assertions.assertDoesNotThrow<List<CsvRow>> { readAll(",bar\nfaz,baz") }
  }

  @Test
  fun differentFieldCountFail() {
    val reader = CsvReader("foo\nbar,\"baz\nbax\"", errorOnDifferentFieldCount = true)
    val e = Assertions.assertThrows(
      MalformedCsvException::class.java
    ) { reader.toList() }
    assertEquals("Row 2 has 2 fields, but first row had 1 fields", e.message)
  }

  @Test
  fun hasHeader() {
    val reader = CsvReader("h1,h2,h3\n1,2,3", hasHeader = true)

    assertTrue(reader.header == setOf("h1", "h2", "h3"))
  }

  @Test
  fun ignoreInvalidQuoteChars() {
    val reader: CsvReader = crb
      .ignoreInvalidQuoteChars(true)
      .build("\"de:14628:1148:1\",\"Ri. \"Am Windberg\"\",\"51,002455\"\n")
    val row = reader.iterator().next()
    assertEquals("Ri. \"Am Windberg\"", row.getField(1))
    assertEquals("de:14628:1148:1", row.getField(0))
    assertEquals("51,002455", row.getField(2))
  }

  @Disabled // does not work yet
  @Test
  fun ignoreInvalidQuoteChars_InvalidQuoteCharAtEndOfBuffer() {
    // see Buffer.READ_SIZE (other option would be to make Buffer.READ_SIZE public readable)
    val bufferSize = 8192
    val csvData = StringBuilder()
      .append('"') // cell starts with a quote
    for (i in 1 until bufferSize - 1) { // fill buffer till bufferSize - 1 with any char
      csvData.append('a')
    }
    csvData.append('"') // now append the invalid quote char at end of buffer
      .append("Some more data") // append some more data
      .append('"') // append the correct quote char
      .append('\n') // and end line / data set
    val reader: CsvReader = crb
      .ignoreInvalidQuoteChars(true)
      .build(DataReader.reader(StringReader(csvData.toString())))
    val row = reader.iterator().next()
    val cell = row.getField(0)
    assertTrue(cell.length > bufferSize)
    assertTrue(cell.endsWith("aaaa\"Some more data"))
  }

  @Test
  fun ignoreInvalidQuoteChars_ValidQuoteCharAtEndOfBuffer() {
    // see Buffer.READ_SIZE (other option would be to make Buffer.READ_SIZE public readable)
    val bufferSize = 8192
    val csvData = StringBuilder()
      .append('"') // cell starts with a quote
    for (i in 1 until bufferSize - 1) { // fill buffer till bufferSize - 1 with any char
      csvData.append('a')
    }
    csvData.append('"') // now append the valid quote char at end of buffer
      .append(",\"Some more data in next cell\"") // append another cell
      .append('\n') // and end line / data set
    val reader: CsvReader = crb
      .ignoreInvalidQuoteChars(true)
      .build(DataReader.reader(StringReader(csvData.toString())))
    val row = reader.iterator().next()
    val cell = row.getField(0)
    assertEquals(bufferSize - 2, cell.length)
    assertTrue(cell.endsWith("aaaa"))
    assertEquals("Some more data in next cell", row.getField(1))
  }

  @get:Test
  val nonExistingFieldByIndex: Unit
    // field by index
    get() {
      Assertions.assertThrows(IndexOutOfBoundsException::class.java) { spotbugs(readSingleRow("foo").getField(1)) }
    }

  private fun spotbugs(foo: String) {
    // Prevent RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT
  }

  // line numbering
  @Test
  fun lineNumbering() {
    val data =
      "line 1\n" +
        "line 2\r" +
        "line 3\r\n" +
        "\"line 4\rwith\r\nand\n\"\n" +
        "#line 8\n" +
        "line 9"
    val it: Iterator<CsvRow> = CsvReader(data, commentStrategy = CommentStrategy.SKIP).iterator()
    var row = it.next()
    assertEquals(listOf("line 1"), row.getFields())
    assertEquals(1, row.originalLineNumber)
    row = it.next()
    assertEquals(listOf("line 2"), row.getFields())
    assertEquals(2, row.originalLineNumber)
    row = it.next()
    assertEquals(listOf("line 3"), row.getFields())
    assertEquals(3, row.originalLineNumber)
    row = it.next()
    assertEquals(listOf("line 4\rwith\r\nand\n"), row.getFields())
    assertEquals(4, row.originalLineNumber)
    row = it.next()
    assertEquals(listOf("line 9"), row.getFields())
    assertEquals(9, row.originalLineNumber)
    Assertions.assertFalse(it.hasNext())
  }

  // comment
  @Test
  fun comment() {
    val it: Iterator<CsvRow> = CsvReader("#comment \"1\"\na,#b,c", commentStrategy = CommentStrategy.READ).iterator()
    var row = it.next()
    assertTrue(row.isComment)
    assertEquals(1, row.originalLineNumber)
    assertEquals(listOf("comment \"1\""), row.getFields())
    row = it.next()
    Assertions.assertFalse(row.isComment)
    assertEquals(2, row.originalLineNumber)
    assertEquals(mutableListOf("a", "#b", "c"), row.getFields())
  }

  // to string
  @Test
  fun toStringWithoutHeader() {
    assertEquals(
      "CsvRow[originalLineNumber=1, fields=[fieldA, fieldB], comment=false]",
      readSingleRow("fieldA,fieldB\n").toString()
    )
  }

  // refill buffer while parsing an unquoted field containing a quote character
  @Test
  fun refillBufferInDataWithQuote() {
    val extra = ",a\"b\"c,d,".toCharArray()
    val buf = CharArray(8192 + extra.size)
    Arrays.fill(buf, 'X')
    System.arraycopy(extra, 0, buf, 8190, extra.size)
    val row = csvReader(CharArrayReader(buf)).iterator().next()
    assertEquals(4, row.getFieldCount())
    assertEquals("a\"b\"c", row.getField(1))
    assertEquals("d", row.getField(2))
    assertEquals("XX", row.getField(3))
  }

  // buffer exceed
  @Test
  fun bufferExceed() {
    val buf = CharArray(8 * 1024 * 1024)
    Arrays.fill(buf, 'X')
    buf[buf.size - 1] = ','
    csvReader(CharArrayReader(buf)).iterator().next()
    buf[buf.size - 1] = Char('X'.code.toByte().toUShort())
    val exception = Assertions.assertThrows(UncheckedIOException::class.java) { csvReader(CharArrayReader(buf)).iterator().next() }
    assertEquals("IOException when reading first record", exception.message)
    assertEquals(
      "Maximum buffer size 8388608 is not enough to read data of a single field. "
        + "Typically, this happens if quotation started but did not end within this buffer's "
        + "maximum boundary.",
      exception.cause!!.message
    )
  }

  @Test
  fun bufferExceedSubsequentRecord() {
    val buf = CharArray(8 * 1024 * 1024)
    Arrays.fill(buf, 'X')
    val s = "a,b,c\n\""
    System.arraycopy(s.toCharArray(), 0, buf, 0, s.length)
    val iterator = csvReader(CharArrayReader(buf)).iterator()
    iterator.next()
    val exception = Assertions.assertThrows(UncheckedIOException::class.java) { iterator.next() }
    assertEquals("IOException when reading record that started in line 2", exception.message)
    assertEquals(
      "Maximum buffer size 8388608 is not enough to read data of a single field. "
        + "Typically, this happens if quotation started but did not end within this buffer's "
        + "maximum boundary.",
      exception.cause!!.message
    )
  }

  // API
  @Test
  fun closeApi() {
    val consumer = Consumer { csvRow: CsvRow? -> }
    val supp = Supplier { CloseStatusReader(StringReader("foo,bar")) }
    var csr = supp.get()
    csvReader(csr).use { reader -> reader.forEach(consumer) }
    assertTrue(csr.isClosed)
    csr = supp.get()
    csvReader(csr).iterator().use { it.forEachRemaining(consumer) }
    assertTrue(csr.isClosed)
    csr = supp.get()
    csvReader(csr).stream().use { stream -> stream.forEach(consumer) }
    assertTrue(csr.isClosed)
  }

  @Test
  fun closeStringNoException() {
    Assertions.assertDoesNotThrow { CsvReader("foo").close() }
  }

  @Test
  fun spliterator() {
    val spliterator = CsvReader("a,b,c\n1,2,3").spliterator()
    Assertions.assertNull(spliterator.trySplit())
    assertEquals(Long.MAX_VALUE, spliterator.estimateSize())
    assertEquals(
      Spliterator.ORDERED or Spliterator.DISTINCT or Spliterator.NONNULL
        or Spliterator.IMMUTABLE, spliterator.characteristics()
    )
    val rows = AtomicInteger()
    val rows2 = AtomicInteger()
    while (spliterator.tryAdvance { row: CsvRow? -> rows.incrementAndGet() }) {
      rows2.incrementAndGet()
    }
    assertEquals(2, rows.get())
    assertEquals(2, rows2.get())
  }

  @Test
  fun parallelDistinct() {
    assertEquals(2, CsvReader("foo\nfoo").stream().parallel().distinct().count())
  }

  // Coverage
  @Test
  fun closeException() {
    val csvReader = csvReader(UncloseableReader(StringReader("foo"))).stream()
    val e = Assertions.assertThrows(
      UncheckedIOException::class.java
    ) { csvReader.close() }
    assertEquals("java.io.IOException: Cannot close", e.message)
  }

  @Test
  fun unreadable() {
    val e = Assertions.assertThrows(UncheckedIOException::class.java) { csvReader(UnreadableReader()).iterator().next() }
    assertEquals("IOException when reading first record", e.message)
  }


  private fun csvReader(reader: Reader) = CsvReader(DataReader.reader(reader))

  // test helpers
  private fun readSingleRow(data: String): CsvRow {
    val lists = readAll(data)
    assertEquals(1, lists.size)
    return lists[0]
  }

  private fun readAll(data: String): List<CsvRow> {
    return CsvReader(data).toList()
  }

  companion object {
    @JvmStatic
    fun provideBuilderForMisconfiguration(): Stream<Arguments> {
      return Stream.of(
        Arguments.of(CsvReader.builder().quoteCharacter(',')),
        Arguments.of(CsvReader.builder().commentCharacter(',')),
        Arguments.of(CsvReader.builder().quoteCharacter('#').commentCharacter('#'))
      )
    }
  }
}