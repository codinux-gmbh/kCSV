package net.codinux.csv

import net.codinux.csv.reader.CommentStrategy
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

  const val DefaultHasHeaderRow = false

  const val DefaultReuseRowInstance = false

  val DefaultIgnoreColumns = emptySet<Int>()

  const val DefaultIgnoreInvalidQuoteChars = false

  // CsvWriter config

  val DefaultQuoteStrategy = QuoteStrategy.REQUIRED

  val DefaultLineDelimiter = LineDelimiter.CRLF

  const val DefaultBufferSize = 8192

}