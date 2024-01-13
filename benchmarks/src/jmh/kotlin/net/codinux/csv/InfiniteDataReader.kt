package net.codinux.csv

import java.io.Reader
import kotlin.math.min

class InfiniteDataReader(data: String) : Reader() {

    private val data = data.toCharArray()
    private var pos = 0

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        var copied = 0
        while (copied < len) {
            val tlen = min((len - copied).toDouble(), (data.size - pos).toDouble()).toInt()
            System.arraycopy(data, pos, cbuf, off + copied, tlen)
            copied += tlen
            pos += tlen

            if (pos == data.size) {
                pos = 0
            }
        }

        return copied
    }

    override fun close() {
        // NOP
    }

}