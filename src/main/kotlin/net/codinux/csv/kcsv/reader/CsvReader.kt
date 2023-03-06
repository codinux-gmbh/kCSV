package net.codinux.csv.kcsv.reader

import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * This is the main class for reading CSV data.
 *
 *
 * Example use:
 * <pre>`try (CsvReader csvReader = CsvReader.builder().build(path)) {
 * for (CsvRow row : csvReader) {
 * ...
 * }
 * }
`</pre> *
 */
class CsvReader(
  private val reader: Reader?,
  private val fieldSeparator: Char = Config.DefaultFieldSeparator,
  private val quoteCharacter: Char = Config.DefaultQuoteCharacter,
  private val commentStrategy: CommentStrategy = Config.DefaultCommentStrategy,
  private val commentCharacter: Char = Config.DefaultCommentCharacter,
  private val skipEmptyRows: Boolean = Config.DefaultSkipEmptyRows,
  private val errorOnDifferentFieldCount: Boolean = Config.DefaultErrorOnDifferentFieldCount,
  private val hasHeader: Boolean = Config.DefaultHasHeader
) : Iterable<CsvRow>, Closeable {

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [CsvReader.builder].
   */
  constructor(reader: Reader) : this(reader, fieldSeparator = Config.DefaultFieldSeparator)

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [CsvReader.builder].
   */
  constructor(data: String) : this(data, fieldSeparator = Config.DefaultFieldSeparator)

  constructor(
    data: String,
    fieldSeparator: Char = Config.DefaultFieldSeparator,
    quoteCharacter: Char = Config.DefaultQuoteCharacter,
    commentStrategy: CommentStrategy = Config.DefaultCommentStrategy,
    commentCharacter: Char = Config.DefaultCommentCharacter,
    skipEmptyRows: Boolean = Config.DefaultSkipEmptyRows,
    errorOnDifferentFieldCount: Boolean = Config.DefaultErrorOnDifferentFieldCount,
    hasHeader: Boolean = Config.DefaultHasHeader
  ) : this(reader(data), fieldSeparator, quoteCharacter, commentStrategy, commentCharacter, skipEmptyRows, errorOnDifferentFieldCount, hasHeader)


  private val rowReader: RowReader
  private val csvRowIterator: CloseableIterator<CsvRow>

  val header: Set<String>

  init {
    assertFields(fieldSeparator, quoteCharacter, commentCharacter)

    rowReader = RowReader(
      reader, fieldSeparator, quoteCharacter, commentStrategy,
      commentCharacter
    )

    csvRowIterator = CsvRowIterator(rowReader, commentStrategy, skipEmptyRows, errorOnDifferentFieldCount)

    header = readHeader()
  }

  private fun assertFields(fieldSeparator: Char, quoteCharacter: Char, commentCharacter: Char) {
    require(!(fieldSeparator == CR || fieldSeparator == LF)) { "fieldSeparator must not be a newline char" }
    require(!(quoteCharacter == CR || quoteCharacter == LF)) { "quoteCharacter must not be a newline char" }
    require(!(commentCharacter == CR || commentCharacter == LF)) { "commentCharacter must not be a newline char" }
    require(!(fieldSeparator == quoteCharacter || fieldSeparator == commentCharacter || quoteCharacter == commentCharacter)) {
      String.format(
        "Control characters must differ"
          + " (fieldSeparator=%s, quoteCharacter=%s, commentCharacter=%s)",
        fieldSeparator, quoteCharacter, commentCharacter
      )
    }
  }

  private fun readHeader() =
    if (hasHeader == false || csvRowIterator.hasNext() == false) {
      emptySet()
    } else {
      val firstRow = csvRowIterator.next()
      val headerSet: MutableSet<String> = LinkedHashSet(firstRow.getFieldCount())
      for (field in firstRow.getFields()) {
        check(headerSet.add(field)) { "Duplicate header field '$field' found" }
      }
      Collections.unmodifiableSet(headerSet)
    }

  override fun iterator(): CloseableIterator<CsvRow> {
    return csvRowIterator
  }

  override fun spliterator(): Spliterator<CsvRow> {
    return CsvRowSpliterator(csvRowIterator)
  }

  /**
   * Creates a new sequential `Stream` from this instance.
   *
   *
   * A close handler is registered by this method in order to close the underlying resources.
   * Don't forget to close the returned stream when you're done.
   *
   * @return a new sequential `Stream`.
   */
  fun stream(): Stream<CsvRow> {
    return StreamSupport.stream(spliterator(), false)
      .onClose {
        try {
          close()
        } catch (e: IOException) {
          throw UncheckedIOException(e)
        }
      }
  }

  @Throws(IOException::class)
  override fun close() {
    csvRowIterator.close()
  }

  override fun toString(): String {
    return StringJoiner(", ", CsvReader::class.java.simpleName + "[", "]")
      .add("commentStrategy=$commentStrategy")
      .add("skipEmptyRows=$skipEmptyRows")
      .add("errorOnDifferentFieldCount=$errorOnDifferentFieldCount")
      .toString()
  }

  /**
   * This builder is used to create configured instances of [CsvReader]. The default
   * configuration of this class complies with RFC 4180.
   *
   *
   * The line delimiter (line-feed, carriage-return or the combination of both) is detected
   * automatically and thus not configurable.
   */
  class CsvReaderBuilder {
    private var fieldSeparator = Config.DefaultFieldSeparator
    private var quoteCharacter = Config.DefaultQuoteCharacter
    private var commentStrategy = Config.DefaultCommentStrategy
    private var commentCharacter = Config.DefaultCommentCharacter
    private var skipEmptyRows = Config.DefaultSkipEmptyRows
    private var errorOnDifferentFieldCount = Config.DefaultErrorOnDifferentFieldCount
    private var hasHeader = Config.DefaultHasHeader

    /**
     * Sets the `fieldSeparator` used when reading CSV data.
     *
     * @param fieldSeparator the field separator character (default: `,` - comma).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun fieldSeparator(fieldSeparator: Char): CsvReaderBuilder {
      this.fieldSeparator = fieldSeparator
      return this
    }

    /**
     * Sets the `quoteCharacter` used when reading CSV data.
     *
     * @param quoteCharacter the character used to enclose fields
     * (default: `"` - double quotes).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun quoteCharacter(quoteCharacter: Char): CsvReaderBuilder {
      this.quoteCharacter = quoteCharacter
      return this
    }

    /**
     * Sets the strategy that defines how (and if) commented lines should be handled
     * (default: [CommentStrategy.NONE] as comments are not defined in RFC 4180).
     *
     * @param commentStrategy the strategy for handling comments.
     * @return This updated object, so that additional method calls can be chained together.
     * @see .commentCharacter
     */
    fun commentStrategy(commentStrategy: CommentStrategy): CsvReaderBuilder {
      this.commentStrategy = commentStrategy
      return this
    }

    /**
     * Sets the `commentCharacter` used to comment lines.
     *
     * @param commentCharacter the character used to comment lines (default: `#` - hash)
     * @return This updated object, so that additional method calls can be chained together.
     * @see .commentStrategy
     */
    fun commentCharacter(commentCharacter: Char): CsvReaderBuilder {
      this.commentCharacter = commentCharacter
      return this
    }

    /**
     * Defines if empty rows should be skipped when reading data.
     *
     * @param skipEmptyRows if empty rows should be skipped (default: `true`).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun skipEmptyRows(skipEmptyRows: Boolean): CsvReaderBuilder {
      this.skipEmptyRows = skipEmptyRows
      return this
    }

    /**
     * Defines if an [MalformedCsvException] should be thrown if lines do contain a
     * different number of columns.
     *
     * @param errorOnDifferentFieldCount if an exception should be thrown, if CSV data contains
     * different field count (default: `false`).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun errorOnDifferentFieldCount(
      errorOnDifferentFieldCount: Boolean
    ): CsvReaderBuilder {
      this.errorOnDifferentFieldCount = errorOnDifferentFieldCount
      return this
    }

    fun hasHeader(hasHeader: Boolean) = this.apply {
      this.hasHeader = hasHeader
    }

    /**
     * Constructs a new [CsvReader] for the specified arguments.
     *
     *
     * This library uses built-in buffering, so you do not need to pass in a buffered Reader
     * implementation such as [java.io.BufferedReader]. Performance may be even likely
     * better if you do not.
     *
     *
     * Use [.build] for optimal performance when
     * reading files and [.build] when reading Strings.
     *
     * @param reader the data source to read from.
     * @return a new CsvReader - never `null`.
     * @throws NullPointerException if reader is `null`
     */
    fun build(reader: Reader?): CsvReader {
      return newReader(Objects.requireNonNull(reader, "reader must not be null"))
    }

    /**
     * Constructs a new [CsvReader] for the specified arguments.
     *
     * @param data    the data to read.
     * @return a new CsvReader - never `null`.
     */
    fun build(data: String): CsvReader {
      return newReader(Objects.requireNonNull(data, "data must not be null"))
    }
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
    @Throws(IOException::class)
    fun build(path: Path?, charset: Charset? = StandardCharsets.UTF_8): CsvReader {
      Objects.requireNonNull(path, "path must not be null")
      Objects.requireNonNull(charset, "charset must not be null")
      return newReader(InputStreamReader(Files.newInputStream(path), charset))
    }

    private fun newReader(reader: Reader?): CsvReader {
      return CsvReader(
        reader, fieldSeparator, quoteCharacter, commentStrategy,
        commentCharacter, skipEmptyRows, errorOnDifferentFieldCount, hasHeader
      )
    }

    private fun newReader(data: String): CsvReader {
      return CsvReader(
        data, fieldSeparator, quoteCharacter, commentStrategy,
        commentCharacter, skipEmptyRows, errorOnDifferentFieldCount, hasHeader
      )
    }

    override fun toString(): String {
      return StringJoiner(", ", CsvReaderBuilder::class.java.simpleName + "[", "]")
        .add("fieldSeparator=$fieldSeparator")
        .add("quoteCharacter=$quoteCharacter")
        .add("commentStrategy=$commentStrategy")
        .add("commentCharacter=$commentCharacter")
        .add("skipEmptyRows=$skipEmptyRows")
        .add("errorOnDifferentFieldCount=$errorOnDifferentFieldCount")
        .add("hasHeader=$hasHeader")
        .toString()
    }
  }

  companion object {
    private const val CR = '\r'
    private const val LF = '\n'

    /**
     * Constructs a [CsvReaderBuilder] to configure and build instances of this class.
     * @return a new [CsvReaderBuilder] instance.
     */
    @JvmStatic
    fun builder(): CsvReaderBuilder {
      return CsvReaderBuilder()
    }

    fun reader(path: Path, charset: Charset = StandardCharsets.UTF_8) =
      InputStreamReader(Files.newInputStream(path), charset)

    fun reader(data: String) =
      StringReader(data)
  }
}