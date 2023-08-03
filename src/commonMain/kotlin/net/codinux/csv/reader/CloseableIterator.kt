package net.codinux.csv.reader

import net.codinux.csv.Closeable

/**
 * Iterator that supports closing underlying resources.
 *
 * @param <E> the type of elements returned by this iterator
</E> */
interface CloseableIterator<E> : Iterator<E>, Closeable