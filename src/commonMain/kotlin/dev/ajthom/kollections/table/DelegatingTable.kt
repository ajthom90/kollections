package dev.ajthom.kollections.table

/**
 * A read-only [Table] backed by the nested map [map].
 *
 * The table is a *view*: it never copies [map], so later changes to that map — or to the row maps
 * inside it — are visible through the table. Pass a map that nothing else can reach, or use
 * [dev.ajthom.kollections.table.toTable], when an independent snapshot is wanted.
 *
 * The caller is responsible for the table invariant: a row mapped to an empty map would be reported
 * by [containsRow] even though the table holds no cell in it.
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 * @param map the nested map of row key to column key to value that backs this table.
 */
public open class DelegatingTable<R, C, V>(private val map: Map<R, Map<C, V>>) : AbstractTable<R, C, V>() {
    protected override val rows: Map<R, Map<C, V>> get() = map
}
