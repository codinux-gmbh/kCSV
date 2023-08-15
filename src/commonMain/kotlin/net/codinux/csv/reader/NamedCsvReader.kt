package net.codinux.csv.reader

import net.codinux.csv.Config
import net.codinux.csv.reader.CsvReader.CsvReaderBuilder
import net.codinux.csv.reader.datareader.DataReader
import kotlin.jvm.JvmStatic

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
class NamedCsvReader private constructor(private val csvReader: CsvReader) {

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [NamedCsvReader.builder].
   */
  constructor() : this(Config.DefaultFieldSeparator)

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [NamedCsvReader.builder].
   */
  constructor(fieldSeparator: Char) : this(fieldSeparator, Config.DefaultQuoteCharacter)

  constructor(
    fieldSeparator: Char = Config.DefaultFieldSeparator,
    quoteCharacter: Char = Config.DefaultQuoteCharacter,
    skipComments: Boolean = Config.NamedCsvReaderDefaultSkipComments,
    commentCharacter: Char = Config.DefaultCommentCharacter
  ) : this(
    CsvReader(fieldSeparator, quoteCharacter, if (skipComments) CommentStrategy.SKIP else CommentStrategy.NONE, commentCharacter,
    errorOnDifferentFieldCount = Config.NamedCsvReaderDefaultErrorOnDifferentFieldCount, hasHeaderRow = true)
  )


  fun read(data: String) = read(DataReader.reader(data))

  fun read(reader: DataReader): NamedCsvRowIterator =
    NamedCsvRowIterator(csvReader.read(reader))

  override fun toString(): String {
    return NamedCsvReader::class.simpleName + "[" +
      "csvReader=$csvReader" +
      "]"
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
     * @return a new NamedCsvReader - never `null`.
     * @throws NullPointerException if reader is `null`
     */
    fun build(): NamedCsvReader {
      return NamedCsvReader(csvReaderBuilder().build())
    }

    private fun csvReaderBuilder(): CsvReaderBuilder {
      return CsvReader.builder()
        .fieldSeparator(fieldSeparator)
        .quoteCharacter(quoteCharacter)
        .commentCharacter(commentCharacter)
        .commentStrategy(if (skipComments) CommentStrategy.SKIP else CommentStrategy.NONE)
        .errorOnDifferentFieldCount(true)
        .hasHeaderRow(true)
        .ignoreInvalidQuoteChars(ignoreInvalidQuoteChars)
    }

    override fun toString(): String {
      return NamedCsvReaderBuilder::class.simpleName + "[" +
        "fieldSeparator=$fieldSeparator, " +
        "quoteCharacter=$quoteCharacter, " +
        "commentCharacter=$commentCharacter, " +
        "skipComments=$skipComments" +
        "]"
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