package net.codinux.csv.kcsv.reader

import java.io.Closeable
import java.io.IOException
import java.io.Reader

/*
 * This class contains ugly, performance optimized code - be warned!
 */
class RowReader : Closeable {
  private val rowHandler = RowHandler(32)
  private val buffer: Buffer
  private val reader: Reader?
  private val fsep: Char
  private val qChar: Char
  private val cStrat: CommentStrategy
  private val cChar: Char
  private val ignoreInvalidQuoteChars: Boolean
  private var status = 0
  private var finished = false

  constructor(
    reader: Reader?, fieldSeparator: Char, quoteCharacter: Char,
    commentStrategy: CommentStrategy, commentCharacter: Char, ignoreInvalidQuoteChars: Boolean
  ) {
    buffer = Buffer(reader)
    this.reader = reader
    fsep = fieldSeparator
    qChar = quoteCharacter
    cStrat = commentStrategy
    cChar = commentCharacter
    this.ignoreInvalidQuoteChars = ignoreInvalidQuoteChars
  }

  constructor(
    data: String, fieldSeparator: Char, quoteCharacter: Char,
    commentStrategy: CommentStrategy, commentCharacter: Char, ignoreInvalidQuoteChars: Boolean
  ) {
    buffer = Buffer(data)
    reader = null
    fsep = fieldSeparator
    qChar = quoteCharacter
    cStrat = commentStrategy
    cChar = commentCharacter
    this.ignoreInvalidQuoteChars = ignoreInvalidQuoteChars
  }

  @Throws(IOException::class)
  fun fetchAndRead(): CsvRow? {
    if (finished) {
      return null
    }
    do {
      if (buffer.len == buffer.pos) {
        // cursor reached current EOD -- need to fetch
        if (buffer.fetchData()) {
          // reached end of stream
          if (buffer.begin < buffer.pos || rowHandler.isCommentMode) {
            rowHandler.add(
              materialize(
                buffer.buf, buffer.begin,
                buffer.pos - buffer.begin, status, qChar
              )
            )
          } else if (status and STATUS_NEW_FIELD != 0) {
            rowHandler.add("")
          }
          finished = true
          break
        }
      }
    } while (consume(rowHandler, buffer.buf, buffer.len))
    return rowHandler.buildAndReset()
  }

  private fun consume(rh: RowHandler, lBuf: CharArray, lLen: Int): Boolean {
    var lPos = buffer.pos
    var lBegin = buffer.begin
    var lStatus = status
    var moreDataNeeded = true
    run OUTER@ {
      mode_check@ do {
        if (lStatus and STATUS_QUOTED_MODE != 0) {
          // we're in quotes
          while (lPos < lLen) {
            val c = lBuf[lPos++]
            if (c == qChar) {
              if (ignoreInvalidQuoteChars == false || isInvalidQuoteChar(lPos, lLen, lBuf) == false) {
                lStatus = lStatus and STATUS_QUOTED_MODE.inv()
              }
              continue@mode_check
            } else if (c == CR) {
              lStatus = lStatus or STATUS_LAST_CHAR_WAS_CR
              rh.incLines()
            } else if (c == LF) {
              if (lStatus and STATUS_LAST_CHAR_WAS_CR == 0) {
                rh.incLines()
              } else {
                lStatus = lStatus and STATUS_LAST_CHAR_WAS_CR.inv()
              }
            } else {
              // fast-forward
              while (lPos < lLen) {
                val lookAhead = lBuf[lPos++]
                if (lookAhead == qChar || lookAhead == LF || lookAhead == CR) {
                  lPos--
                  break
                }
              }
            }
          }
        } else if (lStatus and STATUS_COMMENTED_ROW != 0) {
          // commented line
          while (lPos < lLen) {
            val lookAhead = lBuf[lPos++]
            if (lookAhead == CR) {
              rh.add(
                materialize(
                  lBuf, lBegin, lPos - lBegin - 1, lStatus,
                  qChar
                )
              )
              status = STATUS_LAST_CHAR_WAS_CR
              lBegin = lPos
              moreDataNeeded = false
              return@OUTER
            } else if (lookAhead == LF) {
              rh.add(
                materialize(
                  lBuf, lBegin, lPos - lBegin - 1, lStatus,
                  qChar
                )
              )
              status = STATUS_RESET
              lBegin = lPos
              moreDataNeeded = false
              return@OUTER
            }
          }
        } else {
          // we're not in quotes
          while (lPos < lLen) {
            val c = lBuf[lPos++]
            if (c == fsep) {
              rh.add(
                materialize(
                  lBuf, lBegin, lPos - lBegin - 1, lStatus,
                  qChar
                )
              )
              lStatus = STATUS_NEW_FIELD
              lBegin = lPos
            } else if (c == CR) {
              rh.add(
                materialize(
                  lBuf, lBegin, lPos - lBegin - 1, lStatus,
                  qChar
                )
              )
              status = STATUS_LAST_CHAR_WAS_CR
              lBegin = lPos
              moreDataNeeded = false
              return@OUTER
            } else if (c == LF) {
              if (lStatus and STATUS_LAST_CHAR_WAS_CR == 0) {
                rh.add(
                  materialize(
                    lBuf, lBegin, lPos - lBegin - 1,
                    lStatus, qChar
                  )
                )
                status = STATUS_RESET
                lBegin = lPos
                moreDataNeeded = false
                return@OUTER
              }
              lStatus = STATUS_RESET
              lBegin = lPos
            } else if (cStrat != CommentStrategy.NONE && c == cChar && (lStatus == STATUS_RESET || lStatus == STATUS_LAST_CHAR_WAS_CR)) {
              lBegin = lPos
              lStatus = STATUS_COMMENTED_ROW
              rh.enableCommentMode()
              continue@mode_check
            } else if (c == qChar && lStatus and STATUS_DATA_COLUMN == 0) {
              // quote and not in data-only mode
              lStatus = STATUS_QUOTED_COLUMN or STATUS_QUOTED_MODE
              continue@mode_check
            } else {
              if (lStatus and STATUS_QUOTED_COLUMN == 0) {
                // normal unquoted data
                lStatus = STATUS_DATA_COLUMN

                // fast-forward
                while (lPos < lLen) {
                  val lookAhead = lBuf[lPos++]
                  if (lookAhead == fsep || lookAhead == LF || lookAhead == CR) {
                    lPos--
                    break
                  }
                }
              } else {
                // field data after closing quote
              }
            }
          }
        }
      } while (lPos < lLen)
      status = lStatus
    }
    buffer.pos = lPos
    buffer.begin = lBegin
    return moreDataNeeded
  }

