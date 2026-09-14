package dev.ajthom.kollections.bimap

/**
 * A [MutableBiMap] backed by two [LinkedHashMap]s: one from keys to values and one from values to
 * keys. Both directions therefore offer constant-time lookup, including [containsValue].
 *
 * Each direction iterates in its own insertion order: this bimap in the order in which its keys were
 * first inserted, and the [inverse] in the order in which its values were. Rebinding a key to a new
 * value keeps the key where it is here, but moves the new value to the end of the inverse's order.
 *
 * The [inverse] bimap shares the very same pair of backing maps, so `inverse().inverse()` is this
 * instance and every mutation is immediately visible through both. Null keys and null values are
 * allowed.
 *
 * ```kotlin
 * val biMap = HashBiMap<String, Int>()
 * biMap["one"] = 1
 * biMap.inverse()[1] // "one"
 * ```
 *
 * @param K the type of the keys
 * @param V the type of the values, unique within the bimap
 */
// Implementation note: this class deliberately does not extend kotlin.collections.AbstractMutableMap.
// On the JVM that class is java.util.AbstractMap with final `values()` / `keySet()` bridges that
// delegate to non-final `getValues()` / `getKeys()`. A subclass that narrows `values` to
// `MutableSet<V>` (as BiMap requires) makes the compiler emit `values()Ljava/util/Set;` without the
// matching `getValues()Ljava/util/Collection;` bridge, so call sites reach java.util.AbstractMap's
// own values view and fail with a ClassCastException; narrowing `keys` recurses until it overflows
// the stack. Both were reproduced with Kotlin 2.3.21. Implementing MutableMap directly avoids it;
// equals/hashCode/toString are delegated to the backing map so the Map contract still holds.
public class HashBiMap<K, V> private constructor(
    private val forward: MutableMap<K, V>,
    private val backward: MutableMap<V, K>,
    inverse: HashBiMap<V, K>?,
) : MutableBiMap<K, V> {

    /**
     * Creates an empty bimap.
     */
    public constructor() : this(LinkedHashMap<K, V>(), LinkedHashMap<V, K>(), null)

    private val keySet: KeySet = KeySet()

    private val entrySet: EntrySet = EntrySet()

    private val inverseBiMap: HashBiMap<V, K> = inverse ?: HashBiMap(backward, forward, this)

    /**
     * The number of key-value pairs in this bimap.
     */
    override val size: Int
        get() = forward.size

    /**
     * A live view of the keys of this bimap. Removing a key through this view also removes its
     * value from the [inverse] bimap. Adding a key is not supported.
     */
    override val keys: MutableSet<K>
        get() = keySet

    /**
     * A live view of the values of this bimap, which is the key set of the [inverse] bimap.
     * Removing a value through this view also removes its key from this bimap. Adding a value is
     * not supported.
     */
    override val values: MutableSet<V>
        get() = inverseBiMap.keys

    /**
     * A live view of the entries of this bimap. Removing an entry through this view, or calling
     * [MutableMap.MutableEntry.setValue] on one of its entries, keeps this bimap and its [inverse]
     * in sync. Adding an entry is not supported. Each entry reads its value from this bimap rather
     * than holding a copy, so it always sees the current binding for its key and throws
     * [IllegalStateException] once that key has been removed.
     */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = entrySet

    /**
     * Returns the inverse view of this bimap, mapping each value back to its key. The view shares
     * this bimap's storage, so changes are visible in both directions and `inverse().inverse()` is
     * this bimap.
     *
     * @return the live inverse view of this bimap
     */
    override fun inverse(): MutableBiMap<V, K> = inverseBiMap

    /**
     * Returns `true` when this bimap holds no entries.
     *
     * @return `true` if there are no entries
     */
    override fun isEmpty(): Boolean = forward.isEmpty()

    /**
     * Returns the value bound to [key], or `null` when [key] is not present.
     *
     * @param key the key to look up
     * @return the bound value, or `null`
     */
    override fun get(key: K): V? = forward[key]

    /**
     * Returns `true` when [key] is bound to a value.
     *
     * @param key the key to look for
     * @return `true` if [key] is present
     */
    override fun containsKey(key: K): Boolean = forward.containsKey(key)

    /**
     * Returns `true` when [value] is bound to a key. The lookup uses the inverse map and so runs
     * in constant time.
     *
     * @param value the value to look for
     * @return `true` if [value] is present
     */
    override fun containsValue(value: V): Boolean = backward.containsKey(value)

    /**
     * Binds [value] to [key], replacing any value previously bound to [key] and freeing it.
     *
     * @param key the key to associate with [value]
     * @param value the value to associate with [key]
     * @return the value previously bound to [key], or `null` if [key] was not present
     * @throws IllegalArgumentException if [value] is already bound to a different key; use
     * [forcePut] to evict that key instead
     */
    override fun put(key: K, value: V): V? = put(key, value, force = false)

    /**
     * Binds [value] to [key], first removing any entry that binds [value] to a different key.
     * Apart from that eviction it behaves exactly like [put].
     *
     * @param key the key to associate with [value]
     * @param value the value to associate with [key]
     * @return the value previously bound to [key], or `null` if [key] was not present
     */
    override fun forcePut(key: K, value: V): V? = put(key, value, force = true)

    /**
     * Binds every entry of [from], in iteration order, as if by [put].
     *
     * @param from the entries to add
     * @throws IllegalArgumentException if one of the values is already bound to a different key
     */
    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }

    /**
     * Removes the entry for [key], if present, from this bimap and from its [inverse].
     *
     * @param key the key to remove
     * @return the value that was bound to [key], or `null` if [key] was not present
     */
    override fun remove(key: K): V? {
        if (!forward.containsKey(key)) {
            return null
        }
        val value = forward.getValue(key)
        forward.remove(key)
        backward.remove(value)
        return value
    }

    /**
     * Removes every entry from this bimap and from its [inverse].
     */
    override fun clear() {
        forward.clear()
        backward.clear()
    }

    /**
     * Returns `true` when [other] is a [Map] holding exactly the same entries as this bimap.
     *
     * @param other the object to compare with
     * @return `true` if [other] is an equal map
     */
    override fun equals(other: Any?): Boolean = other === this || forward == other

    /**
     * Returns the hash code of this bimap, which is the sum of the hash codes of its entries, as
     * required by [Map].
     *
     * @return the hash code of this bimap
     */
    override fun hashCode(): Int = forward.hashCode()

    /**
     * Returns this bimap rendered in the standard map format, for example `{one=1}`.
     *
     * @return a string describing this bimap
     */
    override fun toString(): String = forward.toString()

    private fun put(key: K, value: V, force: Boolean): V? {
        val keyPresent = forward.containsKey(key)
        val previousValue = if (keyPresent) forward.getValue(key) else null
        if (keyPresent && previousValue == value) {
            return previousValue
        }
        if (backward.containsKey(value)) {
            require(force) { "value already bound to a different key: $value" }
            forward.remove(backward.getValue(value))
            backward.remove(value)
        }
        if (keyPresent) {
            backward.remove(forward.getValue(key))
        }
        forward[key] = value
        backward[value] = key
        return previousValue
    }

    private fun rebind(key: K, previousValue: V, newValue: V) {
        backward.remove(previousValue)
        forward[key] = newValue
        backward[newValue] = key
    }

    private inner class KeySet : AbstractMutableSet<K>() {

        override val size: Int
            get() = forward.size

        override fun add(element: K): Boolean =
            throw UnsupportedOperationException("adding a key to a bimap key view is not supported")

        override fun clear() {
            this@HashBiMap.clear()
        }

        override fun contains(element: K): Boolean = forward.containsKey(element)

        override fun remove(element: K): Boolean {
            if (!forward.containsKey(element)) {
                return false
            }
            this@HashBiMap.remove(element)
            return true
        }

        override fun iterator(): MutableIterator<K> = KeyIterator()
    }

    private inner class KeyIterator : MutableIterator<K> {

        private val delegate: EntryIterator = EntryIterator()

        override fun hasNext(): Boolean = delegate.hasNext()

        override fun next(): K = delegate.next().key

        override fun remove() {
            delegate.remove()
        }
    }

    private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {

        override val size: Int
            get() = forward.size

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
            throw UnsupportedOperationException("adding an entry to a bimap entry view is not supported")

        override fun clear() {
            this@HashBiMap.clear()
        }

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean =
            forward.containsKey(element.key) && forward.getValue(element.key) == element.value

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
            if (!contains(element)) {
                return false
            }
            this@HashBiMap.remove(element.key)
            return true
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntryIterator()
    }

    private inner class EntryIterator : MutableIterator<MutableMap.MutableEntry<K, V>> {

        private val delegate: MutableIterator<MutableMap.MutableEntry<K, V>> = forward.entries.iterator()

        private var lastReturned: MutableMap.MutableEntry<K, V>? = null

        override fun hasNext(): Boolean = delegate.hasNext()

        override fun next(): MutableMap.MutableEntry<K, V> {
            val entry = delegate.next()
            lastReturned = entry
            return BiMapEntry(entry.key)
        }

        override fun remove() {
            val entry = lastReturned
            check(entry != null) { "next() must be called before remove()" }
            // Read the current value before removing: the key may have been rebound since next().
            val currentValue = entry.value
            delegate.remove()
            backward.remove(currentValue)
            lastReturned = null
        }
    }

    private inner class BiMapEntry(override val key: K) : MutableMap.MutableEntry<K, V> {

        // The value is always read through the backing map rather than cached, so an entry can
        // never act on a stale value and leave the two maps disagreeing.
        override val value: V
            get() {
                check(forward.containsKey(key)) { "entry is no longer present in the bimap: $key" }
                return forward.getValue(key)
            }

        override fun setValue(newValue: V): V {
            val previousValue = value
            if (previousValue == newValue) {
                return previousValue
            }
            require(!backward.containsKey(newValue)) {
                "value already bound to a different key: $newValue"
            }
            rebind(key, previousValue, newValue)
            return previousValue
        }

        override fun equals(other: Any?): Boolean = other is Map.Entry<*, *> && other.key == key && other.value == value

        override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

        override fun toString(): String = "$key=$value"
    }
}
