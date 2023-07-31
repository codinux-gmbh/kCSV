package net.codinux.csv.kcsv.writer

/**
 * The strategies that can be used to quote values when writing CSV data.
 */
enum class QuoteStrategy {
  /**
   * Enclose fields only with quotes if required. That is, if the field contains:
   *
   *  * field separator
   *  * quote character
   *  * comment character
   *  * newline character(s) (CR / LF / CRLF)
   *
   *
   * Empty strings and `null` fields will not be enclosed with quotes.
   */
  REQUIRED,

  /**
   * In addition to fields that require quote enclosing also delimit empty text fields to
   * differentiate between empty and `null` fields.
   * This is required for PostgreSQL CSV imports for example.
   */
  EMPTY,

  /**
   * Enclose any text field with quotes regardless of its content (even empty and `null` fields).
   */
  ALWAYS
}