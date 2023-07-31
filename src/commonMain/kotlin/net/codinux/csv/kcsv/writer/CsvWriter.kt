package net.codinux.csv.kcsv.writer

import net.codinux.csv.kcsv.Closeable
import net.codinux.csv.kcsv.IOException
import net.codinux.csv.kcsv.UncheckedIOException
import net.codinux.csv.kcsv.writer.datawriter.DataWriter
import kotlin.jvm.JvmStatic

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
  writer: DataWriter, fieldSeparator: Char, quoteCharacter: Char,
  commentCharacter: Char, quoteStrategy: QuoteStrategy, lineDelimiter: LineDelimiter,
  syncWriter: Boolean
) : Closeable {
  private val writer: DataWriter
  private val fieldSeparator: Char
  private val quoteCharacter: Char
  private val commentCharacter: Char
  private val quoteStrategy: QuoteStrategy
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
    this.writer = writer
    this.fieldSeparator = fieldSeparator
    this.quoteCharacter = quoteCharacter
    this.commentCharacter = commentCharacter
    this.quoteStrategy = quoteStrategy
    this.lineDelimiter = lineDelimiter.toString()
    this.syncWriter = syncWriter
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
   * This builder is used to create configured instances of [CsvWriter]. The default
   * configuration of this class complies with RFC 4180.
   */
  class CsvWriterBuilder internal constructor() {
    private var fieldSeparator = ','
    private var quoteCharacter = '"'
    private var commentCharacter = '#'
    private var quoteStrategy = QuoteStrategy.REQUIRED
    private var lineDelimiter = LineDelimiter.CRLF
    private var bufferSize = DEFAULT_BUFFER_SIZE

    /**
     * Sets the character that is used to separate columns (default: ',' - comma).
     *
     * @param fieldSeparator the field separator character.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun fieldSeparator(fieldSeparator: Char): CsvWriterBuilder {
      this.fieldSeparator = fieldSeparator
      return this
    }

    /**
     * Sets the character that is used to quote values (default: '"' - double quotes).
     *
     * @param quoteCharacter the character for enclosing fields.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun quoteCharacter(quoteCharacter: Char): CsvWriterBuilder {
      this.quoteCharacter = quoteCharacter
      return this
    }

    /**
     * Sets the character that is used to prepend commented lines (default: '#' - hash/number).
     *
     * @param commentCharacter the character for prepending commented lines.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun commentCharacter(commentCharacter: Char): CsvWriterBuilder {
      this.commentCharacter = commentCharacter
      return this
    }

    /**
     * Sets the strategy that defines when quoting has to be performed
     * (default: [QuoteStrategy.REQUIRED]).
     *
     * @param quoteStrategy the strategy when fields should be enclosed using the `quoteCharacter`.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun quoteStrategy(quoteStrategy: QuoteStrategy): CsvWriterBuilder {
      this.quoteStrategy = quoteStrategy
      return this
    }

    /**
     * Sets the delimiter that is used to separate lines (default: [LineDelimiter.CRLF]).
     *
     * @param lineDelimiter the line delimiter to be used.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun lineDelimiter(lineDelimiter: LineDelimiter): CsvWriterBuilder {
      this.lineDelimiter = lineDelimiter
      return this
    }

    /**
     * Configures the size of the internal buffer.
     *
     *
     * The default buffer size of 8,192 bytes usually does not need to be altered. One use-case is if you
     * need many instances of a CsvWriter and need to optimize for instantiation time and memory footprint.
     *
     *
     * A buffer size of 0 disables the buffer.
     *
     * @param bufferSize the buffer size to be used (must be  0).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun bufferSize(bufferSize: Int): CsvWriterBuilder {
      require(bufferSize >= 0) { "buffer size must be >= 0" }
      this.bufferSize = bufferSize
      return this
    }

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
    fun build(writer: DataWriter): CsvWriter {
      return newWriter(writer, true)
    }

    private fun newWriter(writer: DataWriter, syncWriter: Boolean): CsvWriter {
      return if (bufferSize > 0) {
        CsvWriter(
          FastBufferedWriter(writer, bufferSize), fieldSeparator, quoteCharacter,
          commentCharacter, quoteStrategy, lineDelimiter, syncWriter
        )
      } else CsvWriter(
        writer, fieldSeparator, quoteCharacter, commentCharacter, quoteStrategy,
        lineDelimiter, false
      )
    }

    override fun toString(): String {
      return CsvWriterBuilder::class.simpleName + "[" +
        "fieldSeparator=$fieldSeparator, " +
        "quoteCharacter=$quoteCharacter, " +
        "commentCharacter=$commentCharacter, " +
        "quoteStrategy=$quoteStrategy, " +
        "lineDelimiter=$lineDelimiter, " +
        "bufferSize=$bufferSize" +
        "]"
    }

    companion object {
      private const val DEFAULT_BUFFER_SIZE = 8192
    }
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

  companion object {
    private const val CR = '\r'
    private const val LF = '\n'

    /**
     * Creates a [CsvWriterBuilder] instance used to configure and create instances of
     * this class.
     *
     * @return CsvWriterBuilder instance with default settings.
     */
    @JvmStatic
    fun builder(): CsvWriterBuilder {
      return CsvWriterBuilder()
    }
  }
}