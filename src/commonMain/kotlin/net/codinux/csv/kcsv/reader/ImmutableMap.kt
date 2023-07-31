package net.codinux.csv.kcsv.reader

class ImmutableMap<K, V>(private val wrapped: Map<K, V>) : Map<K, V> {

    constructor(vararg entries: Pair<K, V>) : this(entries.toMap())


    override val size = wrapped.size

    override val keys = wrapped.keys

    override val values = wrapped.values

    override val entries = wrapped.entries


    override fun isEmpty() = wrapped.isEmpty()

    override fun containsKey(key: K) = wrapped.containsKey(key)

    override fun containsValue(value: V) = wrapped.containsValue(value)

    override fun get(key: K) = wrapped.get(key)

    override fun toString() = wrapped.toString()

}

fun <K, V> Map<K, V>.toImmutableMap() = ImmutableMap(this)