  private fun isInvalidQuoteChar(lPos: Int, lLen: Int, lBuf: CharArray): Boolean {
    if (lPos < lLen) {
      val nextChar = lBuf[lPos]
      if (nextChar != fsep && nextChar != CR && nextChar != LF) {
        return true
      }
    }

    return false
  }

  private fun materialize(
    lBuf: CharArray,
    lBegin: Int, lPos: Int, lStatus: Int,
    quoteCharacter: Char
  ): String {
    if (lStatus and STATUS_QUOTED_COLUMN == 0) {
      // column without quotes
      return String(lBuf, lBegin, lPos)
    }

    // column with quotes
    val shift = cleanDelimiters(
      lBuf, lBegin + 1, lBegin + lPos,
      quoteCharacter
    )
    return String(lBuf, lBegin + 1, lPos - 1 - shift)
  }

  private fun cleanDelimiters(
    buf: CharArray, begin: Int, pos: Int,
    quoteCharacter: Char
  ): Int {
    var shift = 0
    var escape = false

    for (i in begin until pos) {
      val c = buf[i]
      if (c == quoteCharacter && (ignoreInvalidQuoteChars == false || i >= pos - 1)) {
        if (!escape) {
          shift++
          escape = true
          continue
        } else {
          escape = false
        }
      }

      if (shift > 0) {
        buf[i - shift] = c
      }
    }

    return shift
  }

  private class Buffer {
    var buf: CharArray
    var len = 0
    var begin = 0
    var pos = 0
    private val reader: Reader?

    internal constructor(reader: Reader?) {
      this.reader = reader
      buf = CharArray(BUFFER_SIZE)
    }

    internal constructor(data: String) {
      reader = null
      buf = data.toCharArray()
      len = data.length
    }

    /**
     * Reads data from the underlying reader and manages the local buffer.
     *
     * @return `true`, if EOD reached.
     * @throws IOException if a read error occurs
     */
    @Throws(IOException::class)
    fun fetchData(): Boolean {
      if (reader == null) {
        return true
      }
      if (begin < pos) {
        // we have data that can be relocated
        if (READ_SIZE > buf.size - pos) {
          // need to relocate data in buffer -- not enough capacity left
          val lenToCopy = pos - begin
          if (READ_SIZE > buf.size - lenToCopy) {
            // need to relocate data in new, larger buffer
            buf = extendAndRelocate(buf, begin)
          } else {
            // relocate data in existing buffer
            System.arraycopy(buf, begin, buf, 0, lenToCopy)
          }
          pos -= begin
          begin = 0
        }
      } else {
        // all data was consumed -- nothing to relocate
        begin = 0
        pos = begin
      }
      val cnt = reader.read(buf, pos, READ_SIZE)
      if (cnt == -1) {
        return true
      }
      len = pos + cnt
      return false
    }

    companion object {
      private const val READ_SIZE = 8192
      private const val BUFFER_SIZE = READ_SIZE
      private const val MAX_BUFFER_SIZE = 8 * 1024 * 1024
      @Throws(IOException::class)
      private fun extendAndRelocate(buf: CharArray, begin: Int): CharArray {
        val newBufferSize = buf.size * 2
        if (newBufferSize > MAX_BUFFER_SIZE) {
          throw IOException(
            "Maximum buffer size " + MAX_BUFFER_SIZE + " is not enough "
              + "to read data of a single field. Typically, this happens if quotation "
              + "started but did not end within this buffer's maximum boundary."
          )
        }
        val newBuf = CharArray(newBufferSize)
        System.arraycopy(buf, begin, newBuf, 0, buf.size - begin)
        return newBuf
      }
    }
  }

  companion object {
    private const val LF = '\n'
    private const val CR = '\r'
    private const val STATUS_LAST_CHAR_WAS_CR = 32
    private const val STATUS_COMMENTED_ROW = 16
    private const val STATUS_NEW_FIELD = 8
    private const val STATUS_QUOTED_MODE = 4
    private const val STATUS_QUOTED_COLUMN = 2
    private const val STATUS_DATA_COLUMN = 1
    private const val STATUS_RESET = 0
  }

  override fun close() {
    reader?.close()
  }
}