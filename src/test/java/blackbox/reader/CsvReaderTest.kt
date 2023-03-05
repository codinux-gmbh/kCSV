package blackbox.reader

import net.codinux.csv.kcsv.reader.*
import net.codinux.csv.kcsv.reader.CsvReader.CsvReaderBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.CharArrayReader
import java.io.IOException
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
    Assertions.assertEquals("fieldSeparator must not be a newline char", e.message)
    val e2 = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvReader("foo", quoteCharacter = c) }
    Assertions.assertEquals("quoteCharacter must not be a newline char", e2.message)
    val e3 = Assertions.assertThrows(IllegalArgumentException::class.java) { CsvReader("foo", commentCharacter = c) }
    Assertions.assertEquals("commentCharacter must not be a newline char", e3.message)
  }

  @ParameterizedTest
  @MethodSource("provideBuilderForMisconfiguration")
  fun configReader(builder: CsvReaderBuilder) {
    val e = Assertions.assertThrows(IllegalArgumentException::class.java) { builder.build("foo") }
    Assertions.assertTrue(e.message!!.contains("Control characters must differ"))
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
    Assertions.assertEquals(
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
    Assertions.assertTrue(row.isEmpty())
    Assertions.assertEquals(1, row.getFieldCount())
    Assertions.assertEquals(1, row.originalLineNumber)
    Assertions.assertEquals(listOf(""), row.getFields())
    row = it.next()
    Assertions.assertTrue(row.isEmpty())
    Assertions.assertEquals(1, row.getFieldCount())
    Assertions.assertEquals(2, row.originalLineNumber)
    Assertions.assertEquals(listOf(""), row.getFields())
    row = it.next()
    Assertions.assertFalse(row.isEmpty())
    Assertions.assertEquals(1, row.getFieldCount())
    Assertions.assertEquals(3, row.originalLineNumber)
    Assertions.assertEquals(listOf("a"), row.getFields())
    Assertions.assertFalse(it.hasNext())
  }

  @Test
  fun skippedRows() {
    val csv = readAll("\n\nfoo\n\nbar\n\n")
    Assertions.assertEquals(2, csv.size)
    val it = csv.iterator()
    var row = it.next()
    Assertions.assertEquals(3, row.originalLineNumber)
    Assertions.assertEquals(listOf("foo"), row.getFields())
    row = it.next()
    Assertions.assertEquals(5, row.originalLineNumber)
    Assertions.assertEquals(listOf("bar"), row.getFields())
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
    Assertions.assertEquals("Row 2 has 2 fields, but first row had 1 fields", e.message)
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
    Assertions.assertEquals(listOf("line 1"), row.getFields())
    Assertions.assertEquals(1, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals(listOf("line 2"), row.getFields())
    Assertions.assertEquals(2, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals(listOf("line 3"), row.getFields())
    Assertions.assertEquals(3, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals(listOf("line 4\rwith\r\nand\n"), row.getFields())
    Assertions.assertEquals(4, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals(listOf("line 9"), row.getFields())
    Assertions.assertEquals(9, row.originalLineNumber)
    Assertions.assertFalse(it.hasNext())
  }

  // comment
  @Test
  fun comment() {
    val it: Iterator<CsvRow> = CsvReader("#comment \"1\"\na,#b,c", commentStrategy = CommentStrategy.READ).iterator()
    var row = it.next()
    Assertions.assertTrue(row.isComment)
    Assertions.assertEquals(1, row.originalLineNumber)
    Assertions.assertEquals(listOf("comment \"1\""), row.getFields())
    row = it.next()
    Assertions.assertFalse(row.isComment)
    Assertions.assertEquals(2, row.originalLineNumber)
    Assertions.assertEquals(mutableListOf("a", "#b", "c"), row.getFields())
  }

  // to string
  @Test
  fun toStringWithoutHeader() {
    Assertions.assertEquals(
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
    val row = CsvReader(CharArrayReader(buf)).iterator().next()
    Assertions.assertEquals(4, row.getFieldCount())
    Assertions.assertEquals("a\"b\"c", row.getField(1))
    Assertions.assertEquals("d", row.getField(2))
    Assertions.assertEquals("XX", row.getField(3))
  }

  // buffer exceed
  @Test
  fun bufferExceed() {
    val buf = CharArray(8 * 1024 * 1024)
    Arrays.fill(buf, 'X')
    buf[buf.size - 1] = ','
    CsvReader(CharArrayReader(buf)).iterator().next()
    buf[buf.size - 1] = Char('X'.code.toByte().toUShort())
    val exception = Assertions.assertThrows(UncheckedIOException::class.java) { CsvReader(CharArrayReader(buf)).iterator().next() }
    Assertions.assertEquals("IOException when reading first record", exception.message)
    Assertions.assertEquals(
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
    val iterator = CsvReader(CharArrayReader(buf)).iterator()
    iterator.next()
    val exception = Assertions.assertThrows(UncheckedIOException::class.java) { iterator.next() }
    Assertions.assertEquals("IOException when reading record that started in line 2", exception.message)
    Assertions.assertEquals(
      "Maximum buffer size 8388608 is not enough to read data of a single field. "
        + "Typically, this happens if quotation started but did not end within this buffer's "
        + "maximum boundary.",
      exception.cause!!.message
    )
  }

  // API
  @Test
  @Throws(IOException::class)
  fun closeApi() {
    val consumer = Consumer { csvRow: CsvRow? -> }
    val supp = Supplier { CloseStatusReader(StringReader("foo,bar")) }
    var csr = supp.get()
    CsvReader(csr).use { reader -> reader.forEach(consumer) }
    Assertions.assertTrue(csr.isClosed)
    csr = supp.get()
    CsvReader(csr).iterator().use { it.forEachRemaining(consumer) }
    Assertions.assertTrue(csr.isClosed)
    csr = supp.get()
    CsvReader(csr).stream().use { stream -> stream.forEach(consumer) }
    Assertions.assertTrue(csr.isClosed)
  }

  @Test
  fun closeStringNoException() {
    Assertions.assertDoesNotThrow { CsvReader("foo").close() }
  }

  @Test
  fun spliterator() {
    val spliterator = CsvReader("a,b,c\n1,2,3").spliterator()
    Assertions.assertNull(spliterator.trySplit())
    Assertions.assertEquals(Long.MAX_VALUE, spliterator.estimateSize())
    Assertions.assertEquals(
      Spliterator.ORDERED or Spliterator.DISTINCT or Spliterator.NONNULL
        or Spliterator.IMMUTABLE, spliterator.characteristics()
    )
    val rows = AtomicInteger()
    val rows2 = AtomicInteger()
    while (spliterator.tryAdvance { row: CsvRow? -> rows.incrementAndGet() }) {
      rows2.incrementAndGet()
    }
    Assertions.assertEquals(2, rows.get())
    Assertions.assertEquals(2, rows2.get())
  }

  @Test
  fun parallelDistinct() {
    Assertions.assertEquals(2, CsvReader("foo\nfoo").stream().parallel().distinct().count())
  }

  // Coverage
  @Test
  fun closeException() {
    val csvReader = CsvReader(UncloseableReader(StringReader("foo"))).stream()
    val e = Assertions.assertThrows(
      UncheckedIOException::class.java
    ) { csvReader.close() }
    Assertions.assertEquals("java.io.IOException: Cannot close", e.message)
  }

  @Test
  fun unreadable() {
    val e = Assertions.assertThrows(UncheckedIOException::class.java) { CsvReader(UnreadableReader()).iterator().next() }
    Assertions.assertEquals("IOException when reading first record", e.message)
  }

  // test helpers
  private fun readSingleRow(data: String): CsvRow {
    val lists = readAll(data)
    Assertions.assertEquals(1, lists.size)
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