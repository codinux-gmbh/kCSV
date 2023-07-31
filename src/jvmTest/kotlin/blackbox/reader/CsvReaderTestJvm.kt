package blackbox.reader

import net.codinux.csv.kcsv.UncheckedIOException
import net.codinux.csv.kcsv.reader.CsvReader
import net.codinux.csv.kcsv.reader.CsvRow
import net.codinux.csv.kcsv.reader.datareader.DataReader
import net.codinux.csv.kcsv.reader.datareader.StringDataReader
import net.codinux.csv.kcsv.reader.spliterator
import net.codinux.csv.kcsv.reader.stream
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
    val reader = CsvReader("a,b,c\n1,2,3")
    val spliterator = reader.spliterator()
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
  fun streamCloseException() {
    val stream = csvReader(UncloseableReader(StringDataReader("foo"))).stream()
    val e = Assertions.assertThrows(UncheckedIOException::class.java) {
      stream.close()
    }
    assertEquals("net.codinux.csv.kcsv.IOException: Cannot close", e.message)
  }


  private fun csvReader(reader: DataReader) = CsvReader(reader)

}