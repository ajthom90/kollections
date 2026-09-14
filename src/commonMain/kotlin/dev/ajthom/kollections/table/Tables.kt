package dev.ajthom.kollections.table

/** Returns an empty read-only [Table]. */
public fun <R, C, V> emptyTable(): Table<R, C, V> = DelegatingTable(emptyMap())

/**
 * Returns a read-only [Table] holding [cells], each a `Triple` of row key, column key, and value.
 * When two triples share a row and column key, the later one wins.
 */
public fun <R, C, V> tableOf(vararg cells: Triple<R, C, V>): Table<R, C, V> =
    DelegatingTable(cells.asIterable().toRowMap())

/**
 * Returns a [MutableTable] holding [cells], each a `Triple` of row key, column key, and value. When
 * two triples share a row and column key, the later one wins.
 */
public fun <R, C, V> mutableTableOf(vararg cells: Triple<R, C, V>): MutableTable<R, C, V> =
    DelegatingMutableTable(cells.asIterable().toRowMap())

/**
 * Returns a read-only [Table] holding the cells of this iterable of row key, column key, and value
 * triples. The table is a copy; later changes to this iterable are not visible in it.
 */
public fun <R, C, V> Iterable<Triple<R, C, V>>.toTable(): Table<R, C, V> = DelegatingTable(toRowMap())

/**
 * Returns a [MutableTable] holding the cells of this iterable of row key, column key, and value
 * triples. The table is a copy; later changes to this iterable are not visible in it.
 */
public fun <R, C, V> Iterable<Triple<R, C, V>>.toMutableTable(): MutableTable<R, C, V> =
    DelegatingMutableTable(toRowMap())

/**
 * Returns an independent read-only copy of this table. Later changes to either table are not
 * visible in the other.
 */
public fun <R, C, V> Table<R, C, V>.toTable(): Table<R, C, V> = DelegatingTable(toRowMap())

/**
 * Returns an independent [MutableTable] copy of this table. Later changes to either table are not
 * visible in the other.
 */
public fun <R, C, V> Table<R, C, V>.toMutableTable(): MutableTable<R, C, V> = DelegatingMutableTable(toRowMap())

/**
 * Returns an independent read-only [Table] holding the cells of this nested map. Rows mapped to an
 * empty map are dropped, so the result holds the table invariant that no row maps to an empty map.
 */
public fun <R, C, V> Map<R, Map<C, V>>.toTable(): Table<R, C, V> {
    val rows = LinkedHashMap<R, Map<C, V>>()
    for ((rowKey, row) in this) {
        if (row.isNotEmpty()) {
            rows[rowKey] = row.toMap()
        }
    }
    return DelegatingTable(rows)
}

/**
 * Returns a read-only [Table] view of this nested map. Nothing is copied, so later changes to this
 * map — or to the row maps inside it — are visible through the table. The caller is responsible for
 * the table invariant that no row maps to an empty map; use [toTable] to copy and enforce it.
 */
public fun <R, C, V> Map<R, Map<C, V>>.asTable(): Table<R, C, V> = DelegatingTable(this)

/**
 * Builds a read-only [Table] by applying [builderAction] to a new insertion ordered [MutableTable].
 * The returned table is an independent copy of the builder's result.
 */
public inline fun <R, C, V> buildTable(builderAction: MutableTable<R, C, V>.() -> Unit): Table<R, C, V> =
    SimpleTable<R, C, V>().apply(builderAction).toTable()

/**
 * Stores [value] at [rowKey] / [columnKey], so that a cell can be written as
 * `table[rowKey, columnKey] = value`.
 */
public operator fun <R, C, V> MutableTable<R, C, V>.set(rowKey: R, columnKey: C, value: V) {
    put(rowKey, columnKey, value)
}

/** Returns `true` when this table holds at least one cell. */
public fun <R, C, V> Table<R, C, V>.isNotEmpty(): Boolean = !isEmpty()

/** Returns this table, or an empty table when it is `null`. */
public fun <R, C, V> Table<R, C, V>?.orEmpty(): Table<R, C, V> = this ?: emptyTable()

/**
 * Returns a read-only [Table] with the same cells as this one, holding the result of applying
 * [transform] to each value.
 */
public fun <R, C, V, W> Table<R, C, V>.mapValues(transform: (V) -> W): Table<R, C, W> {
    val rows = LinkedHashMap<R, MutableMap<C, W>>()
    forEach { rowKey, columnKey, value ->
        rows.getOrPut(rowKey) { LinkedHashMap() }[columnKey] = transform(value)
    }
    return DelegatingTable(rows)
}

/**
 * Returns a read-only [Table] holding the cells of this table that match [predicate]. A row whose
 * every cell is filtered out is dropped along with them.
 */
public fun <R, C, V> Table<R, C, V>.filter(predicate: (TableCell<R, C, V>) -> Boolean): Table<R, C, V> {
    val rows = LinkedHashMap<R, MutableMap<C, V>>()
    forEach { rowKey, columnKey, value ->
        if (predicate(TableCell(rowKey, columnKey, value))) {
            rows.getOrPut(rowKey) { LinkedHashMap() }[columnKey] = value
        }
    }
    return DelegatingTable(rows)
}

/** Returns the number of cells in this table. */
@Deprecated(
    message = "Table.size is now a property counting cells; the row count is rowKeySet().size.",
    replaceWith = ReplaceWith("size"),
)
public fun <R, C, V> Table<R, C, V>.size(): Int = size

/** Returns an independent read-only copy of this table. */
@Deprecated(
    message = "Use toTable(), which names the copy it makes.",
    replaceWith = ReplaceWith("toTable()", "dev.ajthom.kollections.table.toTable"),
)
public fun <R, C, V> MutableTable<R, C, V>.asTable(): Table<R, C, V> = toTable()

/** Collects the triples of this iterable into a fresh insertion ordered nested map of rows. */
private fun <R, C, V> Iterable<Triple<R, C, V>>.toRowMap(): MutableMap<R, MutableMap<C, V>> {
    val rows = LinkedHashMap<R, MutableMap<C, V>>()
    for ((rowKey, columnKey, value) in this) {
        rows.getOrPut(rowKey) { LinkedHashMap() }[columnKey] = value
    }
    return rows
}

/** Copies the cells of this table into a fresh insertion ordered nested map of rows. */
private fun <R, C, V> Table<R, C, V>.toRowMap(): MutableMap<R, MutableMap<C, V>> {
    val rows = LinkedHashMap<R, MutableMap<C, V>>()
    forEach { rowKey, columnKey, value ->
        rows.getOrPut(rowKey) { LinkedHashMap() }[columnKey] = value
    }
    return rows
}
