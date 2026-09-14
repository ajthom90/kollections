package dev.ajthom.kollections.multimap

/**
 * A skeletal [MutableMultimap] backed by a [MutableMap] from each key to the mutable collection of its values.
 *
 * Subclasses implement [createCollection], which supplies the collection for a newly inserted key, and [get],
 * which returns a live view of one key's values. Every mutator here maintains the invariant that [backing]
 * never associates a key with an empty collection. [keys] and [asMap] are live views; [values], [flatValues]
 * and [entries] are snapshots. Bulk mutators read their whole input before changing anything, so they accept
 * this multimap's own live views as input.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @param C the type of the collection holding the values of one key
 * @property backing the map holding the values of each key; it must not contain an empty collection, and
 * subclasses must never leave one in it
 */
public abstract class AbstractMutableMultimap<K, V, C : MutableCollection<V>>(
    protected val backing: MutableMap<K, C>,
) : AbstractMultimap<K, V, C>(),
    MutableMultimap<K, V> {

    private val asMapView: Map<K, C> = AsMapView()

    /** Creates the empty collection that will hold the values of a newly inserted key. */
    protected abstract fun createCollection(): C

    /**
     * Returns a live view of the values associated with [key]. Adding to the view inserts pairs into this
     * multimap (even if the key is currently absent); removing the last value from it drops the key.
     */
    abstract override fun get(key: K): C

    /**
     * Returns a live, read-only [Map] view of this multimap. Each value is an equivalent live view to the
     * one [get] returns for that key; an absent key maps to `null`.
     */
    override fun asMap(): Map<K, C> = asMapView

    /**
     * The number of key-value pairs in this multimap, summed over the value collections and so
     * computed in `O(keys.size)`; use `keys.size` for the number of keys.
     */
    override val size: Int
        get() = backing.values.sumOf { it.size }

    /**
     * A live view of the distinct keys. Removing a key from it drops all of the key's values; adding to it is
     * not supported.
     */
    override val keys: MutableSet<K>
        get() = backing.keys

    /** A snapshot of the value collections, one per key, in the same order as [keys]. */
    override val values: Collection<C>
        get() = backing.values.map { copyOf(it) }

    /** A snapshot of every value, in the order of [keys] and, within a key, in the order of its values. */
    override val flatValues: Collection<V>
        get() = backing.values.flatten()

    /** A snapshot of the key to value-collection associations, one per key, in the same order as [keys]. */
    override val entries: Set<Map.Entry<K, C>>
        get() = backing.mapValuesTo(LinkedHashMap()) { copyOf(it.value) }.entries

    /** Returns `true` if this multimap contains no key-value pairs. */
    override fun isEmpty(): Boolean = backing.isEmpty()

    /** Returns `true` if at least one value is associated with [key]. */
    override fun containsKey(key: K): Boolean = backing.containsKey(key)

    /** Returns `true` if [value] is associated with at least one key. */
    override fun containsValue(value: V): Boolean = backing.values.any { value in it }

    /** Returns `true` if [value] is associated with [key]. */
    override fun containsEntry(key: K, value: V): Boolean = backing[key]?.contains(value) == true

    /**
     * Associates [value] with [key].
     *
     * @return `true` if the multimap changed
     */
    override fun put(key: K, value: V): Boolean {
        val collection = backing.getOrPut(key) { createCollection() }
        val changed = collection.add(value)
        if (collection.isEmpty()) backing.remove(key)
        return changed
    }

    /**
     * Associates every element of [values] with [key]. An empty [values] leaves the multimap unchanged.
     * [values] is read completely before anything is added, so it may be a live view of this multimap.
     *
     * @return `true` if the multimap changed
     */
    override fun putAll(key: K, values: Iterable<V>): Boolean {
        val snapshot = values.toList()
        if (snapshot.isEmpty()) return false
        val collection = backing.getOrPut(key) { createCollection() }
        var changed = false
        for (value in snapshot) {
            if (collection.add(value)) changed = true
        }
        if (collection.isEmpty()) backing.remove(key)
        return changed
    }

    /**
     * Adds every key-value pair of [from]. The entries of [from] are read before anything is added, so
     * [from] may be this multimap itself or a view of it.
     *
     * @return `true` if the multimap changed
     */
    override fun putAll(from: Multimap<out K, out V>): Boolean {
        var changed = false
        for ((key, values) in from.asMap().entries.toList()) {
            if (putAll(key, values)) changed = true
        }
        return changed
    }

    /**
     * Associates every value of each entry of [from] with that entry's key. The entries of [from] are read
     * before anything is added, so [from] may be this multimap's own [asMap] view.
     *
     * @return `true` if the multimap changed
     */
    override fun putAll(from: Map<out K, Iterable<V>>): Boolean {
        var changed = false
        for ((key, values) in from.entries.toList()) {
            if (putAll(key, values)) changed = true
        }
        return changed
    }

    /**
     * Removes a single association of [value] with [key], dropping the key if it was the last one.
     *
     * @return `true` if the multimap changed
     */
    override fun remove(key: K, value: V): Boolean {
        val collection = backing[key] ?: return false
        val changed = collection.remove(value)
        if (collection.isEmpty()) backing.remove(key)
        return changed
    }

    /**
     * Removes every value associated with [key].
     *
     * @return the removed values, or an empty collection if the key was absent; the result is not a view
     */
    override fun remove(key: K): C = backing.remove(key) ?: createCollection()

    /**
     * Replaces the values associated with [key] with [values]. An empty [values] removes the key. A present
     * key keeps its position in [keys].
     *
     * @return the values previously associated with the key, or an empty collection if it was absent; the
     * result is not a view
     */
    override fun replaceValues(key: K, values: Iterable<V>): C {
        val replacement = createCollection()
        replacement.addAll(values)
        val old = if (replacement.isEmpty()) backing.remove(key) else backing.put(key, replacement)
        return old ?: createCollection()
    }

    /** Removes every key-value pair. */
    override fun clear() {
        backing.clear()
    }

    private fun copyOf(collection: C): C = createCollection().apply { addAll(collection) }

    private inner class AsMapView : AbstractMap<K, C>() {
        private val entriesView = EntriesView()

        override val entries: Set<Map.Entry<K, C>>
            get() = entriesView

        override val keys: Set<K>
            get() = backing.keys

        override val size: Int
            get() = backing.size

        override fun isEmpty(): Boolean = backing.isEmpty()

        override fun containsKey(key: K): Boolean = backing.containsKey(key)

        override fun get(key: K): C? = if (backing.containsKey(key)) this@AbstractMutableMultimap[key] else null
    }

    private inner class EntriesView : AbstractSet<Map.Entry<K, C>>() {
        override val size: Int
            get() = backing.size

        override fun iterator(): Iterator<Map.Entry<K, C>> {
            val keys = backing.keys.iterator()
            return object : Iterator<Map.Entry<K, C>> {
                override fun hasNext(): Boolean = keys.hasNext()

                override fun next(): Map.Entry<K, C> = ViewEntry(keys.next())
            }
        }
    }

    private inner class ViewEntry(override val key: K) : Map.Entry<K, C> {
        override val value: C
            get() = this@AbstractMutableMultimap[key]

        override fun equals(other: Any?): Boolean = other is Map.Entry<*, *> && other.key == key && other.value == value

        override fun hashCode(): Int = key.hashCode() xor value.hashCode()

        override fun toString(): String = "$key=$value"
    }
}
