package dev.ajthom.kollections.table

/**
 * A [MutableTable] backed by the mutable nested map [backing].
 *
 * Every mutation goes straight through to [backing], so a caller that keeps a reference to it sees
 * the table's changes and the table sees theirs. New rows are created by [rowFactory], which lets
 * callers choose the row map implementation — for example a JVM caller can get a fully sorted table
 * with `DelegatingMutableTable(TreeMap()) { TreeMap() }`.
 *
 * The class upholds the invariant that no row maps to an empty map: a row is created on the first
 * put and removed as soon as its last cell is removed, whichever mutation path is used.
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 * @param backing the mutable nested map of row key to column key to value that backs this table.
 * @param rowFactory creates the map that holds a newly created row; insertion ordered by default.
 */
public open class DelegatingMutableTable<R, C, V>(
    private val backing: MutableMap<R, MutableMap<C, V>>,
    private val rowFactory: () -> MutableMap<C, V> = { LinkedHashMap() },
) : AbstractTable<R, C, V>(),
    MutableTable<R, C, V> {
    protected override val rows: Map<R, Map<C, V>> get() = backing

    override fun put(rowKey: R, columnKey: C, value: V): V? =
        backing.getOrPut(rowKey) { rowFactory() }.put(columnKey, value)

    override fun putAll(from: Table<out R, out C, out V>) {
        from.forEach { rowKey, columnKey, value -> put(rowKey, columnKey, value) }
    }

    override fun remove(rowKey: R, columnKey: C): V? {
        val row = backing[rowKey] ?: return null
        val removed = row.remove(columnKey)
        if (row.isEmpty()) {
            backing.remove(rowKey)
        }
        return removed
    }

    override fun clear() {
        backing.clear()
    }

    /**
     * Returns a **live view** of the row [rowKey]; see [MutableTable.row]. The view is usable for a
     * row that does not exist yet: the first put creates it with [rowFactory].
     */
    override fun row(rowKey: R): MutableMap<C, V> = RowView(rowKey)

    /**
     * Returns a **live view** of the row keys; see [MutableTable.rowKeySet]. Removing a key removes
     * the whole row, and keys cannot be added.
     */
    override fun rowKeySet(): MutableSet<R> = backing.keys

    /**
     * Returns a **live**, read-only view of this table as a map of row key to live row view; see
     * [MutableTable.rowMap].
     */
    override fun rowMap(): Map<R, MutableMap<C, V>> = RowMapView()

    /**
     * Returns an independent mutable **copy** of this table with its row and column keys swapped.
     * The copy is insertion ordered regardless of [rowFactory], because a row of the transpose is
     * keyed by the row keys of this table rather than by its column keys.
     */
    override fun transpose(): MutableTable<C, R, V> {
        val transposed = LinkedHashMap<C, MutableMap<R, V>>()
        forEach { rowKey, columnKey, value ->
            transposed.getOrPut(columnKey) { LinkedHashMap() }[rowKey] = value
        }
        return DelegatingMutableTable(transposed)
    }

    /**
     * A live view of a single row. It holds no reference to the row map itself, only to its key, so
     * it stays correct across removal and re-creation of the row.
     */
    private inner class RowView(private val rowKey: R) : AbstractMutableMap<C, V>() {
        private fun backingRow(): MutableMap<C, V>? = backing[rowKey]

        override val size: Int get() = backingRow()?.size ?: 0

        override val entries: MutableSet<MutableMap.MutableEntry<C, V>> get() = RowEntrySet()

        override fun isEmpty(): Boolean = backingRow()?.isEmpty() != false

        override fun containsKey(key: C): Boolean = backingRow()?.containsKey(key) == true

        override fun containsValue(value: V): Boolean = backingRow()?.containsValue(value) == true

        override fun get(key: C): V? = backingRow()?.get(key)

        override fun put(key: C, value: V): V? = backing.getOrPut(rowKey) { rowFactory() }.put(key, value)

        override fun remove(key: C): V? = this@DelegatingMutableTable.remove(rowKey, key)

        override fun clear() {
            backing.remove(rowKey)
        }

        /**
         * The entry set of a row view. Entries cannot be added — a cell is added by putting into the
         * row — and removing the last entry through the iterator drops the row.
         */
        private inner class RowEntrySet : AbstractMutableSet<MutableMap.MutableEntry<C, V>>() {
            override val size: Int get() = backingRow()?.size ?: 0

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<C, V>> {
                val row = backingRow() ?: return mutableMapOf<C, V>().entries.iterator()
                return RowEntryIterator(row)
            }

            override fun add(element: MutableMap.MutableEntry<C, V>): Boolean =
                throw UnsupportedOperationException("add is not supported on the entries of a table row")
        }

        /** An entry iterator that drops the row once its last entry has been removed. */
        private inner class RowEntryIterator(private val row: MutableMap<C, V>) :
            MutableIterator<MutableMap.MutableEntry<C, V>> {
            private val delegate = row.entries.iterator()

            override fun hasNext(): Boolean = delegate.hasNext()

            override fun next(): MutableMap.MutableEntry<C, V> = delegate.next()

            override fun remove() {
                delegate.remove()
                if (row.isEmpty()) {
                    backing.remove(rowKey)
                }
            }
        }
    }

    /** A live, read-only map of row key to live row view. */
    private inner class RowMapView : AbstractMap<R, MutableMap<C, V>>() {
        override val size: Int get() = backing.size

        override val entries: Set<Map.Entry<R, MutableMap<C, V>>>
            get() = backing.keys.mapTo(LinkedHashSet()) { RowMapEntry(it) }

        override fun isEmpty(): Boolean = backing.isEmpty()

        override fun containsKey(key: R): Boolean = backing.containsKey(key)

        override fun get(key: R): MutableMap<C, V>? = if (backing.containsKey(key)) RowView(key) else null
    }

    /** An entry of [RowMapView], whose value is the live row view for its key. */
    private inner class RowMapEntry(override val key: R) : Map.Entry<R, MutableMap<C, V>> {
        override val value: MutableMap<C, V> get() = RowView(key)

        override fun equals(other: Any?): Boolean = other is Map.Entry<*, *> && other.key == key && other.value == value

        override fun hashCode(): Int = (key?.hashCode() ?: 0) xor value.hashCode()

        override fun toString(): String = "$key=$value"
    }
}
