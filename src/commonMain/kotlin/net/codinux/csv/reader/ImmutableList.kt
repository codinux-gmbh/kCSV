package net.codinux.csv.reader

class ImmutableList<E>(private val wrapped: List<E>) : List<E> {

    constructor(vararg elements: E) : this(elements.asList())


    override val size = wrapped.size

    override fun isEmpty() = wrapped.isEmpty()

    override fun get(index: Int) = wrapped.get(index)

    override fun indexOf(element: E) = wrapped.indexOf(element)

    override fun lastIndexOf(element: E) = wrapped.lastIndexOf(element)

    override fun contains(element: E) = wrapped.contains(element)

    override fun containsAll(elements: Collection<E>) = wrapped.containsAll(elements)

    override fun iterator() = wrapped.iterator()

    override fun listIterator() = wrapped.listIterator()

    override fun listIterator(index: Int) = wrapped.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = wrapped.subList(fromIndex, toIndex)

    override fun toString() = wrapped.toString()

}