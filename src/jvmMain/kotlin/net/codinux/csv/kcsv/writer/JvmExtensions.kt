package net.codinux.csv.kcsv.writer

import net.codinux.csv.kcsv.IOException
import net.codinux.csv.kcsv.writer.datawriter.DataWriter
import net.codinux.csv.kcsv.writer.datawriter.JavaIoWriterDataWriter
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path


/**
 * Constructs a [CsvWriter] for the specified Writer.
 *
 *
 * This library uses built-in buffering but writes its internal buffer to the given
 * `writer` on every [CsvWriter.writeRow] or
 * [CsvWriter.writeRow] call. Therefore, you probably want to pass in a
 * [java.io.BufferedWriter] to retain good performance.
 * Use [.build] for optimal performance when writing
 * files.
 *
 * @param writer the Writer to use for writing CSV data.
 * @return a new CsvWriter instance - never `null`.
 * @throws NullPointerException if writer is `null`
 */
fun CsvWriter.CsvWriterBuilder.build(writer: Writer): CsvWriter {
  return build(writer.dataWriter())
}

/**
 * Constructs a [CsvWriter] for the specified Path.
 *
 * @param path        the path to write data to.
 * @param openOptions options specifying how the file is opened.
 * See [Files.newOutputStream] for defaults.
 * @return a new CsvWriter instance - never `null`. Don't forget to close it!
 * @throws IOException          if a write error occurs
 * @throws NullPointerException if path or charset is `null`
 */
fun CsvWriter.CsvWriterBuilder.build(path: Path, vararg openOptions: OpenOption): CsvWriter {
  return build(path, StandardCharsets.UTF_8, *openOptions)
}

/**
 * Constructs a [CsvWriter] for the specified Path.
 *
 * @param path        the path to write data to.
 * @param charset     the character set to be used for writing data to the file.
 * @param openOptions options specifying how the file is opened.
 * See [Files.newOutputStream] for defaults.
 * @return a new CsvWriter instance - never `null`. Don't forget to close it!
 * @throws IOException          if a write error occurs
 * @throws NullPointerException if path or charset is `null`
 */
fun CsvWriter.CsvWriterBuilder.build(path: Path, charset: Charset, vararg openOptions: OpenOption): CsvWriter {
  return build(DataWriter.writer(path, charset, *openOptions))
}

fun DataWriter.Companion.writer(path: Path, charset: Charset = StandardCharsets.UTF_8, vararg openOptions: OpenOption) =
  JavaIoWriterDataWriter(OutputStreamWriter(Files.newOutputStream(path, *openOptions), charset))

fun DataWriter.Companion.writer(writer: Writer) = JavaIoWriterDataWriter(writer)

fun <T : Writer> T.dataWriter() = DataWriter.writer(this)

