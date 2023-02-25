package net.codinux.csv.kcsv.writer

import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.*

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
  writer: Writer, fieldSeparator: Char, quoteCharacter: Char,
  commentCharacter: Char, quoteStrategy: QuoteStrategy, lineDelimiter: LineDelimiter,
  syncWriter: Boolean
) : Closeable {
  private val writer: Writer
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
      String.format(
        "Control characters must differ"
          + " (fieldSeparator=%s, quoteCharacter=%s, commentCharacter=%s)",
        fieldSeparator, quoteCharacter, commentCharacter
      )
    }
    this.writer = writer
    this.fieldSeparator = fieldSeparator
    this.quoteCharacter = quoteCharacter
    this.commentCharacter = commentCharacter
    this.quoteStrategy = Objects.requireNonNull(quoteStrategy)
    this.lineDelimiter = Objects.requireNonNull(lineDelimiter).toString()
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
          writer.write(fieldSeparator.code)
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
          writer.write(fieldSeparator.code)
        }
        writeInternal(values[i], i == 0)
      }
      endRow()
      this
    } catch (e: IOException) {
      throw UncheckedIOException(e)
    }
  }

  @Throws(IOException::class)
  private fun writeInternal(value: String?, firstField: Boolean) {
    if (value == null) {
      if (quoteStrategy == QuoteStrategy.ALWAYS) {
        writer.write(quoteCharacter.code)
        writer.write(quoteCharacter.code)
      }
      return
    }
    if (value.isEmpty()) {
      if (quoteStrategy == QuoteStrategy.ALWAYS
        || quoteStrategy == QuoteStrategy.EMPTY
      ) {
        writer.write(quoteCharacter.code)
        writer.write(quoteCharacter.code)
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
      writer.write(quoteCharacter.code)
    }
    if (nextDelimPos > -1) {
      writeEscaped(value, length, nextDelimPos)
    } else {
      writer.write(value, 0, length)
    }
    if (needsQuotes) {
      writer.write(quoteCharacter.code)
    }
  }

  @Throws(IOException::class)
  private fun writeEscaped(value: String, length: Int, nextDelimPos: Int) {
    var nextDelimPos = nextDelimPos
    var startPos = 0
    do {
      val len = nextDelimPos - startPos + 1
      writer.write(value, startPos, len)
      writer.write(quoteCharacter.code)
      startPos += len
      nextDelimPos = -1
      for (i in startPos until length) {
        if (value[i] == quoteCharacter) {
          nextDelimPos = i
          break
        }
      }
    } while (nextDelimPos > -1)
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
      writer.write(commentCharacter.code)
      if (comment != null && !comment.isEmpty()) {
        writeCommentInternal(comment)
      }
      endRow()
      this
    } catch (e: IOException) {
      throw UncheckedIOException(e)
    }
  }

  @Throws(IOException::class)
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
        writer.write(commentCharacter.code)
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
          writer.write(commentCharacter.code)
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

  @Throws(IOException::class)
  private fun endRow() {
    writer.write(lineDelimiter, 0, lineDelimiter.length)
    if (syncWriter) {
      writer.flush()
    }
  }

  @Throws(IOException::class)
  override fun close() {
    writer.close()
  }

  override fun toString(): String {
    return StringJoiner(", ", CsvWriter::class.java.simpleName + "[", "]")
      .add("fieldSeparator=$fieldSeparator")
      .add("quoteCharacter=$quoteCharacter")
      .add("commentCharacter=$commentCharacter")
      .add("quoteStrategy=$quoteStrategy")
      .add("lineDelimiter='$lineDelimiter'")
      .toString()
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
    fun build(writer: Writer): CsvWriter {
      Objects.requireNonNull(writer, "writer must not be null")
      return newWriter(writer, true)
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
    @Throws(IOException::class)
    fun build(path: Path?, vararg openOptions: OpenOption?): CsvWriter {
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
    @Throws(IOException::class)
    fun build(
      path: Path?, charset: Charset,
      vararg openOptions: OpenOption?
    ): CsvWriter {
      Objects.requireNonNull(path, "path must not be null")
      Objects.requireNonNull(charset, "charset must not be null")
      return newWriter(
        OutputStreamWriter(
          Files.newOutputStream(path, *openOptions),
          charset
        ), false
      )
    }

    private fun newWriter(writer: Writer, syncWriter: Boolean): CsvWriter {
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
      return StringJoiner(", ", CsvWriterBuilder::class.java.simpleName + "[", "]")
        .add("fieldSeparator=$fieldSeparator")
        .add("quoteCharacter=$quoteCharacter")
        .add("commentCharacter=$commentCharacter")
        .add("quoteStrategy=$quoteStrategy")
        .add("lineDelimiter=$lineDelimiter")
        .add("bufferSize=$bufferSize")
        .toString()
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
  internal class FastBufferedWriter(private val writer: Writer, bufferSize: Int) : Writer() {
    private val buf: CharArray
    private var pos = 0

    init {
      buf = CharArray(bufferSize)
    }

    @Throws(IOException::class)
    override fun write(c: Int) {
      if (pos == buf.size) {
        flush()
      }
      buf[pos++] = c.toChar()
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
      throw IllegalStateException("Not implemented")
    }

    @Throws(IOException::class)
    override fun write(str: String, off: Int, len: Int) {
      if (pos + len >= buf.size) {
        flush()
        if (len >= buf.size) {
          val tmp = CharArray(len)
          str.toCharArray(tmp, 0, off, off + len)
          writer.write(tmp, 0, len)
          return
        }
      }
      str.toCharArray(buf, pos, off, off + len)
      pos += len
    }

    @Throws(IOException::class)
    override fun flush() {
      writer.write(buf, 0, pos)
      pos = 0
    }

    @Throws(IOException::class)
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