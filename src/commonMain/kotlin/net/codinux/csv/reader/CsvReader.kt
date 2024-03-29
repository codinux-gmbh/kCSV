package net.codinux.csv.reader

import net.codinux.csv.Config
import net.codinux.csv.Constants.CR
import net.codinux.csv.Constants.LF
import net.codinux.csv.reader.datareader.DataReader
import kotlin.jvm.JvmStatic

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
  private val fieldSeparator: Char = Config.DefaultFieldSeparator,
  private val hasHeaderRow: Boolean = Config.DefaultHasHeaderRow,
  private val quoteCharacter: Char = Config.DefaultQuoteCharacter,
  private val commentCharacter: Char = Config.DefaultCommentCharacter,
  private val commentStrategy: CommentStrategy = Config.DefaultCommentStrategy,
  private val skipEmptyRows: Boolean = Config.DefaultSkipEmptyRows,
  private val reuseRowInstance: Boolean = Config.DefaultReuseRowInstance,
  /**
   * Specify the column indices whose values can be ignored to save memory and CPU for copying them
   * into RAM (saves quite a lot RAM on larger files).
   */
  private val ignoreColumns: Set<Int> = Config.DefaultIgnoreColumns,
  private val errorOnDifferentFieldCount: Boolean = Config.DefaultErrorOnDifferentFieldCount,
  private val ignoreInvalidQuoteChars: Boolean = Config.DefaultIgnoreInvalidQuoteChars
) {

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [CsvReader.builder].
   */
  constructor() : this(Config.DefaultFieldSeparator)

  /**
   * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
   *
   * To set individual options better use [CsvReader.builder].
   */
  constructor(fieldSeparator: Char = Config.DefaultFieldSeparator) : this(fieldSeparator, Config.DefaultHasHeaderRow)


  init {
    require(!(fieldSeparator == CR || fieldSeparator == LF)) { "fieldSeparator must not be a newline char" }
    require(!(quoteCharacter == CR || quoteCharacter == LF)) { "quoteCharacter must not be a newline char" }
    require(!(commentCharacter == CR || commentCharacter == LF)) { "commentCharacter must not be a newline char" }
    require(!(fieldSeparator == quoteCharacter || fieldSeparator == commentCharacter || quoteCharacter == commentCharacter)) {
        "Control characters must differ" +
          " (fieldSeparator=$fieldSeparator, quoteCharacter=$quoteCharacter, commentCharacter=$commentCharacter)"
    }
  }


  fun read(data: String) = read(DataReader.reader(data))

  internal fun read(reader: DataReader): CsvRowIterator {
    val rowReader = RowReader(reader, fieldSeparator, quoteCharacter, commentStrategy, commentCharacter, hasHeaderRow, reuseRowInstance, ignoreColumns, ignoreInvalidQuoteChars)

    return CsvRowIterator(rowReader, commentStrategy, skipEmptyRows, errorOnDifferentFieldCount, hasHeaderRow)
  }


  override fun toString(): String {
    return CsvReader::class.simpleName + "[" +
      "commentStrategy=$commentStrategy, " +
      "skipEmptyRows=$skipEmptyRows, " +
      "errorOnDifferentFieldCount=$errorOnDifferentFieldCount" +
      "]"
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
    private var hasHeaderRow = Config.DefaultHasHeaderRow
    private var reuseRowInstance = Config.DefaultReuseRowInstance
    private var ignoreColumns = Config.DefaultIgnoreColumns
    private var ignoreInvalidQuoteChars = Config.DefaultIgnoreInvalidQuoteChars

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

    fun hasHeaderRow(hasHeaderRow: Boolean) = this.apply {
      this.hasHeaderRow = hasHeaderRow
    }

    fun reuseRowInstance(reuseRowInstance: Boolean) = this.apply {
      this.reuseRowInstance = reuseRowInstance
    }

    /**
     * Set indices of columns their values are ignored to save memory and CPU of copying their values into RAM.
     */
    fun ignoreColumns(ignoreColumns: Set<Int>) = this.apply {
      this.ignoreColumns = ignoreColumns
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
     * @return a new CsvReader - never `null`.
     * @throws NullPointerException if reader is `null`
     */
    fun build(): CsvReader {
      return CsvReader(
        fieldSeparator, hasHeaderRow, quoteCharacter,
        commentCharacter, commentStrategy, skipEmptyRows,
        reuseRowInstance, ignoreColumns, errorOnDifferentFieldCount, ignoreInvalidQuoteChars
      )
    }

    override fun toString(): String {
      return CsvReaderBuilder::class.simpleName + "[" +
        "fieldSeparator=$fieldSeparator, " +
        "quoteCharacter=$quoteCharacter, " +
        "commentStrategy=$commentStrategy, " +
        "commentCharacter=$commentCharacter, " +
        "skipEmptyRows=$skipEmptyRows, " +
        "errorOnDifferentFieldCount=$errorOnDifferentFieldCount, " +
        "hasHeaderRow=$hasHeaderRow" +
        "]"
    }
  }

  companion object {
    /**
     * Constructs a [CsvReaderBuilder] to configure and build instances of this class.
     * @return a new [CsvReaderBuilder] instance.
     */
    @JvmStatic
    fun builder(): CsvReaderBuilder {
      return CsvReaderBuilder()
    }
  }
}