package blackbox.reader

import net.codinux.csv.UncheckedIOException
import net.codinux.csv.reader.*
import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.datareader.StringDataReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class CsvReaderTestJvm {

  // API
  @Test
  fun closeApi_Stream() {
    val dataReader = CloseStatusReader(StringDataReader("foo,bar"))
    csvReader(dataReader).stream().use { stream -> stream.forEach {  } }
    kotlin.test.assertTrue(dataReader.isClosed)
  }

  @Test
  fun spliterator() {
    val reader = CsvReader().read("a,b,c\n1,2,3")
    val spliterator = reader.rowSpliterator()
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
    assertEquals(2, CsvReader().read("foo\nfoo").stream().parallel().distinct().count())
  }

  // Coverage
  @Test
  fun streamCloseException() {
    val stream = csvReader(UncloseableReader(StringDataReader("foo"))).stream()
    val e = Assertions.assertThrows(UncheckedIOException::class.java) {
      stream.close()
    }
    assertEquals("net.codinux.csv.IOException: Cannot close", e.message)
  }

  // API
  @Test
  fun closeApi_Stream_WithHeader() {
    val dataReader = CloseStatusReader(StringDataReader("h1,h2\nfoo,bar"))
    csvReaderWithHeader(dataReader).stream().use { stream -> stream.forEach { } }
    Assertions.assertTrue(dataReader.isClosed)
  }

  @Test
  fun spliterator_WithHeader() {
    val spliterator = CsvReader(hasHeaderRow = true).read("a,b,c\n1,2,3\n4,5,6").rowSpliterator()
    Assertions.assertNull(spliterator.trySplit())
    assertEquals(Long.MAX_VALUE, spliterator.estimateSize())
    val rows = AtomicInteger()
    val rows2 = AtomicInteger()
    while (spliterator.tryAdvance { rows.incrementAndGet() }) {
      rows2.incrementAndGet()
    }
    assertEquals(2, rows.get())
    assertEquals(2, rows2.get())
  }

  // Coverage
  @Test
  fun streamCloseException_WithHeader() {
    val csvReader = csvReaderWithHeader(UncloseableReader(StringDataReader("foo")))
    val e = Assertions.assertThrows(UncheckedIOException::class.java) {
      csvReader.stream().close()
    }
    assertEquals("net.codinux.csv.IOException: Cannot close", e.message)
  }


  private fun csvReader(reader: DataReader) = CsvReader().read(reader)

  private fun csvReaderWithHeader(reader: DataReader) = CsvReader(hasHeaderRow = true).read(reader)

}