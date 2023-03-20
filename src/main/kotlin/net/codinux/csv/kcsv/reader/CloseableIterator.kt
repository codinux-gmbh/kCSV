package net.codinux.csv.kcsv.reader

import net.codinux.csv.kcsv.Closeable

/**
 * Iterator that supports closing underlying resources.
 *
 * @param <E> the type of elements returned by this iterator
</E> */
interface CloseableIterator<E> : Iterator<E>, Closeable