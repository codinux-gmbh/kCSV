package net.codinux.csv.utils

import org.openjdk.jmh.infra.Blackhole
import java.io.Writer

/**
 * Writer implementation that sends all data to a black hole.
 */
class NullWriter internal constructor(private val bh: Blackhole) : Writer() {

    companion object {
        private const val BUFFER_SIZE = 8192
    }


    private val buf = CharArray(BUFFER_SIZE)
    private var pos = 0

    override fun write(c: Int) {
        if (pos == buf.size) {
            flush()
        }
        buf[pos++] = c.toChar()
    }

    override fun write(cbuf: CharArray, off: Int, len: Int) {
        if (len + pos > buf.size) {
            flush()
        }
        System.arraycopy(cbuf, off, buf, pos, len)
        pos += len
    }

    override fun write(str: String, off: Int, len: Int) {
        if (len + pos > buf.size) {
            flush()
        }
        str.toCharArray(buf, pos, off, off + len)
        pos += len
    }

    override fun flush() {
        bh.consume(buf.copyOf(pos))
        pos = 0
    }

    override fun close() {
        flush()
    }

}