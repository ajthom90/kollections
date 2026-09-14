package dev.ajthom.kollections.bimap

/**
 * A [BiMap] that supports adding and removing entries.
 *
 * The uniqueness of values is enforced on every mutation: [put] fails when the value is already
 * bound to a different key, while [forcePut] evicts the conflicting key instead.
 *
 * @param K the type of the keys
 * @param V the type of the values, unique within the bimap
 */
public interface MutableBiMap<K, V> :
    BiMap<K, V>,
    MutableMap<K, V> {

    /**
     * A live view of the keys contained in this bimap. Removing a key through this view also
     * removes its value from the [inverse] bimap. Adding a key is not supported.
     */
    override val keys: MutableSet<K>

    /**
     * A live view of the values contained in this bimap, which is the key set of the [inverse]
     * bimap. Removing a value through this view also removes its key from this bimap. Adding a
     * value is not supported.
     */
    override val values: MutableSet<V>

    /**
     * A live view of the entries contained in this bimap. Removing an entry through this view, or
     * changing an entry's value with [MutableMap.MutableEntry.setValue], keeps this bimap and its
     * [inverse] in sync. Adding an entry is not supported.
     */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>

    /**
     * Returns the inverse view of this bimap, mapping each value back to its key. The returned
     * bimap is a live view: changes made through it are visible in this bimap and vice versa.
     *
     * @return a mutable bimap of values to keys
     */
    override fun inverse(): MutableBiMap<V, K>

    /**
     * Associates [value] with [key], evicting any entry that already binds [value] to a different
     * key instead of failing the way [put] does.
     *
     * @param key the key to associate with [value]
     * @param value the value to associate with [key]
     * @return the value previously associated with [key], or `null` if [key] was not present
     */
    public fun forcePut(key: K, value: V): V?
}
