@file:JvmName("CsvRowIteratorJvm")

package net.codinux.csv.reader

import net.codinux.csv.UncheckedIOException
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

fun CsvRowIterator.rowSpliterator(): Spliterator<CsvRow> {
  return CsvRowSpliterator(iterator())
}

/**
 * Creates a new sequential `Stream` from this instance.
 *
 *
 * A close handler is registered by this method in order to close the underlying resources.
 * Don't forget to close the returned stream when you're done.
 *
 * @return a new sequential `Stream`.
 */
fun CsvRowIterator.stream(): Stream<CsvRow> {
  return StreamSupport.stream(rowSpliterator(), false)
    .onClose {
      try {
        close()
      } catch (e: net.codinux.csv.IOException) {
        throw UncheckedIOException(e)
      }
    }
}