package dev.ajthom.kollections.table

/**
 * A [Table] that supports adding, replacing, and removing cells.
 *
 * The invariant that no row maps to an empty map is upheld by every mutation path: a row is created
 * on the first cell put into it and dropped as soon as its last cell is removed, whether that
 * happens through [remove], through the map returned by [row], or through that map's `entries`,
 * `keys`, or `values` iterators.
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 */
public interface MutableTable<R, C, V> : Table<R, C, V> {
    /**
     * Returns a **live view** of the row [rowKey]. Changes to the table are visible in the view and
     * changes made through the view are visible in the table, including for a row that is absent
     * when the view is created: putting into that view creates the row.
     */
    override fun row(rowKey: R): MutableMap<C, V>

    /**
     * Returns a **live view** of the row keys of this table. Removing a key removes that whole row;
     * keys cannot be added, because a row can never be empty.
     */
    override fun rowKeySet(): MutableSet<R>

    /**
     * Returns a **live view** of this table as a map of row key to row. The map itself is read-only,
     * but each value is the live row view returned by [row], so writing through a value writes
     * through to the table.
     */
    override fun rowMap(): Map<R, MutableMap<C, V>>

    /** Returns a **snapshot** of the column [columnKey]; later changes to the table are not visible. */
    override fun column(columnKey: C): Map<R, V>

    /** Returns a **snapshot** of the column keys; later changes to the table are not visible. */
    override fun columnKeySet(): Set<C>

    /** Returns a **snapshot** of the column map; later changes to the table are not visible. */
    override fun columnMap(): Map<C, Map<R, V>>

    /** Returns a **snapshot** of the cells; later changes to the table are not visible. */
    override fun cellSet(): Set<TableCell<R, C, V>>

    /** Returns a **snapshot** of the values; later changes to the table are not visible. */
    override fun values(): Collection<V>

    /**
     * Returns an independent mutable **copy** of this table with its row and column keys swapped;
     * later changes to either table are not visible in the other.
     */
    override fun transpose(): MutableTable<C, R, V>

    /**
     * Stores [value] at [rowKey] / [columnKey], creating the row when it is absent, and returns the
     * value previously stored there or `null` when the cell was empty.
     */
    public fun put(rowKey: R, columnKey: C, value: V): V?

    /** Copies every cell of [from] into this table, replacing values of cells that already exist. */
    public fun putAll(from: Table<out R, out C, out V>)

    /**
     * Removes the cell at [rowKey] / [columnKey] and returns the value it held, or `null` when there
     * was no such cell. Removing the last cell of a row also removes the row; an absent row is never
     * created.
     */
    public fun remove(rowKey: R, columnKey: C): V?

    /** Removes every cell, and therefore every row, from this table. */
    public fun clear()
}
