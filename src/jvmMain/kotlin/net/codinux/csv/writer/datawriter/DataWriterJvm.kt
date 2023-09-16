@file:JvmName("DataWriterJvm")

package net.codinux.csv.writer.datawriter

import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path

internal fun DataWriter.Companion.writer(path: Path, charset: Charset = StandardCharsets.UTF_8, vararg openOptions: OpenOption) =
  JavaIoWriterDataWriter(OutputStreamWriter(Files.newOutputStream(path, *openOptions), charset))

internal fun DataWriter.Companion.writer(writer: Writer) = JavaIoWriterDataWriter(writer)

internal fun DataWriter.Companion.writer(appendable: Appendable) = JavaAppendableDataWriter(appendable)