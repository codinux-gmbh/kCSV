package net.codinux.csv.reader

import net.codinux.csv.Closeable
import net.codinux.csv.Constants.CR
import net.codinux.csv.Constants.LF
import net.codinux.csv.IOException
import net.codinux.csv.reader.datareader.DataReader

/*
 * This class contains ugly, performance optimized code - be warned!
 */
class RowReader internal constructor(
  private val reader: DataReader,
  private val fieldSeparator: Char,
  private val quoteCharacter: Char,
  private val commentStrategy: CommentStrategy,
  private val commentCharacter: Char,
  hasHeaderRow: Boolean,
  reuseRowInstance: Boolean,
  ignoreColumns: Set<Int>,
  private val ignoreInvalidQuoteChars: Boolean
) : Closeable {

  private val buffer: Buffer = Buffer(reader)

  private val rowHandler = RowHandler(32, hasHeaderRow, reuseRowInstance, ignoreColumns, ignoreInvalidQuoteChars)

  private var status = 0

  private var finished = false

  fun setHeader(header: Set<String>) {
    rowHandler.header = header
  }

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
            rowHandler.add(buffer.buf, buffer.begin, buffer.pos, status, quoteCharacter)
          } else if (status and STATUS_NEW_FIELD != 0) {
            rowHandler.addEmptyField()
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
            if (c == quoteCharacter) {
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
                if (lookAhead == quoteCharacter || lookAhead == LF || lookAhead == CR) {
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
              rh.add(lBuf, lBegin, lPos - 1, lStatus, quoteCharacter)
              status = STATUS_LAST_CHAR_WAS_CR
              lBegin = lPos
              moreDataNeeded = false
              return@OUTER
            } else if (lookAhead == LF) {
              rh.add(lBuf, lBegin, lPos - 1, lStatus, quoteCharacter)
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
            if (c == fieldSeparator) {
              rh.add(lBuf, lBegin, lPos - 1, lStatus, quoteCharacter)
              lStatus = STATUS_NEW_FIELD
              lBegin = lPos
            } else if (c == CR) {
              rh.add(lBuf, lBegin, lPos - 1, lStatus, quoteCharacter)
              status = STATUS_LAST_CHAR_WAS_CR
              lBegin = lPos
              moreDataNeeded = false
              return@OUTER
            } else if (c == LF) {
              if (lStatus and STATUS_LAST_CHAR_WAS_CR == 0) {
                rh.add(lBuf, lBegin, lPos - 1, lStatus, quoteCharacter)
                status = STATUS_RESET
                lBegin = lPos
                moreDataNeeded = false
                return@OUTER
              }
              lStatus = STATUS_RESET
              lBegin = lPos
            } else if (commentStrategy != CommentStrategy.NONE && c == commentCharacter && (lStatus == STATUS_RESET || lStatus == STATUS_LAST_CHAR_WAS_CR)) {
              lBegin = lPos
              lStatus = STATUS_COMMENTED_ROW
              rh.enableCommentMode()
              continue@mode_check
            } else if (c == quoteCharacter && lStatus and STATUS_DATA_COLUMN == 0) {
              // quote and not in data-only mode
              lStatus = STATUS_QUOTED_COLUMN or STATUS_QUOTED_MODE
              continue@mode_check
            } else {
              if (lStatus and STATUS_QUOTED_COLUMN == 0) {
                // TODO: may implement to ignore byte order mark (\uFEFF) if it's the first character in stream
                // normal unquoted data
                lStatus = STATUS_DATA_COLUMN

                // fast-forward
                while (lPos < lLen) {
                  val lookAhead = lBuf[lPos++]
                  if (lookAhead == fieldSeparator || lookAhead == LF || lookAhead == CR) {
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
      if (nextChar != fieldSeparator && nextChar != CR && nextChar != LF) {
        return true
      }
    }

    return false
  }

  private class Buffer(reader: DataReader) {
    var buf: CharArray
    var len = 0
    var begin = 0
    var pos = 0
    private val reader: DataReader?

    init {
      if (reader.areAllDataBuffered) {
        this.reader = null
        buf = reader.getBufferedData()!!
        len = buf.size
      } else {
        this.reader = reader
        buf = CharArray(BUFFER_SIZE)
      }
    }

    /**
     * Reads data from the underlying reader and manages the local buffer.
     *
     * @return `true`, if EOD reached.
     * @throws IOException if a read error occurs
     */
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
            // System.arraycopy(buf, begin, buf, 0, lenToCopy);
            // iterating over indices is way faster than buf.copyInto(buf, 0, begin, begin + lenToCopy), see ArrayCopyBenchmark
            for (index in 0 until lenToCopy) {
              buf[index] = buf[begin + index]
            }
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
        // System.arraycopy(buf, begin, newBuf, 0, buf.length - begin);
        buf.copyInto(newBuf, 0, begin, buf.size)
        return newBuf
      }
    }
  }

  override fun close() {
    reader.close()
  }

  companion object {
    private const val STATUS_LAST_CHAR_WAS_CR = 32
    private const val STATUS_COMMENTED_ROW = 16
    private const val STATUS_NEW_FIELD = 8
    private const val STATUS_QUOTED_MODE = 4
    internal const val STATUS_QUOTED_COLUMN = 2
    private const val STATUS_DATA_COLUMN = 1
    private const val STATUS_RESET = 0
  }
}