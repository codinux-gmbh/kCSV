@file:JvmName("CsvReaderJvm")

package net.codinux.csv.reader

import net.codinux.csv.reader.datareader.DataReader
import net.codinux.csv.reader.datareader.reader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path

/**
 * Constructs a new [CsvReader] for the specified path using UTF-8 as the character set.
 *
 * @param path    the file to read data from.
 * @return a new CsvReader - never `null`. Don't forget to close it!
 * @throws IOException if an I/O error occurs.
 * @throws NullPointerException if path or charset is `null`
 */
@JvmOverloads
fun CsvReader.read(path: Path, charset: Charset = StandardCharsets.UTF_8) =
  this.read(DataReader.reader(path, charset))

@JvmOverloads
fun CsvReader.read(file: File, charset: Charset = StandardCharsets.UTF_8) =
  this.read(DataReader.reader(file, charset))

@JvmOverloads
fun CsvReader.read(inputStream: InputStream, charset: Charset = StandardCharsets.UTF_8) =
  this.read(DataReader.reader(inputStream, charset))

fun CsvReader.read(reader: Reader) =
  this.read(DataReader.reader(reader))