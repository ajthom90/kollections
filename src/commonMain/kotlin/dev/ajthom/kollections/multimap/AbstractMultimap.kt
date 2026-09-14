package dev.ajthom.kollections.multimap

/**
 * A skeletal [Multimap] implementation that derives everything from [asMap] and [get].
 *
 * Subclasses implement [asMap], returning a map that never holds an empty collection, and [get], returning
 * an empty collection of type [C] for an absent key. [equals], [hashCode] and [toString] delegate to
 * [asMap], so two multimaps are equal exactly when their maps are; because a [List] never equals a [Set], a
 * [ListMultimap] never equals a [SetMultimap].
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @param C the type of the collection holding the values of one key
 */
public abstract class AbstractMultimap<K, V, C : Collection<V>> : Multimap<K, V> {
    /** Returns a [Map] view of this multimap, associating each key with its non-empty value collection. */
    abstract override fun asMap(): Map<K, C>

    /** Returns the values associated with [key], or an empty collection if the key is absent. */
    abstract override fun get(key: K): C

    /** The number of key-value pairs in this multimap. */
    override val size: Int
        get() = asMap().values.sumOf { it.size }

    /** The distinct keys in this multimap. */
    override val keys: Set<K>
        get() = asMap().keys

    /** The value collections, one per key, in the same order as [keys]. */
    override val values: Collection<C>
        get() = asMap().values

    /** Every value in this multimap, in the order of [keys] and, within a key, in the order of its values. */
    override val flatValues: Collection<V>
        get() = asMap().values.flatten()

    /** The key to value-collection associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, C>>
        get() = asMap().entries

    /** Returns `true` if this multimap contains no key-value pairs. */
    override fun isEmpty(): Boolean = asMap().isEmpty()

    /** Returns `true` if at least one value is associated with [key]. */
    override fun containsKey(key: K): Boolean = asMap().containsKey(key)

    /** Returns `true` if [value] is associated with at least one key. */
    override fun containsValue(value: V): Boolean = asMap().values.any { value in it }

    /** Returns `true` if [value] is associated with [key]. */
    override fun containsEntry(key: K, value: V): Boolean = asMap()[key]?.contains(value) == true

    /** Returns `true` if [other] is a [Multimap] whose [asMap] equals this multimap's [asMap]. */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Multimap<*, *> && asMap() == other.asMap()
    }

    /** Returns the hash code of [asMap]. */
    override fun hashCode(): Int = asMap().hashCode()

    /** Returns the string representation of [asMap]. */
    override fun toString(): String = asMap().toString()
}
