package net.codinux.csv.kcsv.reader

/**
 * This strategy is used for handling comments in (non RFC conforming) CSV data.
 */
enum class CommentStrategy {
  /**
   * Don't detect comments - handle everything as regular cell content.
   */
  NONE,

  /**
   * Detect comments but do not return commented lines.
   */
  SKIP,

  /**
   * Detect and return the commented lines (entire line as one field).
   * The comment character itself will be stripped.
   */
  READ
}