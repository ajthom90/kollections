package dev.ajthom.kollections.multimap

/**
 * A [MutableListMultimap] backed by [backing], whose value lists are created by [listFactory].
 *
 * The factory lets callers choose the list type for each key; the default is an [ArrayList]. The backing map
 * decides key order and must not contain an empty list.
 *
 * @param K the type of the keys
 * @param V the type of the values
 * @param backing the map holding the values of each key
 * @property listFactory creates the empty list that will hold the values of a newly inserted key
 */
public open class DelegatingMutableListMultimap<K, V>(
    backing: MutableMap<K, MutableList<V>>,
    private val listFactory: () -> MutableList<V> = { ArrayList() },
) : AbstractMutableMultimap<K, V, MutableList<V>>(backing),
    MutableListMultimap<K, V> {

    /** Creates the empty list that will hold the values of a newly inserted key, using [listFactory]. */
    override fun createCollection(): MutableList<V> = listFactory()

    /**
     * Returns a live view of the values associated with [key]. Adding to the view inserts pairs into this
     * multimap (even if the key is currently absent); removing the last value from it drops the key.
     */
    override fun get(key: K): MutableList<V> = ListView(key)

    private inner class ListView(private val key: K) : AbstractMutableList<V>() {
        private fun current(): MutableList<V>? = backing[key]

        private fun currentOrThrow(index: Int): MutableList<V> =
            current() ?: throw IndexOutOfBoundsException("index must be less than the size of 0 but was $index")

        override val size: Int
            get() = current()?.size ?: 0

        override fun get(index: Int): V = currentOrThrow(index)[index]

        override fun set(index: Int, element: V): V = currentOrThrow(index).set(index, element)

        override fun add(index: Int, element: V) {
            val existing = current()
            if (existing != null) {
                existing.add(index, element)
                return
            }
            if (index != 0) throw IndexOutOfBoundsException("index must be 0 but was $index")
            backing.getOrPut(key) { createCollection() }.add(element)
        }

        // Both addAll overloads copy their argument first so that adding a view to itself terminates.
        override fun addAll(elements: Collection<V>): Boolean = super.addAll(elements.toList())

        override fun addAll(index: Int, elements: Collection<V>): Boolean = super.addAll(index, elements.toList())

        override fun removeAt(index: Int): V {
            val list = currentOrThrow(index)
            val removed = list.removeAt(index)
            if (list.isEmpty()) backing.remove(key)
            return removed
        }

        override fun clear() {
            backing.remove(key)
        }

        override fun contains(element: V): Boolean = current()?.contains(element) == true

        override fun indexOf(element: V): Int = current()?.indexOf(element) ?: -1

        override fun lastIndexOf(element: V): Int = current()?.lastIndexOf(element) ?: -1
    }
}
