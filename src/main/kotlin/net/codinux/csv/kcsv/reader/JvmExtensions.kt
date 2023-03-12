package net.codinux.csv.kcsv.reader

import net.codinux.csv.kcsv.reader.CsvReader
import net.codinux.csv.kcsv.reader.NamedCsvReader
import net.codinux.csv.kcsv.reader.datareader.DataReader
import net.codinux.csv.kcsv.reader.datareader.IoReaderDataReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

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
  IoReaderDataReader(InputStreamReader(Files.newInputStream(path), charset))

fun DataReader.Companion.reader(reader: Reader) = IoReaderDataReader(reader)