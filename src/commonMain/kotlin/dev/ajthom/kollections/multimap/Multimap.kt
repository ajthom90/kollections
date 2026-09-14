package dev.ajthom.kollections.multimap

/**
 * A collection that maps keys to values, similar to [Map], but in which each key may be associated with
 * multiple values.
 *
 * The values for a key are exposed as a [Collection]; [ListMultimap] and [SetMultimap] narrow that to [List]
 * and [Set]. A key is present only while it has at least one value: no key ever maps to an empty collection.
 *
 * Two multimaps are equal when their [asMap] views are equal, so a [ListMultimap] never equals a
 * [SetMultimap], even with the same content.
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public interface Multimap<K, V> {
    /** The number of key-value pairs in this multimap. */
    public val size: Int

    /** The distinct keys in this multimap. */
    public val keys: Set<K>

    /** The value collections, one per key, in the same order as [keys]. */
    public val values: Collection<Collection<V>>

    /** Every value in this multimap, in the order of [keys] and, within a key, in the order of its values. */
    public val flatValues: Collection<V>

    /** The key to value-collection associations, one per key, in the same order as [keys]. */
    public val entries: Set<Map.Entry<K, Collection<V>>>

    /** Returns the values associated with [key], or an empty collection if the key is absent. */
    public operator fun get(key: K): Collection<V>

    /** Returns a [Map] view of this multimap, associating each key with its non-empty value collection. */
    public fun asMap(): Map<K, Collection<V>>

    /** Returns `true` if this multimap contains no key-value pairs. */
    public fun isEmpty(): Boolean

    /** Returns `true` if at least one value is associated with [key]. */
    public fun containsKey(key: K): Boolean

    /** Returns `true` if [value] is associated with at least one key. */
    public fun containsValue(value: V): Boolean

    /** Returns `true` if [value] is associated with [key]. */
    public fun containsEntry(key: K, value: V): Boolean

    /**
     * Performs [action] on every key-value pair, in the order of [keys] and, within a key, in the order of
     * its values.
     */
    public fun forEach(action: (key: K, value: V) -> Unit) {
        for ((key, values) in asMap()) {
            for (value in values) {
                action(key, value)
            }
        }
    }
}

/**
 * A [Multimap] whose values for each key form a [List]: duplicate values are kept and their order is
 * significant.
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public interface ListMultimap<K, V> : Multimap<K, V> {
    /** The value lists, one per key, in the same order as [keys]. */
    override val values: Collection<List<V>>

    /** The key to value-list associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, List<V>>>

    /** Returns the values associated with [key], or an empty list if the key is absent. */
    override operator fun get(key: K): List<V>

    /** Returns a [Map] view of this multimap, associating each key with its non-empty value list. */
    override fun asMap(): Map<K, List<V>>
}

/**
 * A [Multimap] whose values for each key form a [Set]: each key-value pair is stored at most once.
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public interface SetMultimap<K, V> : Multimap<K, V> {
    /** The value sets, one per key, in the same order as [keys]. */
    override val values: Collection<Set<V>>

    /** The key to value-set associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, Set<V>>>

    /** Returns the values associated with [key], or an empty set if the key is absent. */
    override operator fun get(key: K): Set<V>

    /** Returns a [Map] view of this multimap, associating each key with its non-empty value set. */
    override fun asMap(): Map<K, Set<V>>
}
