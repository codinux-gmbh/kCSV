@file:JvmName("DataReaderJvm")

package net.codinux.csv.reader.datareader

import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

internal fun DataReader.Companion.reader(path: Path, charset: Charset = StandardCharsets.UTF_8) =
  DataReader.reader(Files.newInputStream(path), charset)

internal fun DataReader.Companion.reader(file: File, charset: Charset = StandardCharsets.UTF_8) =
  DataReader.reader(file.inputStream(), charset)

internal fun DataReader.Companion.reader(inputStream: InputStream, charset: Charset = StandardCharsets.UTF_8) =
  DataReader.reader(InputStreamReader(inputStream, charset))

internal fun DataReader.Companion.reader(reader: Reader) = JavaIoReaderDataReader(reader)