package net.codinux.csv.writer

import net.codinux.csv.Constants
import kotlin.jvm.JvmStatic

/**
 * Enumeration for different line delimiters (LF, CR, CRLF, platform default).
 */
enum class LineDelimiter(private val str: String) {
  /**
   * Line Feed - (UNIX).
   */
  LF(Constants.LF.toString()),

  /**
   * Carriage Return - (Mac classic).
   */
  CR(Constants.CR.toString()),

  /**
   * Carriage Return and Line Feed (Windows).
   */
  CRLF("${Constants.CR}${Constants.LF}"),

//  /**
//   * Use current platform default ([System.lineSeparator].
//   */
//  PLATFORM(System.lineSeparator())
  ;

  override fun toString(): String {
    return str
  }

  companion object {
    /**
     * Build an enum based on the given string.
     *
     * @param str the string to convert to an enum.
     * @return the enum representation of the given string.
     */
    @JvmStatic
    fun of(str: String): LineDelimiter {
      if ("\r\n" == str) {
        return CRLF
      }
      if ("\n" == str) {
        return LF
      }
      if ("\r" == str) {
        return CR
      }
      throw IllegalArgumentException("Unknown line delimiter: $str")
    }
  }
}