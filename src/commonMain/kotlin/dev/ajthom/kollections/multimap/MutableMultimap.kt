package dev.ajthom.kollections.multimap

/**
 * A [Multimap] that supports adding and removing key-value pairs.
 *
 * [get], [keys] and [asMap] are live views: changes to them write through to the multimap and changes to the
 * multimap are visible through them. [values], [flatValues] and [entries] are snapshots taken when the
 * property is read. Every live view preserves the invariant that no key maps to an empty collection: removing
 * the last value of a key drops the key, and adding to the view of an absent key inserts the key.
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public interface MutableMultimap<K, V> : Multimap<K, V> {
    /**
     * The number of key-value pairs in this multimap. Implementations are free to hold their pairs
     * per key, so reading this is `O(keys.size)` rather than `O(1)`; use `keys.size` for the number
     * of keys.
     */
    override val size: Int

    /**
     * Returns a live view of the values associated with [key]. Adding to the view inserts pairs into this
     * multimap (even if the key is currently absent); removing the last value from it drops the key.
     */
    override operator fun get(key: K): MutableCollection<V>

    /**
     * A live view of the distinct keys. Removing a key from it drops all of the key's values; adding to it is
     * not supported.
     */
    override val keys: MutableSet<K>

    /**
     * Returns a live, read-only [Map] view of this multimap. Each value is an equivalent live view to the
     * one [get] returns for that key; an absent key maps to `null`.
     */
    override fun asMap(): Map<K, MutableCollection<V>>

    /** A snapshot of the value collections, one per key, in the same order as [keys]. */
    override val values: Collection<Collection<V>>

    /** A snapshot of every value, in the order of [keys] and, within a key, in the order of its values. */
    override val flatValues: Collection<V>

    /** A snapshot of the key to value-collection associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, Collection<V>>>

    /**
     * Associates [value] with [key].
     *
     * @return `true` if the multimap changed; a [MutableSetMultimap] returns `false` for a pair already present
     */
    public fun put(key: K, value: V): Boolean

    /**
     * Associates every element of [values] with [key]. An empty [values] leaves the multimap unchanged.
     * The input is read completely before anything is mutated, so a live view of this multimap may be
     * passed.
     *
     * @return `true` if the multimap changed
     */
    public fun putAll(key: K, values: Iterable<V>): Boolean

    /**
     * Adds every key-value pair of [from]. The input is read completely before anything is mutated, so a
     * live view of this multimap may be passed.
     *
     * @return `true` if the multimap changed
     */
    public fun putAll(from: Multimap<out K, out V>): Boolean

    /**
     * Associates every value of each entry of [from] with that entry's key. The input is read completely
     * before anything is mutated, so a live view of this multimap may be passed.
     *
     * @return `true` if the multimap changed
     */
    public fun putAll(from: Map<out K, Iterable<V>>): Boolean

    /**
     * Removes a single association of [value] with [key], dropping the key if it was the last one.
     *
     * @return `true` if the multimap changed
     */
    public fun remove(key: K, value: V): Boolean

    /**
     * Removes every value associated with [key].
     *
     * @return the removed values, or an empty collection if the key was absent; the result is not a view
     */
    public fun remove(key: K): Collection<V>

    /**
     * Replaces the values associated with [key] with [values]. An empty [values] removes the key.
     *
     * @return the values previously associated with the key, or an empty collection if it was absent; the
     * result is not a view
     */
    public fun replaceValues(key: K, values: Iterable<V>): Collection<V>

    /** Removes every key-value pair. */
    public fun clear()
}

/**
 * A [MutableMultimap] whose values for each key form a [MutableList].
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public interface MutableListMultimap<K, V> :
    MutableMultimap<K, V>,
    ListMultimap<K, V> {
    /**
     * Returns a live view of the values associated with [key]. Adding to the view inserts pairs into this
     * multimap (even if the key is currently absent); removing the last value from it drops the key.
     */
    override operator fun get(key: K): MutableList<V>

    /**
     * Returns a live, read-only [Map] view of this multimap. Each value is an equivalent live view to the
     * one [get] returns for that key; an absent key maps to `null`.
     */
    override fun asMap(): Map<K, MutableList<V>>

    /** A snapshot of the value lists, one per key, in the same order as [keys]. */
    override val values: Collection<List<V>>

    /** A snapshot of the key to value-list associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, List<V>>>

    /**
     * Removes every value associated with [key].
     *
     * @return the removed values in order, or an empty list if the key was absent; the result is not a view
     */
    override fun remove(key: K): List<V>

    /**
     * Replaces the values associated with [key] with [values]. An empty [values] removes the key.
     *
     * @return the values previously associated with the key, or an empty list if it was absent; the result is
     * not a view
     */
    override fun replaceValues(key: K, values: Iterable<V>): List<V>
}

/**
 * A [MutableMultimap] whose values for each key form a [MutableSet].
 *
 * @param K the type of the keys
 * @param V the type of the values
 */
public interface MutableSetMultimap<K, V> :
    MutableMultimap<K, V>,
    SetMultimap<K, V> {
    /**
     * Returns a live view of the values associated with [key]. Adding to the view inserts pairs into this
     * multimap (even if the key is currently absent); removing the last value from it drops the key.
     */
    override operator fun get(key: K): MutableSet<V>

    /**
     * Returns a live, read-only [Map] view of this multimap. Each value is an equivalent live view to the
     * one [get] returns for that key; an absent key maps to `null`.
     */
    override fun asMap(): Map<K, MutableSet<V>>

    /** A snapshot of the value sets, one per key, in the same order as [keys]. */
    override val values: Collection<Set<V>>

    /** A snapshot of the key to value-set associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, Set<V>>>

    /**
     * Removes every value associated with [key].
     *
     * @return the removed values, or an empty set if the key was absent; the result is not a view
     */
    override fun remove(key: K): Set<V>

    /**
     * Replaces the values associated with [key] with [values]. An empty [values] removes the key.
     *
     * @return the values previously associated with the key, or an empty set if it was absent; the result is
     * not a view
     */
    override fun replaceValues(key: K, values: Iterable<V>): Set<V>
}
