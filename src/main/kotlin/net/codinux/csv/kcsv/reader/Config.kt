package net.codinux.csv.kcsv.reader

/**
 * The default configuration that complies with RFC 4180.
 */
object Config {

  const val DefaultFieldSeparator = ','

  const val DefaultQuoteCharacter = '"'

  const val DefaultCommentCharacter = '#'

  const val DefaultSkipEmptyRows = true

  val DefaultCommentStrategy = CommentStrategy.NONE

  const val DefaultErrorOnDifferentFieldCount = false

  const val NamedCsvReaderDefaultErrorOnDifferentFieldCount = true

  const val NamedCsvReaderDefaultSkipComments = false

}