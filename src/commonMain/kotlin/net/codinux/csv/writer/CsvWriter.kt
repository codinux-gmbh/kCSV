package net.codinux.csv.writer

import net.codinux.csv.Closeable
import net.codinux.csv.IOException
import net.codinux.csv.UncheckedIOException
import net.codinux.csv.Config
import net.codinux.csv.Constants.CR
import net.codinux.csv.Constants.LF
import net.codinux.csv.writer.datawriter.DataWriter

/**
 * This is the main class for writing CSV data.
 *
 *
 * Example use:
 * <pre>`try (CsvWriter csv = CsvWriter.builder().build(path)) {
 * csv.writeRow("Hello", "world");
 * }
`</pre> *
 */
class CsvWriter internal constructor(
  writer: DataWriter,
  private val fieldSeparator: Char = Config.DefaultFieldSeparator,
  private val quoteCharacter: Char = Config.DefaultQuoteCharacter,
  private val commentCharacter: Char = Config.DefaultCommentCharacter,
  private val quoteStrategy: QuoteStrategy = Config.DefaultQuoteStrategy,
  lineDelimiter: LineDelimiter = Config.DefaultLineDelimiter,
  bufferSize: Int = Config.DefaultBufferSize
) : Closeable {

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [CsvWriter.builder].
   */
  internal constructor(writer: DataWriter) : this(writer, Config.DefaultFieldSeparator)

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [CsvWriter.builder].
   */
  internal constructor(writer: DataWriter, fieldSeparator: Char) : this(writer, fieldSeparator, Config.DefaultQuoteCharacter)

  private val writer: DataWriter
  private val lineDelimiter: String
  private val syncWriter: Boolean

  init {
    require(!(fieldSeparator == CR || fieldSeparator == LF)) { "fieldSeparator must not be a newline char" }
    require(!(quoteCharacter == CR || quoteCharacter == LF)) { "quoteCharacter must not be a newline char" }
    require(!(commentCharacter == CR || commentCharacter == LF)) { "commentCharacter must not be a newline char" }
    require(allDiffers(fieldSeparator, quoteCharacter, commentCharacter)) {
        "Control characters must differ" +
          " (fieldSeparator=$fieldSeparator, quoteCharacter=$quoteCharacter, commentCharacter=$commentCharacter)"
    }

    this.lineDelimiter = lineDelimiter.toString()

    if (bufferSize > 0) {
      this.writer = FastBufferedWriter(writer, bufferSize)
      this.syncWriter = true
    } else {
      this.writer = writer
      this.syncWriter = false
    }
  }

  private fun allDiffers(vararg chars: Char): Boolean {
    for (i in 0 until chars.size - 1) {
      if (chars[i] == chars[i + 1]) {
        return false
      }
    }
    return true
  }

  /**
   * Writes a complete line - one or more fields and new line character(s) at the end.
   *
   * @param values the fields to write (`null` values are handled as empty strings, if
   * not configured otherwise ([QuoteStrategy.EMPTY])).
   * @return This CsvWriter.
   * @throws UncheckedIOException if a write error occurs
   * @see .writeRow
   */
  fun writeRow(values: Iterable<String?>): CsvWriter {
    return try {
      var firstField = true
      for (value in values) {
        if (!firstField) {
          writer.write(fieldSeparator)
        }
        writeInternal(value, firstField)
        firstField = false
      }
      endRow()
      this
    } catch (e: IOException) {
      throw UncheckedIOException(e)
    }
  }

  /**
   * Writes a complete line - one or more fields and new line character(s) at the end.
   *
   * @param values the fields to write (`null` values are handled as empty strings, if
   * not configured otherwise ([QuoteStrategy.EMPTY]))
   * @return This CsvWriter.
   * @throws UncheckedIOException if a write error occurs
   * @see .writeRow
   */
  fun writeRow(vararg values: String?): CsvWriter {
    return try {
      for (i in values.indices) {
        if (i > 0) {
          writer.write(fieldSeparator)
        }
        writeInternal(values[i], i == 0)
      }
      endRow()
      this
    } catch (e: IOException) {
      throw UncheckedIOException(e)
    }
  }

  private fun writeInternal(value: String?, firstField: Boolean) {
    if (value == null) {
      if (quoteStrategy == QuoteStrategy.ALWAYS) {
        writer.write(quoteCharacter)
        writer.write(quoteCharacter)
      }
      return
    }
    if (value.isEmpty()) {
      if (quoteStrategy == QuoteStrategy.ALWAYS
        || quoteStrategy == QuoteStrategy.EMPTY
      ) {
        writer.write(quoteCharacter)
        writer.write(quoteCharacter)
      }
      return
    }
    val length = value.length
    var needsQuotes = quoteStrategy == QuoteStrategy.ALWAYS
    var nextDelimPos = -1
    for (i in 0 until length) {
      val c = value[i]
      if (c == quoteCharacter) {
        needsQuotes = true
        nextDelimPos = i
        break
      }
      if (!needsQuotes && (c == fieldSeparator || c == LF || c == CR || firstField && i == 0 && c == commentCharacter)) {
        needsQuotes = true
      }
    }
    if (needsQuotes) {
      writer.write(quoteCharacter)
    }
    if (nextDelimPos > -1) {
      writeEscaped(value, length, nextDelimPos)
    } else {
      writer.write(value, 0, length)
    }
    if (needsQuotes) {
      writer.write(quoteCharacter)
    }
  }

  private fun writeEscaped(value: String, length: Int, nextDelimPos: Int) {
    var nextDelimiterPos = nextDelimPos
    var startPos = 0
    do {
      val len = nextDelimiterPos - startPos + 1
      writer.write(value, startPos, len)
      writer.write(quoteCharacter)
      startPos += len
      nextDelimiterPos = -1
      for (i in startPos until length) {
        if (value[i] == quoteCharacter) {
          nextDelimiterPos = i
          break
        }
      }
    } while (nextDelimiterPos > -1)
    if (length > startPos) {
      writer.write(value, startPos, length - startPos)
    }
  }

  /**
   * Writes a comment line and new line character(s) at the end.
   *
   * @param comment the comment to write. The comment character
   * (configured by [CsvWriterBuilder.commentCharacter]) is automatically prepended.
   * Empty or `null` values results in a line only consisting of the comment character.
   * If the argument `comment` contains line break characters (CR, LF), multiple comment lines
   * will be written, terminated with the line break character configured by
   * [CsvWriterBuilder.lineDelimiter].
   *
   * @return This CsvWriter.
   * @throws UncheckedIOException if a write error occurs
   */
  fun writeComment(comment: String?): CsvWriter {
    return try {
      writer.write(commentCharacter)
      if (comment != null && !comment.isEmpty()) {
        writeCommentInternal(comment)
      }
      endRow()
      this
    } catch (e: IOException) {
      throw UncheckedIOException(e)
    }
  }

  private fun writeCommentInternal(comment: String) {
    val length = comment.length
    var startPos = 0
    var lastCharWasCR = false
    for (i in 0 until comment.length) {
      val c = comment[i]
      if (c == CR) {
        val len = i - startPos
        writer.write(comment, startPos, len)
        writer.write(lineDelimiter, 0, lineDelimiter.length)
        writer.write(commentCharacter)
        startPos += len + 1
        lastCharWasCR = true
      } else if (c == LF) {
        if (lastCharWasCR) {
          lastCharWasCR = false
          startPos++
        } else {
          val len = i - startPos
          writer.write(comment, startPos, len)
          writer.write(lineDelimiter, 0, lineDelimiter.length)
          writer.write(commentCharacter)
          startPos += len + 1
        }
      } else {
        lastCharWasCR = false
      }
    }
    if (length > startPos) {
      writer.write(comment, startPos, length - startPos)
    }
  }

  private fun endRow() {
    writer.write(lineDelimiter, 0, lineDelimiter.length)
    if (syncWriter) {
      writer.flush()
    }
  }

  override fun close() {
    writer.close()
  }

  override fun toString(): String {
    return CsvWriter::class.simpleName + "[" +
      "fieldSeparator=$fieldSeparator, " +
      "quoteCharacter=$quoteCharacter, " +
      "commentCharacter=$commentCharacter, " +
      "quoteStrategy=$quoteStrategy, " +
      "lineDelimiter='$lineDelimiter'" +
      "]"
  }

  /**
   * Unsynchronized and thus high performance replacement for BufferedWriter.
   *
   *
   * This class is intended for internal use only.
   */
  internal class FastBufferedWriter(private val writer: DataWriter, bufferSize: Int) : DataWriter {
    private val buf: CharArray
    private var pos = 0

    init {
      buf = CharArray(bufferSize)
    }

    override fun write(char: Char) {
      if (pos == buf.size) {
        flush()
      }
      buf[pos++] = char
    }

    override fun write(charArray: CharArray, offset: Int, length: Int) {
      writer.write(charArray, offset, length)
    }

    override fun write(string: String, offset: Int, length: Int) {
      if (pos + length >= buf.size) {
        flush()
        if (length >= buf.size) {
          val tmp = string.toCharArray(offset, offset + length)
          writer.write(tmp, 0, length)
          return
        }
      }

      string.toCharArray(offset, offset + length).copyInto(buf, pos)
      pos += length
    }

    override fun flush() {
      writer.write(buf, 0, pos)
      pos = 0
    }

    override fun close() {
      flush()
      writer.close()
    }
  }
}