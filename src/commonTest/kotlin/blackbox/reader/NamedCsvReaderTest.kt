package blackbox.reader

import test.assertElementsEqual
import blackbox.Util
import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.datareader.StringDataReader
import net.codinux.csv.reader.NamedCsvReader
import net.codinux.csv.reader.NamedCsvRow
import net.codinux.csv.use
import kotlin.test.*

class NamedCsvReaderTest {
  
  @Test
  fun empty() {
    val parse = parse("")
    assertElementsEqual(arrayOfNulls<String>(0), parse.header.toTypedArray())
    val it: Iterator<NamedCsvRow> = parse.iterator()
    assertFalse(it.hasNext())
    assertFailsWith(NoSuchElementException::class) { it.next() }
  }

  // toString()
  @Test
  fun readerToString() {
    assertEquals(
      "NamedCsvReader[csvReader=CsvReader["
        + "commentStrategy=NONE, skipEmptyRows=true, errorOnDifferentFieldCount=true]]",
      NamedCsvReader().toString()
    )
  }

  @Test
  fun duplicateHeader() {
    val e = assertFailsWith(IllegalStateException::class) { parse("a,b,a").header }
    assertEquals("Duplicate header field 'a' found", e.message)
  }

  @Test
  fun onlyHeader() {
    val csv = parse("foo,bar\n")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    assertFalse(csv.iterator().hasNext())
    assertFailsWith(NoSuchElementException::class) { csv.iterator().next() }
  }

  @Test
  fun onlyHeaderIterator() {
    val csv = parse("foo,bar\n")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    assertFalse(csv.iterator().hasNext())
  }

  @Test
  fun fieldByName() {
    assertEquals("bar", parse("foo\nbar").iterator().next().getField("foo"))
  }

  @Test
  fun header() {
    assertElementsEqual(Util.asArray("foo"), parse("foo\nbar").header.toTypedArray())
    val reader = parse("foo,bar\n1,2")
    assertElementsEqual(Util.asArray("foo", "bar"), reader.header.toTypedArray())

    // second call
    assertElementsEqual(Util.asArray("foo", "bar"), reader.header.toTypedArray())
  }

  @Test
  fun headerEmptyRows() {
    val csv = parse("foo,bar")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    val it: Iterator<NamedCsvRow> = csv.iterator()
    assertFalse(it.hasNext())
    assertFailsWith(NoSuchElementException::class) { it.next() }
  }

  @Test
  fun headerAfterSkippedRow() {
    val csv = parse("\nfoo,bar")
    assertElementsEqual(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    val it: Iterator<NamedCsvRow> = csv.iterator()
    assertFalse(it.hasNext())
  }

  @Test
  fun headerWithoutNextRowCall() {
      assertElementsEqual(Util.asArray("foo"), parse("foo\n").header.toTypedArray())
    }

  @Test
  fun findNonExistingFieldByName() {
    val e = assertFailsWith(NoSuchElementException::class) { parse("foo\nfaz").iterator().next().getField("bar") }
    assertEquals("No element with name 'bar' found. Valid names are: [foo]", e.message)
  }

  @Test
  fun toStringWithHeader() {
    val csvRow: Iterator<NamedCsvRow> = parse("headerA,headerB,headerC\nfieldA,fieldB,fieldC\n").iterator()
    assertEquals(
      "NamedCsvRow[originalLineNumber=2, "
        + "fieldMap={headerA=fieldA, headerB=fieldB, headerC=fieldC}]",
      csvRow.next().toString()
    )
  }

  @Test
  fun fieldMap() {
    val it: Iterator<NamedCsvRow> = parse(
      """
  headerA,headerB,headerC
  fieldA,fieldB,fieldC
  
  """.trimIndent()
    )
      .iterator()
    assertEquals(
      "{headerA=fieldA, headerB=fieldB, headerC=fieldC}",
      it.next().fields.toString()
    )
  }

  // line numbering
  @Test
  fun lineNumbering() {
    val it: Iterator<NamedCsvRow> = NamedCsvReader().read(
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
    var row = it.next()
    assertEquals("a", row.getField("h1"))
    assertEquals(2, row.originalLineNumber)
    row = it.next()
    assertEquals("b", row.getField("h1"))
    assertEquals(3, row.originalLineNumber)
    row = it.next()
    assertEquals("c", row.getField("h1"))
    assertEquals(4, row.originalLineNumber)
    row = it.next()
    assertEquals("d", row.getField("h1"))
    assertEquals(5, row.originalLineNumber)
    row = it.next()
    assertEquals("e", row.getField("h1"))
    assertEquals(9, row.originalLineNumber)
    assertFalse(it.hasNext())
  }

  // API
  @Test
  fun closeApi_Closable() {
    val dataReader = CloseStatusReader(StringDataReader("h1,h2\nfoo,bar"))
    namedCsvReader(dataReader).use { reader -> reader.forEach { } }
    assertTrue(dataReader.isClosed)
  }

  @Test
  fun closeApi_Iterator() {
    val dataReader = CloseStatusReader(StringDataReader("h1,h2\nfoo,bar"))
    namedCsvReader(dataReader).iterator().use { it.forEach {  } }
    assertTrue(dataReader.isClosed)
  }

  @Test
  fun noComments() {
    val data = readAll("# comment 1\nfieldA")
    assertEquals("fieldA", data.iterator().next().getField("# comment 1"))
  }


  private fun namedCsvReader(reader: DataReader) = NamedCsvReader().read(reader)

  // test helpers
  private fun parse(data: String) =
    NamedCsvReader().read(data)

  private fun readAll(data: String): List<NamedCsvRow> {
    return parse(data).toList()
  }
}