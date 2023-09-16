package net.codinux.csv.writer

import net.codinux.csv.Config
import net.codinux.csv.writer.datawriter.DataWriter

/**
 * This builder is used to create configured instances of [CsvWriter]. The default
 * configuration of this class complies with RFC 4180.
 */
class CsvFormat(
    private var fieldSeparator: Char = Config.DefaultFieldSeparator,
    private var quoteCharacter: Char = Config.DefaultQuoteCharacter,
    private var commentCharacter: Char = Config.DefaultCommentCharacter,
    private var quoteStrategy: QuoteStrategy = Config.DefaultQuoteStrategy,
    private var lineDelimiter: LineDelimiter = Config.DefaultLineDelimiter,
    private var bufferSize: Int = Config.DefaultBufferSize
) {

    /**
     * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
     */
    constructor() : this(Config.DefaultFieldSeparator)

    /**
     * For programing languages that don't support default parameters like Java, Swift, JavaScript, ...
     */
    constructor(fieldSeparator: Char) : this(fieldSeparator, Config.DefaultQuoteCharacter)

    /**
     * Sets the character that is used to separate columns (default: ',' - comma).
     *
     * @param fieldSeparator the field separator character.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun fieldSeparator(fieldSeparator: Char) = this.apply {
        this.fieldSeparator = fieldSeparator
    }

    /**
     * Sets the character that is used to quote values (default: '"' - double quotes).
     *
     * @param quoteCharacter the character for enclosing fields.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun quoteCharacter(quoteCharacter: Char) = this.apply {
        this.quoteCharacter = quoteCharacter
    }

    /**
     * Sets the character that is used to prepend commented lines (default: '#' - hash/number).
     *
     * @param commentCharacter the character for prepending commented lines.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun commentCharacter(commentCharacter: Char) = this.apply {
        this.commentCharacter = commentCharacter
    }

    /**
     * Sets the strategy that defines when quoting has to be performed
     * (default: [QuoteStrategy.REQUIRED]).
     *
     * @param quoteStrategy the strategy when fields should be enclosed using the `quoteCharacter`.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun quoteStrategy(quoteStrategy: QuoteStrategy) = this.apply {
        this.quoteStrategy = quoteStrategy
    }

    /**
     * Sets the delimiter that is used to separate lines (default: [LineDelimiter.CRLF]).
     *
     * @param lineDelimiter the line delimiter to be used.
     * @return This updated object, so that additional method calls can be chained together.
     */
    fun lineDelimiter(lineDelimiter: LineDelimiter) = this.apply {
        this.lineDelimiter = lineDelimiter
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
    fun bufferSize(bufferSize: Int) = this.apply {
        require(bufferSize >= 0) { "buffer size must be >= 0" }
        this.bufferSize = bufferSize
    }

    fun writer(builder: StringBuilder) = writer(DataWriter.writer(builder))

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
    fun writer(writer: DataWriter): CsvWriter =
        CsvWriter(writer, fieldSeparator, quoteCharacter, commentCharacter, quoteStrategy, lineDelimiter, bufferSize)

    override fun toString(): String {
        return CsvFormat::class.simpleName + "[" +
                "fieldSeparator=$fieldSeparator, " +
                "quoteCharacter=$quoteCharacter, " +
                "commentCharacter=$commentCharacter, " +
                "quoteStrategy=$quoteStrategy, " +
                "lineDelimiter=$lineDelimiter, " +
                "bufferSize=$bufferSize" +
                "]"
    }

}