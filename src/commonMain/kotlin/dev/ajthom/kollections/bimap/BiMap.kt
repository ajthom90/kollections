package dev.ajthom.kollections.bimap

/**
 * A read-only [Map] whose values are as unique as its keys, so the mapping can be read in both
 * directions.
 *
 * Because no two keys map to the same value, [values] is a [Set] rather than a general collection,
 * and [inverse] exposes the value-to-key mapping.
 *
 * @param K the type of the keys
 * @param V the type of the values, unique within the bimap
 */
public interface BiMap<K, V> : Map<K, V> {

    /**
     * The values contained in this bimap. The values of a bimap are unique, so they form a [Set].
     */
    override val values: Set<V>

    /**
     * Returns the inverse view of this bimap, mapping each value back to its key.
     *
     * @return a bimap of values to keys
     */
    public fun inverse(): BiMap<V, K>
}
