package net.codinux.csv.writer

import net.codinux.csv.writer.datawriter.DataWriter
import net.codinux.csv.writer.datawriter.JavaIoWriterDataWriter
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.writer

fun CsvFormat.writer(builder: StringBuilder) =
  writer(DataWriter.writer(builder))

fun CsvFormat.writer(writer: Writer) =
  writer(writer.dataWriter())

@JvmOverloads
fun CsvFormat.writer(outputStream: OutputStream, charset: Charset = StandardCharsets.UTF_8) =
  writer(outputStream.writer(charset))

@JvmOverloads
fun CsvFormat.writer(file: File, charset: Charset = StandardCharsets.UTF_8) =
  writer(file.writer(charset))

@JvmOverloads
fun CsvFormat.writer(path: Path, charset: Charset = StandardCharsets.UTF_8) =
  writer(path.writer(charset))

fun CsvFormat.writer(path: Path, vararg openOptions: OpenOption) =
  writer(path, StandardCharsets.UTF_8, *openOptions)

fun CsvFormat.writer(path: Path, charset: Charset, vararg openOptions: OpenOption) =
  writer(DataWriter.writer(path, charset, *openOptions))

fun DataWriter.Companion.writer(path: Path, charset: Charset = StandardCharsets.UTF_8, vararg openOptions: OpenOption) =
  JavaIoWriterDataWriter(OutputStreamWriter(Files.newOutputStream(path, *openOptions), charset))

fun DataWriter.Companion.writer(writer: Writer) = JavaIoWriterDataWriter(writer)

fun <T : Writer> T.dataWriter() = DataWriter.writer(this)

