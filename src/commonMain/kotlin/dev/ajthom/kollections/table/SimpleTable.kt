package dev.ajthom.kollections.table

/**
 * An insertion ordered [MutableTable], the default table implementation.
 *
 * Rows are iterated in the order their first cell was added and, within a row, columns are iterated
 * in the order their cell was added. It is a [DelegatingMutableTable] over fresh
 * [LinkedHashMap]s, so nothing else can reach its backing map.
 *
 * @param R the row key type.
 * @param C the column key type.
 * @param V the value type.
 */
public class SimpleTable<R, C, V> : DelegatingMutableTable<R, C, V>(LinkedHashMap())
