package net.codinux.csv.reader

import java.util.*
import java.util.function.Consumer

internal class CsvRowSpliterator<T>(private val iterator: Iterator<T>) : Spliterator<T> {

  override fun tryAdvance(action: Consumer<in T>): Boolean {
    if (!iterator.hasNext()) {
      return false
    }
    action.accept(iterator.next())
    return true
  }

  override fun forEachRemaining(action: Consumer<in T>) {
    iterator.forEachRemaining(action)
  }

  override fun trySplit(): Spliterator<T>? {
    return null
  }

  override fun estimateSize(): Long {
    return Long.MAX_VALUE
  }

  override fun characteristics(): Int {
    return FIXED_CHARACTERISTICS
  }

  companion object {
    private const val FIXED_CHARACTERISTICS = Spliterator.ORDERED or Spliterator.DISTINCT or Spliterator.NONNULL or Spliterator.IMMUTABLE
  }
}