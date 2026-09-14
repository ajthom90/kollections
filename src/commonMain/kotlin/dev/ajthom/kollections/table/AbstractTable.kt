package dev.ajthom.kollections.table

/**
 * A skeletal [Table] implementation backed by a nested map of rows.
 *
 * Subclasses supply [rows] and inherit every read-only operation, along with equality, hash code,
 * and string rendering:
 *
 * - two tables are equal when their [cellSet]s are equal, regardless of implementation,
 * - [hashCode] is the hash code of the [cellSet],
 * - [toString] renders the [rowMap], for example `{a={x=1, y=2}, b={y=3}}`.
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 */
public abstract class AbstractTable<R, C, V> : Table<R, C, V> {
    /**
     * The rows of this table, keyed by row key. Implementations must uphold the table invariant
     * that no row maps to an empty map.
     */
    protected abstract val rows: Map<R, Map<C, V>>

    override val size: Int get() = rows.values.sumOf { it.size }

    // Checks the rows rather than the row map itself so that this can never disagree with [size]
    // when a caller-supplied map breaks the invariant by holding an empty row.
    override fun isEmpty(): Boolean = rows.values.all { it.isEmpty() }

    override operator fun get(rowKey: R, columnKey: C): V? = rows[rowKey]?.get(columnKey)

    override fun contains(rowKey: R, columnKey: C): Boolean = rows[rowKey]?.containsKey(columnKey) == true

    override fun containsRow(rowKey: R): Boolean = rows.containsKey(rowKey)

    override fun containsColumn(columnKey: C): Boolean = rows.values.any { it.containsKey(columnKey) }

    override fun containsValue(value: V): Boolean = rows.values.any { it.containsValue(value) }

    override fun row(rowKey: R): Map<C, V> = rows[rowKey] ?: emptyMap()

    override fun column(columnKey: C): Map<R, V> {
        val column = LinkedHashMap<R, V>()
        for ((rowKey, row) in rows) {
            if (row.containsKey(columnKey)) {
                @Suppress("UNCHECKED_CAST")
                column[rowKey] = row[columnKey] as V
            }
        }
        return column
    }

    override fun rowKeySet(): Set<R> = rows.keys

    override fun columnKeySet(): Set<C> {
        val columnKeys = LinkedHashSet<C>()
        for (row in rows.values) {
            columnKeys.addAll(row.keys)
        }
        return columnKeys
    }

    override fun rowMap(): Map<R, Map<C, V>> = rows

    override fun columnMap(): Map<C, Map<R, V>> {
        val columns = LinkedHashMap<C, MutableMap<R, V>>()
        for ((rowKey, row) in rows) {
            for ((columnKey, value) in row) {
                columns.getOrPut(columnKey) { LinkedHashMap() }[rowKey] = value
            }
        }
        return columns
    }

    override fun cellSet(): Set<TableCell<R, C, V>> {
        val cells = LinkedHashSet<TableCell<R, C, V>>()
        forEach { rowKey, columnKey, value -> cells.add(TableCell(rowKey, columnKey, value)) }
        return cells
    }

    override fun values(): Collection<V> {
        val values = ArrayList<V>(size)
        forEach { _, _, value -> values.add(value) }
        return values
    }

    override fun transpose(): Table<C, R, V> = DelegatingTable(columnMap())

    override fun forEach(action: (rowKey: R, columnKey: C, value: V) -> Unit) {
        for ((rowKey, row) in rows) {
            for ((columnKey, value) in row) {
                action(rowKey, columnKey, value)
            }
        }
    }

    /** Returns `true` when [other] is a [Table] holding exactly the same cells as this table. */
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Table<*, *, *>) return false
        return cellSet() == other.cellSet()
    }

    /** Returns the hash code of the [cellSet], so that equal tables have equal hash codes. */
    override fun hashCode(): Int = cellSet().hashCode()

    /** Returns the [rowMap] rendered as a string, for example `{a={x=1, y=2}, b={y=3}}`. */
    override fun toString(): String = rowMap().toString()
}
