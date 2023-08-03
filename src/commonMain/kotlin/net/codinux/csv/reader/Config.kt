package net.codinux.csv.reader

import net.codinux.csv.writer.LineDelimiter
import net.codinux.csv.writer.QuoteStrategy

/**
 * The default configuration that complies with RFC 4180.
 */
object Config {

  // common config

  const val DefaultFieldSeparator = ','

  const val DefaultQuoteCharacter = '"'

  const val DefaultCommentCharacter = '#'

  // CsvReader config

  const val DefaultSkipEmptyRows = true

  val DefaultCommentStrategy = CommentStrategy.NONE

  const val DefaultErrorOnDifferentFieldCount = false

  const val DefaultHasHeader = false

  const val DefaultIgnoreInvalidQuoteChars = false

  // NamedCsvReader config

  const val NamedCsvReaderDefaultErrorOnDifferentFieldCount = true

  const val NamedCsvReaderDefaultSkipComments = false

  // CsvWriter config

  val DefaultQuoteStrategy = QuoteStrategy.REQUIRED

  val DefaultLineDelimiter = LineDelimiter.CRLF

  const val DefaultBufferSize = 8192

}