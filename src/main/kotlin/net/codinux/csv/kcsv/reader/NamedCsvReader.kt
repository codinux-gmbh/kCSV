package net.codinux.csv.kcsv.reader

import net.codinux.csv.kcsv.reader.CsvReader.CsvReaderBuilder
import net.codinux.csv.kcsv.reader.datareader.DataReader
import net.codinux.csv.kcsv.reader.datareader.DataReader.Companion.reader
import java.io.Closeable
import java.io.IOException
import java.io.Reader
import java.io.UncheckedIOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * Header name based Csv reader implementation.
 *
 *
 * Example use:
 * <pre>`try (NamedCsvReader csvReader = NamedCsvReader.builder().build(path)) {
 * for (NamedCsvRow row : csvReader) {
 * ...
 * }
 * }
`</pre> *
 */
class NamedCsvReader private constructor(private val csvReader: CsvReader) : Iterable<NamedCsvRow>, Closeable {

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [NamedCsvReader.builder].
   */
  constructor(data: String) : this(data, skipComments = Config.NamedCsvReaderDefaultSkipComments)

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [NamedCsvReader.builder].
   */
  constructor(reader: DataReader) : this(reader, skipComments = Config.NamedCsvReaderDefaultSkipComments)

  constructor(
    data: String,
    fieldSeparator: Char = Config.DefaultFieldSeparator,
    quoteCharacter: Char = Config.DefaultQuoteCharacter,
    skipComments: Boolean = Config.NamedCsvReaderDefaultSkipComments,
    commentCharacter: Char = Config.DefaultCommentCharacter
  ) : this(reader(data), fieldSeparator, quoteCharacter, skipComments, commentCharacter)

  constructor(
    reader: DataReader,
    fieldSeparator: Char = Config.DefaultFieldSeparator,
    quoteCharacter: Char = Config.DefaultQuoteCharacter,
    skipComments: Boolean = Config.NamedCsvReaderDefaultSkipComments,
    commentCharacter: Char = Config.DefaultCommentCharacter
  ) : this(CsvReader(reader, fieldSeparator, quoteCharacter, if (skipComments) CommentStrategy.SKIP else CommentStrategy.NONE, commentCharacter,
    errorOnDifferentFieldCount = Config.NamedCsvReaderDefaultErrorOnDifferentFieldCount, hasHeader = true))

  /**
   * Returns the header columns. Can be called at any time.
   *
   * @return the header columns
   */
  val header: Set<String> = csvReader.header

  private val csvIterator: CloseableIterator<CsvRow> = csvReader.iterator()
  private val namedCsvIterator: CloseableIterator<NamedCsvRow> = NamedCsvRowIterator(csvIterator, header)

  override fun iterator(): CloseableIterator<NamedCsvRow> = namedCsvIterator

  override fun spliterator(): Spliterator<NamedCsvRow> {
    return CsvRowSpliterator(iterator())
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
  fun stream(): Stream<NamedCsvRow> {
    return StreamSupport.stream(spliterator(), false).onClose {
      try {
        close()
      } catch (e: IOException) {
        throw UncheckedIOException(e)
      }
    }
  }

  @Throws(IOException::class)
  override fun close() {
    csvReader.close()
  }

  override fun toString(): String {
    return StringJoiner(", ", NamedCsvReader::class.java.simpleName + "[", "]")
      .add("header=$header")
      .add("csvReader=$csvReader")
      .toString()
  }

  /**
   * This builder is used to create configured instances of [NamedCsvReader]. The default
   * configuration of this class complies with RFC 4180.
   *
   *
   * The line delimiter (line-feed, carriage-return or the combination of both) is detected
   * automatically and thus not configurable.
   */
  class NamedCsvReaderBuilder {
    private var fieldSeparator = Config.DefaultFieldSeparator
    private var quoteCharacter = Config.DefaultQuoteCharacter
    private var commentCharacter = Config.DefaultCommentCharacter
    private var skipComments = Config.NamedCsvReaderDefaultSkipComments
    private var ignoreInvalidQuoteChars = Config.DefaultIgnoreInvalidQuoteChars

    /**
     * Sets the `fieldSeparator` used when reading CSV data.
     *
     * @param fieldSeparator the field separator character (default: `,` - comma).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun fieldSeparator(fieldSeparator: Char): NamedCsvReaderBuilder {
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
    fun quoteCharacter(quoteCharacter: Char): NamedCsvReaderBuilder {
      this.quoteCharacter = quoteCharacter
      return this
    }

    /**
     * Sets the `commentCharacter` used to comment lines.
     *
     * @param commentCharacter the character used to comment lines (default: `#` - hash)
     * @return This updated object, so that additional method calls can be chained together.
     * @see .skipComments
     */
    fun commentCharacter(commentCharacter: Char): NamedCsvReaderBuilder {
      this.commentCharacter = commentCharacter
      return this
    }

    /**
     * Defines if commented rows should be detected and skipped when reading data.
     *
     * @param skipComments if commented rows should be skipped (default: `true`).
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun skipComments(skipComments: Boolean): NamedCsvReaderBuilder {
      this.skipComments = skipComments
      return this
    }

    /**
     * Defines if invalid placed quote chars like "\"Contains \" in cell content\"" should be ignored.
     *
     * @param ignoreInvalidQuoteChars If true invalid placed quote chars will be ignored
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun ignoreInvalidQuoteChars(ignoreInvalidQuoteChars: Boolean) = this.apply {
      this.ignoreInvalidQuoteChars = ignoreInvalidQuoteChars
    }

    /**
     * Constructs a new [NamedCsvReader] for the specified arguments.
     *
     *
     * This library uses built-in buffering, so you do not need to pass in a buffered Reader
     * implementation such as [java.io.BufferedReader]. Performance may be even likely
     * better if you do not.
     * Use [.build] for optimal performance when
     * reading files and [.build] when reading Strings.
     *
     * @param reader the data source to read from.
     * @return a new NamedCsvReader - never `null`.
     * @throws NullPointerException if reader is `null`
     */
    fun build(reader: DataReader): NamedCsvReader {
      return NamedCsvReader(csvReaderBuilder().build(reader))
    }

    /**
     * Constructs a new [NamedCsvReader] for the specified arguments.
     *
     * @param data    the data to read.
     * @return a new NamedCsvReader - never `null`.
     */
    fun build(data: String): NamedCsvReader {
      return NamedCsvReader(csvReaderBuilder().build(data))
    }

    private fun csvReaderBuilder(): CsvReaderBuilder {
      return CsvReader.builder()
        .fieldSeparator(fieldSeparator)
        .quoteCharacter(quoteCharacter)
        .commentCharacter(commentCharacter)
        .commentStrategy(if (skipComments) CommentStrategy.SKIP else CommentStrategy.NONE)
        .errorOnDifferentFieldCount(true)
        .hasHeader(true)
        .ignoreInvalidQuoteChars(ignoreInvalidQuoteChars)
    }

    override fun toString(): String {
      return StringJoiner(", ", NamedCsvReaderBuilder::class.java.simpleName + "[", "]")
        .add("fieldSeparator=$fieldSeparator")
        .add("quoteCharacter=$quoteCharacter")
        .add("commentCharacter=$commentCharacter")
        .add("skipComments=$skipComments")
        .toString()
    }
  }

  companion object {
    /**
     * Constructs a [NamedCsvReaderBuilder] to configure and build instances of this class.
     * @return a new [NamedCsvReaderBuilder] instance.
     */
    @JvmStatic
    fun builder(): NamedCsvReaderBuilder {
      return NamedCsvReaderBuilder()
    }
  }
}