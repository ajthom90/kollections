package dev.ajthom.kollections.multimap

/**
 * A read-only [ListMultimap] that wraps [map] by reference: later changes to the map are visible through
 * this multimap.
 *
 * The caller is responsible for never associating a key with an empty list.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @property map the wrapped map
 */
public class DelegatingListMultimap<K, V>(private val map: Map<K, List<V>>) :
    AbstractMultimap<K, V, List<V>>(),
    ListMultimap<K, V> {

    /** Returns the wrapped map. */
    override fun asMap(): Map<K, List<V>> = map

    /** Returns the values associated with [key], or an empty list if the key is absent. */
    override fun get(key: K): List<V> = map[key] ?: emptyList()
}
