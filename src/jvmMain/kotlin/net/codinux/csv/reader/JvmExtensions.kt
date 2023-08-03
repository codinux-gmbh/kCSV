package net.codinux.csv.reader

import net.codinux.csv.UncheckedIOException
import net.codinux.csv.reader.CsvReader
import net.codinux.csv.reader.NamedCsvReader
import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.datareader.JavaIoReaderDataReader
//import net.codinux.csv.reader.datareader.JavaIoReaderDataReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.math.BigDecimal
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * Constructs a new [CsvReader] for the specified arguments.
 *
 * @param path    the file to read data from.
 * @param charset the character set to use.
 * @return a new CsvReader - never `null`. Don't forget to close it!
 * @throws IOException if an I/O error occurs.
 * @throws NullPointerException if path or charset is `null`
 */
/**
 * Constructs a new [CsvReader] for the specified path using UTF-8 as the character set.
 *
 * @param path    the file to read data from.
 * @return a new CsvReader - never `null`. Don't forget to close it!
 * @throws IOException if an I/O error occurs.
 * @throws NullPointerException if path or charset is `null`
 */
@JvmOverloads
fun CsvReader.CsvReaderBuilder.build(path: Path, charset: Charset = StandardCharsets.UTF_8): CsvReader {
  return build(DataReader.reader(path, charset))
}


/**
 * Constructs a new [NamedCsvReader] for the specified arguments.
 *
 * @param path    the file to read data from.
 * @param charset the character set to use.
 * @return a new NamedCsvReader - never `null`. Don't forget to close it!
 * @throws IOException if an I/O error occurs.
 * @throws NullPointerException if path or charset is `null`
 */
/**
 * Constructs a new [NamedCsvReader] for the specified path using UTF-8 as the character set.
 *
 * @param path    the file to read data from.
 * @return a new NamedCsvReader - never `null`. Don't forget to close it!
 * @throws IOException if an I/O error occurs.
 * @throws NullPointerException if path or charset is `null`
 */
@JvmOverloads
fun NamedCsvReader.NamedCsvReaderBuilder.build(path: Path, charset: Charset = StandardCharsets.UTF_8): NamedCsvReader {
  return build(DataReader.reader(path, charset))
}

fun DataReader.Companion.reader(path: Path, charset: Charset = StandardCharsets.UTF_8) =
  JavaIoReaderDataReader(InputStreamReader(Files.newInputStream(path), charset))

fun DataReader.Companion.reader(reader: Reader) = JavaIoReaderDataReader(reader)

fun <T : Reader> T.dataReader() = DataReader.reader(this)


fun CsvRow.getBigDecimal(fieldIndex: Int): BigDecimal =
  this.getString(fieldIndex).toBigDecimal()

fun CsvRow.getBigDecimalOrNull(fieldIndex: Int): BigDecimal? =
  this.getStringOrNull(fieldIndex)?.toBigDecimalOrNull()

fun NamedCsvRow.getBigDecimal(name: String): BigDecimal =
  this.getString(name).toBigDecimal()

fun NamedCsvRow.getBigDecimalOrNull(name: String): BigDecimal? =
  this.getStringOrNull(name)?.toBigDecimalOrNull()



fun CsvReader.rowSpliterator(): Spliterator<CsvRow> {
  return CsvRowSpliterator(iterator())
}

/**
 * Creates a new sequential `Stream` from this instance.
 *
 *
 * A close handler is registered by this method in order to close the underlying resources.
 * Don't forget to close the returned stream when you're done.
 *
 * @return a new sequential `Stream`.
 */
fun CsvReader.stream(): Stream<CsvRow> {
  return StreamSupport.stream(rowSpliterator(), false)
    .onClose {
      try {
        close()
      } catch (e: net.codinux.csv.IOException) {
        throw UncheckedIOException(e)
      }
    }
}

fun NamedCsvReader.rowSpliterator(): Spliterator<NamedCsvRow> {
  return CsvRowSpliterator(iterator())
}

/**
 * Creates a new sequential `Stream` from this instance.
 *
 *
 * A close handler is registered by this method in order to close the underlying resources.
 * Don't forget to close the returned stream when you're done.
 *
 * @return a new sequential `Stream`.
 */
fun NamedCsvReader.stream(): Stream<NamedCsvRow> {
  return StreamSupport.stream(rowSpliterator(), false).onClose {
    try {
      close()
    } catch (e: net.codinux.csv.IOException) {
      throw UncheckedIOException(e)
    }
  }
}