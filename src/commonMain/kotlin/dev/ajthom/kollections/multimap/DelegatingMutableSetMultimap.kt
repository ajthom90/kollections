package dev.ajthom.kollections.multimap

/**
 * A [MutableSetMultimap] backed by [backing], whose value sets are created by [setFactory].
 *
 * The factory lets callers choose the set type for each key; the default is a [LinkedHashSet]. The backing
 * map decides key order and must not contain an empty set.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @param backing the map holding the values of each key
 * @property setFactory creates the empty set that will hold the values of a newly inserted key
 */
public open class DelegatingMutableSetMultimap<K, V>(
    backing: MutableMap<K, MutableSet<V>>,
    private val setFactory: () -> MutableSet<V> = { LinkedHashSet() },
) : AbstractMutableMultimap<K, V, MutableSet<V>>(backing),
    MutableSetMultimap<K, V> {

    /** Creates the empty set that will hold the values of a newly inserted key, using [setFactory]. */
    override fun createCollection(): MutableSet<V> = setFactory()

    /**
     * Returns a live view of the values associated with [key]. Adding to the view inserts pairs into this
     * multimap (even if the key is currently absent); removing the last value from it drops the key.
     */
    override fun get(key: K): MutableSet<V> = SetView(key)

    private inner class SetView(private val key: K) : AbstractMutableSet<V>() {
        private fun current(): MutableSet<V>? = backing[key]

        override val size: Int
            get() = current()?.size ?: 0

        override fun contains(element: V): Boolean = current()?.contains(element) == true

        override fun add(element: V): Boolean {
            val set = backing.getOrPut(key) { createCollection() }
            val added = set.add(element)
            if (set.isEmpty()) backing.remove(key)
            return added
        }

        // Copies the argument first so that adding a view to itself never iterates a changing set.
        override fun addAll(elements: Collection<V>): Boolean = super.addAll(elements.toList())

        override fun remove(element: V): Boolean {
            val set = current() ?: return false
            val removed = set.remove(element)
            if (set.isEmpty()) backing.remove(key)
            return removed
        }

        override fun clear() {
            backing.remove(key)
        }

        override fun iterator(): MutableIterator<V> {
            val set = current() ?: return createCollection().iterator()
            val delegate = set.iterator()
            return object : MutableIterator<V> {
                override fun hasNext(): Boolean = delegate.hasNext()

                override fun next(): V = delegate.next()

                override fun remove() {
                    delegate.remove()
                    if (set.isEmpty()) backing.remove(key)
                }
            }
        }
    }
}
