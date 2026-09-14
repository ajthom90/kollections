package dev.ajthom.kollections.bimap

/**
 * Returns an empty [BiMap].
 *
 * Like the standard library's [mapOf], the returned instance is backed by a mutable class: the
 * read-only [BiMap] type is a contract rather than a guarantee, so callers must not cast it back to
 * a [MutableBiMap] in order to change it.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @return a new, empty bimap
 */
public fun <K, V> emptyBiMap(): BiMap<K, V> = HashBiMap()

/**
 * Returns a read-only [BiMap] holding the given [pairs], in iteration order.
 *
 * When two pairs share a key the later pair wins and the earlier value is freed, matching
 * [mapOf]. Sharing a value between two keys is not allowed.
 *
 * Like the standard library's [mapOf], the returned instance is backed by a mutable class: the
 * read-only [BiMap] type is a contract rather than a guarantee, so callers must not cast it back to
 * a [MutableBiMap] in order to change it.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @param pairs the key-value pairs to store
 * @return a bimap holding [pairs]
 * @throws IllegalArgumentException if two pairs with different keys share a value
 */
public fun <K, V> biMapOf(vararg pairs: Pair<K, V>): BiMap<K, V> = mutableBiMapOf(*pairs)

/**
 * Returns a [MutableBiMap] holding the given [pairs], in iteration order.
 *
 * When two pairs share a key the later pair wins and the earlier value is freed, matching
 * [mutableMapOf]. Sharing a value between two keys is not allowed.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @param pairs the key-value pairs to store
 * @return a new mutable bimap holding [pairs]
 * @throws IllegalArgumentException if two pairs with different keys share a value
 */
public fun <K, V> mutableBiMapOf(vararg pairs: Pair<K, V>): MutableBiMap<K, V> {
    val biMap = HashBiMap<K, V>()
    for ((key, value) in pairs) {
        biMap[key] = value
    }
    return biMap
}

/**
 * Returns a read-only [BiMap] holding a copy of the entries of this map, in iteration order.
 *
 * Like the standard library's [mapOf], the returned instance is backed by a mutable class: the
 * read-only [BiMap] type is a contract rather than a guarantee, so callers must not cast it back to
 * a [MutableBiMap] in order to change it.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @return a bimap holding the entries of this map
 * @throws IllegalArgumentException if two entries of this map share a value
 */
public fun <K, V> Map<K, V>.toBiMap(): BiMap<K, V> = toMutableBiMap()

/**
 * Returns a [MutableBiMap] holding a copy of the entries of this map, in iteration order. The
 * copy is detached: later changes to either map are not visible in the other.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @return a new mutable bimap holding the entries of this map
 * @throws IllegalArgumentException if two entries of this map share a value
 */
public fun <K, V> Map<K, V>.toMutableBiMap(): MutableBiMap<K, V> {
    val biMap = HashBiMap<K, V>()
    biMap.putAll(this)
    return biMap
}
