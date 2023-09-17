package blackbox.reader

import blackbox.Util
import io.kotest.core.spec.style.FunSpec
import net.codinux.csv.Constants.CR
import net.codinux.csv.Constants.LF
import net.codinux.csv.UncheckedIOException
import net.codinux.csv.reader.*
import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.datareader.StringDataReader
import net.codinux.csv.use
import test.assertElementsEqual
import kotlin.test.*

class CsvReaderTest : FunSpec({

  // parameterized tests

  listOf(CR, LF).forEachIndexed { index, char ->
    test("[$index] configBuilder for '$char'") {
      val e = assertFailsWith(IllegalArgumentException::class) { CsvReader(fieldSeparator = char) }
      assertEquals("fieldSeparator must not be a newline char", e.message)
      val e2 = assertFailsWith(IllegalArgumentException::class) { CsvReader(quoteCharacter = char) }
      assertEquals("quoteCharacter must not be a newline char", e2.message)
      val e3 = assertFailsWith(IllegalArgumentException::class) { CsvReader(commentCharacter = char) }
      assertEquals("commentCharacter must not be a newline char", e3.message)
    }
  }

  listOf(
    CsvReader.builder().quoteCharacter(','),
    CsvReader.builder().commentCharacter(','),
    CsvReader.builder().quoteCharacter('#').commentCharacter('#')
  ).forEachIndexed { index, csvReaderBuilder ->
    test("[$index] configReader for $csvReaderBuilder") {
      val e = assertFailsWith(IllegalArgumentException::class) { csvReaderBuilder.build() }
      assertTrue(e.message!!.contains("Control characters must differ"))
    }
  }

}) {
  
  private val crb = CsvReader.builder()

  @Test
  fun empty() {
    val it: Iterator<CsvRow> = CsvReader().read("").iterator()
    assertFalse(it.hasNext())
    assertFailsWith(NoSuchElementException::class) { it.next() }
  }

  @Test
  fun immutableResponse() {
    val fields = CsvReader().read("foo").iterator().next().fields
    assertFailsWith(ClassCastException::class) { (fields as MutableList).add("bar") }
  }

  // toString()
  @Test
  fun readerToString() {
    assertEquals(
      "CsvReader[commentStrategy=NONE, skipEmptyRows=true, "
        + "errorOnDifferentFieldCount=false]", CsvReader().toString()
    )
  }

  // skipped rows
  @Test
  fun singleRowNoSkipEmpty() {
    val reader = CsvReader(skipEmptyRows = false).read("")
    assertFalse(reader.iterator().hasNext())
  }

  @Test
  fun multipleRowsNoSkipEmpty() {
    val reader = CsvReader(skipEmptyRows = false).read("\n\na")
    val it: Iterator<CsvRow> = reader.iterator()
    var row = it.next()
    assertTrue(row.isEmpty)
    assertEquals(1, row.fieldCount)
    assertEquals(1, row.originalLineNumber)
    assertElementsEqual(listOf(""), row.fields)

    row = it.next()
    assertTrue(row.isEmpty)
    assertEquals(1, row.fieldCount)
    assertEquals(2, row.originalLineNumber)
    assertElementsEqual(listOf(""), row.fields)

    row = it.next()
    assertFalse(row.isEmpty)
    assertEquals(1, row.fieldCount)
    assertEquals(3, row.originalLineNumber)
    assertElementsEqual(listOf("a"), row.fields)
    assertFalse(it.hasNext())
  }

  @Test
  fun skippedRows() {
    val csv = readAll("\n\nfoo\n\nbar\n\n")
    assertEquals(2, csv.size)
    val it = csv.iterator()
    var row = it.next()
    assertEquals(3, row.originalLineNumber)
    assertElementsEqual(listOf("foo"), row.fields)
    row = it.next()
    assertEquals(5, row.originalLineNumber)
    assertElementsEqual(listOf("bar"), row.fields)
  }

  // different field count
  @Test
  fun differentFieldCountSuccess() {
    crb.errorOnDifferentFieldCount(true)
    // asserts that no exception gets thrown
    readAll("foo\nbar")
    readAll("foo\nbar\n")
    readAll("foo,bar\nfaz,baz")
    readAll("foo,bar\nfaz,baz\n")
    readAll("foo,bar\n,baz")
    readAll(",bar\nfaz,baz")
  }

  @Test
  fun differentFieldCountFail() {
    val reader = CsvReader(errorOnDifferentFieldCount = true).read("foo\nbar,\"baz\nbax\"")
    val e = assertFailsWith(MalformedCsvException::class) {
      reader.toList()
    }
    assertEquals("Row 2 has 2 fields, but first row had 1 fields", e.message)
  }

  @Test
  fun hasHeader() {
    val reader = CsvReader(hasHeaderRow = true).read("h1,h2,h3\n1,2,3")

    assertElementsEqual(reader.header, setOf("h1", "h2", "h3"))
  }

  @Test
  fun reuseRowInstanceTrue() {
    val reader = CsvReader.builder().hasHeaderRow(true).reuseRowInstance(true).build().read("h1,h2\n1,2\n3,4\n5,6")
    var firstRowCsvRowInstance: CsvRow = CsvRow(emptySet(), emptyArray(), -1, true, true)

    reader.forEachIndexed { index, csvRow ->
      if (index == 0) {
        firstRowCsvRowInstance = csvRow
        assertEquals(1, csvRow.getInt("h1"))
        assertEquals(2, csvRow.getInt("h2"))
      } else if (index == 1) {
        assertEquals(firstRowCsvRowInstance, csvRow)
        assertEquals(3, csvRow.getInt("h1"))
        assertEquals(4, csvRow.getInt("h2"))
      } else  {
        assertEquals(firstRowCsvRowInstance, csvRow)
        assertEquals(5, csvRow.getInt("h1"))
        assertEquals(6, csvRow.getInt("h2"))
      }
    }
  }

  @Test
  fun reuseRowInstanceFalse() {
    val reader = CsvReader.builder().hasHeaderRow(true).reuseRowInstance(false).build().read("h1,h2\n1,2\n3,4\n5,6")
    var firstRowCsvRowInstance: CsvRow = CsvRow(emptySet(), emptyArray(), -1, true, true)

    reader.forEachIndexed { index, csvRow ->
      if (index == 0) {
        firstRowCsvRowInstance = csvRow
        assertEquals(1, csvRow.getInt("h1"))
        assertEquals(2, csvRow.getInt("h2"))
      } else if (index == 1) {
        assertNotEquals(firstRowCsvRowInstance, csvRow)
        assertEquals(3, csvRow.getInt("h1"))
        assertEquals(4, csvRow.getInt("h2"))
      } else  {
        assertNotEquals(firstRowCsvRowInstance, csvRow)
        assertEquals(5, csvRow.getInt("h1"))
        assertEquals(6, csvRow.getInt("h2"))
      }
    }
  }

  @Test
  fun ignoreInvalidQuoteChars() {
    val reader = crb
      .ignoreInvalidQuoteChars(true)
      .build()
      .read("\"de:14628:1148:1\",\"Ri. \"Am Windberg\"\",\"51,002455\"\n")
    val row = reader.iterator().next()
    assertEquals("Ri. \"Am Windberg\"", row.getField(1))
    assertEquals("de:14628:1148:1", row.getField(0))
    assertEquals("51,002455", row.getField(2))
  }

  @Ignore // does not work yet
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
    val reader = crb
      .ignoreInvalidQuoteChars(true)
      .build()
      .read(csvData.toString())
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
    val reader = crb
      .ignoreInvalidQuoteChars(true)
      .build()
      .read(csvData.toString())
    val row = reader.iterator().next()
    val cell = row.getField(0)
    assertEquals(bufferSize - 2, cell.length)
    assertTrue(cell.endsWith("aaaa"))
    assertEquals("Some more data in next cell", row.getField(1))
  }

  @Test
  fun nonExistingFieldByIndex() { // field by index
      assertFailsWith(IndexOutOfBoundsException::class) {
        spotbugs(readSingleRow("foo").getField(1))
      }
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
    val it: Iterator<CsvRow> = CsvReader(commentStrategy = CommentStrategy.SKIP).read(data).iterator()
    var row = it.next()
    assertElementsEqual(listOf("line 1"), row.fields)
    assertEquals(1, row.originalLineNumber)
    row = it.next()
    assertElementsEqual(listOf("line 2"), row.fields)
    assertEquals(2, row.originalLineNumber)
    row = it.next()
    assertElementsEqual(listOf("line 3"), row.fields)
    assertEquals(3, row.originalLineNumber)
    row = it.next()
    assertElementsEqual(listOf("line 4\rwith\r\nand\n"), row.fields)
    assertEquals(4, row.originalLineNumber)
    row = it.next()
    assertElementsEqual(listOf("line 9"), row.fields)
    assertEquals(9, row.originalLineNumber)
    assertFalse(it.hasNext())
  }

  // comment
  @Test
  fun comment() {
    val it: Iterator<CsvRow> = CsvReader(commentStrategy = CommentStrategy.READ).read("#comment \"1\"\na,#b,c").iterator()
    var row = it.next()
    assertTrue(row.isComment)
    assertEquals(1, row.originalLineNumber)
    assertElementsEqual(listOf("comment \"1\""), row.fields)
    row = it.next()
    assertFalse(row.isComment)
    assertEquals(2, row.originalLineNumber)
    assertElementsEqual(listOf("a", "#b", "c"), row.fields)
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
    buf.fill('X')
//    System.arraycopy(extra, 0, buf, 8190, extra.size)
    extra.copyInto(buf, 8190, 0, extra.size)

    val row = csvReader(buf).iterator().next()
    assertEquals(4, row.fieldCount)
    assertEquals("a\"b\"c", row.getField(1))
    assertEquals("d", row.getField(2))
    assertEquals("XX", row.getField(3))
  }

  // buffer exceed
  @Test
  fun bufferExceed() {
    val buf = CharArray(8 * 1024 * 1024)
    buf.fill('X')
    buf[buf.size - 1] = ','
    csvReader(buf).iterator().next()
    buf[buf.size - 1] = Char('X'.code.toByte().toUShort())
    val exception = assertFailsWith(UncheckedIOException::class) { csvReader(UnbufferedStringDataReader(buf)).iterator().next() }
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
    buf.fill('X')
    val s = "a,b,c\n\""
    s.toCharArray().copyInto(buf, 0, 0, s.length)

    val iterator = csvReader(UnbufferedStringDataReader(buf)).iterator()
    iterator.next()
    val exception = assertFailsWith(UncheckedIOException::class) { iterator.next() }
    assertEquals("IOException when reading record that started in line 2", exception.message)
    assertEquals(
      "Maximum buffer size 8388608 is not enough to read data of a single field. "
              + "Typically, this happens if quotation started but did not end within this buffer's "
              + "maximum boundary.",
      exception.cause!!.message
    )
  }

  // get fields by header name

  @Test
  fun duplicateHeader() {
    val e = assertFailsWith(IllegalStateException::class) { parseWithHeader("a,b,a").header }
    assertEquals("Duplicate header field 'a' found", e.message)
  }

  @Test
  fun onlyHeader() {
    val csv = parseWithHeader("foo,bar\n")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    assertFalse(csv.iterator().hasNext())
    assertFailsWith(NoSuchElementException::class) { csv.iterator().next() }
  }

  @Test
  fun onlyHeaderIterator() {
    val csv = parseWithHeader("foo,bar\n")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    assertFalse(csv.iterator().hasNext())
  }

  @Test
  fun fieldByName() {
    assertEquals("bar", parseWithHeader("foo\nbar").iterator().next().getField("foo"))
  }

  @Test
  fun header() {
    assertElementsEqual(Util.asArray("foo"), parseWithHeader("foo\nbar").header.toTypedArray())
    val reader = parseWithHeader("foo,bar\n1,2")
    assertElementsEqual(Util.asArray("foo", "bar"), reader.header.toTypedArray())

    // second call
    assertElementsEqual(Util.asArray("foo", "bar"), reader.header.toTypedArray())
  }

  @Test
  fun headerEmptyRows() {
    val csv = parseWithHeader("foo,bar")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    val it = csv.iterator()
    assertFalse(it.hasNext())
    assertFailsWith(NoSuchElementException::class) { it.next() }
  }

  @Test
  fun headerAfterSkippedRow() {
    val csv = parseWithHeader("\nfoo,bar")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    val it = csv.iterator()
    assertFalse(it.hasNext())
  }

  @Test
  fun headerWithoutNextRowCall() {
    assertElementsEqual(Util.asArray("foo"), parseWithHeader("foo\n").header.toTypedArray())
  }

  @Test
  fun findNonExistingFieldByName() {
    val e = assertFailsWith(NoSuchElementException::class) { parseWithHeader("foo\nfaz").iterator().next().getField("bar") }
    assertEquals("No element with name 'bar' found. Valid names are: [foo]", e.message)
  }

  @Test
  fun toStringWithHeader() {
    val csvRow = parseWithHeader("headerA,headerB,headerC\nfieldA,fieldB,fieldC\n").iterator()
    assertEquals(
      "CsvRow[originalLineNumber=2, fields=[fieldA, fieldB, fieldC], comment=false]",
      csvRow.next().toString()
    )
  }

  @Test
  fun fieldMap() {
    val iterator = parseWithHeader(
      """
  headerA,headerB,headerC
  fieldA,fieldB,fieldC
  
  """.trimIndent()
    )
      .iterator()
    assertEquals(
      "[fieldA, fieldB, fieldC]",
      iterator.next().fields.toString()
    )
  }

  // line numbering
  @Test
  fun lineNumberingByHeaderName() {
    val iterator = parseWithHeader(
      """
            h1,h2
            a,line 2
            b,line 3
            c,line 4
            d,"line 5
            with
            and
            "
            e,line 9
            """.trimIndent()
    ).iterator()
    var row = iterator.next()
    assertEquals("a", row.getField("h1"))
    assertEquals(2, row.originalLineNumber)
    row = iterator.next()
    assertEquals("b", row.getField("h1"))
    assertEquals(3, row.originalLineNumber)
    row = iterator.next()
    assertEquals("c", row.getField("h1"))
    assertEquals(4, row.originalLineNumber)
    row = iterator.next()
    assertEquals("d", row.getField("h1"))
    assertEquals(5, row.originalLineNumber)
    row = iterator.next()
    assertEquals("e", row.getField("h1"))
    assertEquals(9, row.originalLineNumber)
    assertFalse(iterator.hasNext())
  }

  // API
  @Test
  fun closeApi_Reader() {
    val dataReader = CloseStatusReader(StringDataReader("foo,bar"))
    csvReader(dataReader).use { it.forEach { } }
    assertTrue(dataReader.isClosed)
  }
  
  @Test
  fun closeApi_Iterator() {
    val dataReader = CloseStatusReader(StringDataReader("foo,bar"))
    csvReader(dataReader).iterator().use { it.forEach {  } }
    assertTrue(dataReader.isClosed)
  }

  @Test
  fun closeStringNoException() {
    CsvReader().read("foo").close()
  }

  @Test
  fun unreadable() {
    val e = assertFailsWith(UncheckedIOException::class) { csvReader(UnreadableReader()).iterator().next() }
    assertEquals("IOException when reading first record", e.message)
  }


  // test helpers
  private fun readSingleRow(data: String): CsvRow {
    val lists = readAll(data)
    assertEquals(1, lists.size)
    return lists[0]
  }

  // test helpers
  private fun parseWithHeader(data: String) =
    CsvReader(hasHeaderRow = true).read(data)

  private fun readAll(data: String): List<CsvRow> {
    return CsvReader().read(data).toList()
  }


  companion object {
    private fun csvReader(data: CharArray) = CsvReader().read(data.concatToString())

    private fun csvReader(reader: DataReader) = CsvReader().read(reader)
  }

}