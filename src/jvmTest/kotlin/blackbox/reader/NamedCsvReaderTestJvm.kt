package blackbox.reader

import net.codinux.csv.UncheckedIOException
import net.codinux.csv.reader.NamedCsvReader
import net.codinux.csv.reader.NamedCsvRow
import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.datareader.StringDataReader
import net.codinux.csv.reader.spliterator
import net.codinux.csv.reader.stream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

class NamedCsvReaderTestJvm {

  // API
  @Test
  fun closeApi_Stream() {
    val dataReader = CloseStatusReader(StringDataReader("h1,h2\nfoo,bar"))
    namedCsvReader(dataReader).stream().use { stream -> stream.forEach { } }
    Assertions.assertTrue(dataReader.isClosed)
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
  fun streamCloseException() {
    val csvReader = namedCsvReader(UncloseableReader(StringDataReader("foo")))
    val e = Assertions.assertThrows(UncheckedIOException::class.java) {
      csvReader.stream().close()
    }
    Assertions.assertEquals("net.codinux.csv.IOException: Cannot close", e.message)
  }


  private fun namedCsvReader(reader: DataReader) = NamedCsvReader(reader)

  // test helpers
  private fun parse(data: String): NamedCsvReader {
    return NamedCsvReader(data)
  }

  private fun readAll(data: String): List<NamedCsvRow> {
    return parse(data).toList()
  }
}