package net.codinux.csv.reader

class ImmutableSet<E>(private val wrapped: Set<E>) : Set<E> {

    constructor(vararg elements: E) : this(elements.toSet())


    override val size = wrapped.size

    override fun isEmpty() = wrapped.isEmpty()

    override fun contains(element: E) = wrapped.contains(element)

    override fun containsAll(elements: Collection<E>) = wrapped.containsAll(elements)

    override fun iterator() = wrapped.iterator()

    override fun toString() = wrapped.toString()

}