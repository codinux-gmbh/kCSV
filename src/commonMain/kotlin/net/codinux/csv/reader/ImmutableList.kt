package net.codinux.csv.reader

class ImmutableList<E>(private val wrapped: Array<E>) : List<E> {

    private val wrappedAsList by lazy { wrapped.asList() }


    override val size = wrapped.size

    override fun isEmpty() = wrapped.isEmpty()

    override fun get(index: Int) = wrapped[index]

    override fun indexOf(element: E) = wrapped.indexOf(element)

    override fun lastIndexOf(element: E) = wrapped.lastIndexOf(element)

    override fun contains(element: E) = wrapped.contains(element)

    override fun containsAll(elements: Collection<E>) = wrappedAsList.containsAll(elements)

    override fun iterator() = wrapped.iterator()

    override fun listIterator() = wrappedAsList.listIterator()

    override fun listIterator(index: Int) = wrappedAsList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) = wrappedAsList.subList(fromIndex, toIndex)

    override fun toString() = wrappedAsList.toString()


    fun toMutableList(): MutableList<E> = ArrayList(this)

}