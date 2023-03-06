package blackbox.reader

import blackbox.Util
import net.codinux.csv.kcsv.reader.CloseableIterator
import net.codinux.csv.kcsv.reader.NamedCsvReader
import net.codinux.csv.kcsv.reader.NamedCsvRow
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.StringReader
import java.io.UncheckedIOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier

class NamedCsvReaderTest {
  private val crb = NamedCsvReader.builder()
  @Test
  fun empty() {
    val parse = parse("")
    Assertions.assertArrayEquals(arrayOfNulls<String>(0), parse.header.toTypedArray())
    val it: Iterator<NamedCsvRow> = parse.iterator()
    Assertions.assertFalse(it.hasNext())
    Assertions.assertThrows(NoSuchElementException::class.java) { it.next() }
  }

  // toString()
  @Test
  fun readerToString() {
    Assertions.assertEquals(
      "NamedCsvReader[header=[h1], csvReader=CsvReader["
        + "commentStrategy=NONE, skipEmptyRows=true, errorOnDifferentFieldCount=true]]",
      NamedCsvReader("h1\nd1").toString()
    )
  }

  @Test
  fun duplicateHeader() {
    val e = Assertions.assertThrows(IllegalStateException::class.java) { parse("a,b,a").header }
    Assertions.assertEquals("Duplicate header field 'a' found", e.message)
  }

  @Test
  fun onlyHeader() {
    val csv = parse("foo,bar\n")
    Assertions.assertArrayEquals(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    Assertions.assertFalse(csv.iterator().hasNext())
    Assertions.assertThrows(NoSuchElementException::class.java) { csv.iterator().next() }
  }

  @Test
  fun onlyHeaderIterator() {
    val csv = parse("foo,bar\n")
    Assertions.assertArrayEquals(Util.asArray("foo", "bar"), csv.header.toTypedArray())
    Assertions.assertFalse(csv.iterator().hasNext())
  }

  @get:Test
  val fieldByName: Unit
    get() {
      Assertions.assertEquals("bar", parse("foo\nbar").iterator().next().getField("foo"))
    }

  @get:Test
  val header: Unit
    get() {
      Assertions.assertArrayEquals(Util.asArray("foo"), parse("foo\nbar").header.toTypedArray())
      val reader = parse("foo,bar\n1,2")
      Assertions.assertArrayEquals(Util.asArray("foo", "bar"), reader.header.toTypedArray())

      // second call
      Assertions.assertArrayEquals(Util.asArray("foo", "bar"), reader.header.toTypedArray())
    }

  @get:Test
  val headerEmptyRows: Unit
    get() {
      val csv = parse("foo,bar")
      Assertions.assertArrayEquals(Util.asArray("foo", "bar"), csv.header.toTypedArray())
      val it: Iterator<NamedCsvRow> = csv.iterator()
      Assertions.assertFalse(it.hasNext())
      Assertions.assertThrows(NoSuchElementException::class.java) { it.next() }
    }

  @get:Test
  val headerAfterSkippedRow: Unit
    get() {
      val csv = parse("\nfoo,bar")
      Assertions.assertArrayEquals(Util.asArray("foo", "bar"), csv.header.toTypedArray())
      val it: Iterator<NamedCsvRow> = csv.iterator()
      Assertions.assertFalse(it.hasNext())
    }

  @get:Test
  val headerWithoutNextRowCall: Unit
    get() {
      Assertions.assertArrayEquals(Util.asArray("foo"), parse("foo\n").header.toTypedArray())
    }

  @Test
  fun findNonExistingFieldByName() {
    val e = Assertions.assertThrows(NoSuchElementException::class.java) { parse("foo\nfaz").iterator().next().getField("bar") }
    Assertions.assertEquals(
      "No element with name 'bar' found. Valid names are: [foo]",
      e.message
    )
  }

  @Test
  fun toStringWithHeader() {
    val csvRow: Iterator<NamedCsvRow> = parse("headerA,headerB,headerC\nfieldA,fieldB,fieldC\n").iterator()
    Assertions.assertEquals(
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
    Assertions.assertEquals(
      "{headerA=fieldA, headerB=fieldB, headerC=fieldC}",
      it.next().fields.toString()
    )
  }

  // line numbering
  @Test
  fun lineNumbering() {
    val it: Iterator<NamedCsvRow> = NamedCsvReader(
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
    Assertions.assertEquals("a", row.getField("h1"))
    Assertions.assertEquals(2, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals("b", row.getField("h1"))
    Assertions.assertEquals(3, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals("c", row.getField("h1"))
    Assertions.assertEquals(4, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals("d", row.getField("h1"))
    Assertions.assertEquals(5, row.originalLineNumber)
    row = it.next()
    Assertions.assertEquals("e", row.getField("h1"))
    Assertions.assertEquals(9, row.originalLineNumber)
    Assertions.assertFalse(it.hasNext())
  }

  // API
  @Test
  @Throws(IOException::class)
  fun closeApi() {
    val consumer = Consumer { csvRow: NamedCsvRow? -> }
    val supp = Supplier { CloseStatusReader(StringReader("h1,h2\nfoo,bar")) }
    var csr = supp.get()
    NamedCsvReader(csr).use { reader -> reader.forEach(consumer) }
    Assertions.assertTrue(csr.isClosed)
    csr = supp.get()
    NamedCsvReader(csr).iterator().use { it.forEachRemaining(consumer) }
    Assertions.assertTrue(csr.isClosed)
    csr = supp.get()
    NamedCsvReader(csr).stream().use { stream -> stream.forEach(consumer) }
    Assertions.assertTrue(csr.isClosed)
  }

  @Test
  fun noComments() {
    val data = readAll("# comment 1\nfieldA")
    Assertions.assertEquals("fieldA", data.iterator().next().getField("# comment 1"))
  }

  @Test
  fun spliterator() {
    val spliterator = NamedCsvReader("a,b,c\n1,2,3\n4,5,6").spliterator()
    Assertions.assertNull(spliterator.trySplit())
    Assertions.assertEquals(Long.MAX_VALUE, spliterator.estimateSize())
    val rows = AtomicInteger()
    val rows2 = AtomicInteger()
    while (spliterator.tryAdvance { row: NamedCsvRow? -> rows.incrementAndGet() }) {
      rows2.incrementAndGet()
    }
    Assertions.assertEquals(2, rows.get())
    Assertions.assertEquals(2, rows2.get())
  }

  // Coverage
  @Test
  fun closeException() {
    val csvReader = NamedCsvReader(UncloseableReader(StringReader("foo")))
    val e = Assertions.assertThrows(
      UncheckedIOException::class.java
    ) { csvReader.stream().close() }
    Assertions.assertEquals("java.io.IOException: Cannot close", e.message)
  }

  // test helpers
  private fun parse(data: String): NamedCsvReader {
    return NamedCsvReader(data)
  }

  private fun readAll(data: String): List<NamedCsvRow> {
    return parse(data).toList()
  }
}