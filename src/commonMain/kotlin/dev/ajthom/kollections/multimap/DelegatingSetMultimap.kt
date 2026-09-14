package dev.ajthom.kollections.multimap

/**
 * A read-only [SetMultimap] that wraps [map] by reference: later changes to the map are visible through
 * this multimap.
 *
 * The caller is responsible for never associating a key with an empty set.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @property map the wrapped map
 */
public class DelegatingSetMultimap<K, V>(private val map: Map<K, Set<V>>) :
    AbstractMultimap<K, V, Set<V>>(),
    SetMultimap<K, V> {

    /** Returns the wrapped map. */
    override fun asMap(): Map<K, Set<V>> = map

    /** Returns the values associated with [key], or an empty set if the key is absent. */
    override fun get(key: K): Set<V> = map[key] ?: emptySet()
}
