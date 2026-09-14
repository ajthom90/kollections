package dev.ajthom.kollections.table

/**
 * A collection that associates an ordered pair of keys — a *row key* and a *column key* — with a
 * single value, in the spirit of Guava's `Table`.
 *
 * A table is best pictured as a `Map<R, Map<C, V>>` that additionally offers column-oriented
 * access. Implementations hold the invariant that **no row maps to an empty map**: a row key is
 * present exactly when the table holds at least one cell in that row.
 *
 * The read-only interface makes no promise about mutability of the underlying data. An instance
 * obtained from [dev.ajthom.kollections.table.asTable] is a view of a live map, while one obtained
 * from [dev.ajthom.kollections.table.toTable] is an independent copy.
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 */
public interface Table<R, C, V> {
    /** The number of cells in this table, which is the total number of values it holds. */
    public val size: Int

    /** Returns `true` when this table holds no cells. */
    public fun isEmpty(): Boolean

    /**
     * Returns the value stored at [rowKey] / [columnKey], or `null` when the table holds no such
     * cell. This is a nested map lookup; other rows are never scanned.
     */
    public operator fun get(rowKey: R, columnKey: C): V?

    /**
     * Returns `true` when this table holds a cell at [rowKey] / [columnKey]. Prefer this over
     * `get(...) != null` when the value type is nullable.
     */
    public fun contains(rowKey: R, columnKey: C): Boolean

    /** Returns `true` when this table holds at least one cell in the row [rowKey]. */
    public fun containsRow(rowKey: R): Boolean

    /** Returns `true` when this table holds at least one cell in the column [columnKey]. */
    public fun containsColumn(columnKey: C): Boolean

    /** Returns `true` when [value] is stored in at least one cell of this table. */
    public fun containsValue(value: V): Boolean

    /**
     * Returns the column key to value mapping of the row [rowKey], or an empty map when the row is
     * absent.
     */
    public fun row(rowKey: R): Map<C, V>

    /**
     * Returns the row key to value mapping of the column [columnKey], or an empty map when the
     * column is absent.
     */
    public fun column(columnKey: C): Map<R, V>

    /** Returns the row keys of this table, in row order. */
    public fun rowKeySet(): Set<R>

    /** Returns the distinct column keys of this table, in the order they are first encountered. */
    public fun columnKeySet(): Set<C>

    /** Returns this table as a map of row key to that row's column key to value mapping. */
    public fun rowMap(): Map<R, Map<C, V>>

    /** Returns this table as a map of column key to that column's row key to value mapping. */
    public fun columnMap(): Map<C, Map<R, V>>

    /** Returns every cell of this table, in row order and, within a row, in column order. */
    public fun cellSet(): Set<TableCell<R, C, V>>

    /**
     * Returns the values of this table, in row order and, within a row, in column order. Duplicate
     * values are kept.
     */
    public fun values(): Collection<V>

    /**
     * Returns an independent **copy** of this table with its row and column keys swapped; later
     * changes to this table are not visible in the copy.
     */
    public fun transpose(): Table<C, R, V>

    /**
     * Invokes [action] once for every cell of this table, in row order and, within a row, in column
     * order.
     */
    public fun forEach(action: (rowKey: R, columnKey: C, value: V) -> Unit) {
        for ((rowKey, row) in rowMap()) {
            for ((columnKey, value) in row) {
                action(rowKey, columnKey, value)
            }
        }
    }
}

/**
 * A single cell of a [Table]: the value stored at the intersection of [rowKey] and [columnKey].
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 */
public data class TableCell<R, C, V>(
    /** The row key of this cell. */
    public val rowKey: R,
    /** The column key of this cell. */
    public val columnKey: C,
    /** The value stored in this cell. */
    public val value: V,
)
