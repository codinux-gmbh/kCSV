package net.codinux.csv.writer

import net.codinux.csv.writer.CsvWriter.CsvWriterBuilder
import net.codinux.csv.writer.datawriter.DataWriter
import net.codinux.csv.writer.datawriter.writer
import java.io.File
import java.io.OutputStream
import java.io.Writer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.OpenOption
import java.nio.file.Path
import kotlin.io.path.writer

fun CsvWriterBuilder.writer(builder: Appendable) =
  writer(DataWriter.writer(builder))

fun CsvWriterBuilder.writer(writer: Writer) =
  writer(DataWriter.writer(writer))

@JvmOverloads
fun CsvWriterBuilder.writer(outputStream: OutputStream, charset: Charset = StandardCharsets.UTF_8) =
  writer(outputStream.writer(charset))

@JvmOverloads
fun CsvWriterBuilder.writer(file: File, charset: Charset = StandardCharsets.UTF_8) =
  writer(file.writer(charset))

@JvmOverloads
fun CsvWriterBuilder.writer(path: Path, charset: Charset = StandardCharsets.UTF_8) =
  writer(path.writer(charset))

fun CsvWriterBuilder.writer(path: Path, vararg openOptions: OpenOption) =
  writer(path, StandardCharsets.UTF_8, *openOptions)

fun CsvWriterBuilder.writer(path: Path, charset: Charset = StandardCharsets.UTF_8, vararg openOptions: OpenOption) =
  writer(path.writer(charset, *openOptions))

