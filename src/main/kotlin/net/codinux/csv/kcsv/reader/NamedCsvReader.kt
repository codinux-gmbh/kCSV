package net.codinux.csv.kcsv.reader

import net.codinux.csv.kcsv.reader.CsvReader.Companion.reader
import net.codinux.csv.kcsv.reader.CsvReader.CsvReaderBuilder
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
  constructor(reader: Reader) : this(reader, skipComments = Config.NamedCsvReaderDefaultSkipComments)

  constructor(
    data: String,
    fieldSeparator: Char = Config.DefaultFieldSeparator,
    quoteCharacter: Char = Config.DefaultQuoteCharacter,
    skipComments: Boolean = Config.NamedCsvReaderDefaultSkipComments,
    commentCharacter: Char = Config.DefaultCommentCharacter
  ) : this(reader(data), fieldSeparator, quoteCharacter, skipComments, commentCharacter)

  constructor(
    reader: Reader,
    fieldSeparator: Char = Config.DefaultFieldSeparator,
    quoteCharacter: Char = Config.DefaultQuoteCharacter,
    skipComments: Boolean = Config.NamedCsvReaderDefaultSkipComments,
    commentCharacter: Char = Config.DefaultCommentCharacter
  ) : this(CsvReader(reader, fieldSeparator, quoteCharacter, if (skipComments) CommentStrategy.SKIP else CommentStrategy.NONE, commentCharacter,
    errorOnDifferentFieldCount = Config.NamedCsvReaderDefaultErrorOnDifferentFieldCount, hasHeader = true))

  private val csvIterator: CloseableIterator<CsvRow> = csvReader.iterator()
  private val namedCsvIterator: CloseableIterator<NamedCsvRow> = NamedCsvRowIterator(csvIterator)

  /**
   * Returns the header columns. Can be called at any time.
   *
   * @return the header columns
   */
  val header: Set<String> = csvReader.header

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

  private inner class NamedCsvRowIterator internal constructor(private val csvIterator: CloseableIterator<CsvRow>) : CloseableIterator<NamedCsvRow> {
    override fun hasNext(): Boolean {
      return csvIterator.hasNext()
    }

    override fun next(): NamedCsvRow {
      return NamedCsvRow(header, csvIterator.next())
    }

    @Throws(IOException::class)
    override fun close() {
      csvIterator.close()
    }
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
     * Constructs a new [NamedCsvReader] for the specified arguments.
     *
     * @param path    the file to read data from.
     * @param charset the character set to use.
     * @return a new NamedCsvReader - never `null`. Don't forget to close it!
     * @throws IOException if an I/O error occurs.
     * @throws NullPointerException if path or charset is `null`
     */
    /**
     * Constructs a new [NamedCsvReader] for the specified path using UTF-8 as the character set.
     *
     * @param path    the file to read data from.
     * @return a new NamedCsvReader - never `null`. Don't forget to close it!
     * @throws IOException if an I/O error occurs.
     * @throws NullPointerException if path or charset is `null`
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun build(path: Path?, charset: Charset? = StandardCharsets.UTF_8): NamedCsvReader {
      return NamedCsvReader(csvReaderBuilder().build(path, charset))
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
    fun build(reader: Reader?): NamedCsvReader {
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