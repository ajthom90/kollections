package dev.ajthom.kollections.table

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TableTest {
    private fun sample(): Table<String, String, Int> = DelegatingTable(
        linkedMapOf(
            "a" to linkedMapOf("x" to 1, "y" to 2),
            "b" to linkedMapOf("y" to 3),
        ),
    )

    @Test
    fun getReturnsValueOfPresentCell() {
        assertEquals(2, sample()["a", "y"])
    }

    @Test
    fun getReturnsNullForMissingColumnInPresentRow() {
        assertNull(sample()["a", "z"])
    }

    @Test
    fun getReturnsNullForAbsentRowEvenWhenColumnExistsElsewhere() {
        assertNull(sample()["zzz", "x"])
    }

    @Test
    fun sizeCountsCellsNotRows() {
        assertEquals(3, sample().size)
    }

    @Test
    fun sizeOfEmptyTableIsZero() {
        assertEquals(0, DelegatingTable<String, String, Int>(emptyMap()).size)
    }

    @Test
    fun isEmptyIsTrueWithoutCells() {
        assertTrue(DelegatingTable<String, String, Int>(emptyMap()).isEmpty())
    }

    @Test
    fun isEmptyIsFalseWithCells() {
        assertFalse(sample().isEmpty())
    }

    @Test
    fun isEmptyIsTrueWhenEveryRowOfTheBackingMapIsEmpty() {
        val table = DelegatingTable<String, String, Int>(mapOf("r" to emptyMap()))
        assertTrue(table.isEmpty())
    }

    @Test
    fun isEmptyAgreesWithSizeWhenTheBackingMapHoldsAnEmptyRow() {
        val table = DelegatingTable<String, String, Int>(mapOf("r" to emptyMap()))
        assertEquals(0, table.size)
    }

    @Test
    fun containsIsTrueForPresentCell() {
        assertTrue(sample().contains("b", "y"))
    }

    @Test
    fun containsIsFalseForAbsentCell() {
        assertFalse(sample().contains("b", "x"))
    }

    @Test
    fun containsRowIsTrueForPresentRow() {
        assertTrue(sample().containsRow("a"))
    }

    @Test
    fun containsRowIsFalseForAbsentRow() {
        assertFalse(sample().containsRow("c"))
    }

    @Test
    fun containsColumnIsTrueForPresentColumn() {
        assertTrue(sample().containsColumn("y"))
    }

    @Test
    fun containsColumnIsFalseForAbsentColumn() {
        assertFalse(sample().containsColumn("z"))
    }

    @Test
    fun containsValueIsTrueForPresentValue() {
        assertTrue(sample().containsValue(3))
    }

    @Test
    fun containsValueIsFalseForAbsentValue() {
        assertFalse(sample().containsValue(99))
    }

    @Test
    fun rowReturnsColumnsOfThatRow() {
        assertEquals(mapOf("x" to 1, "y" to 2), sample().row("a"))
    }

    @Test
    fun rowReturnsEmptyMapForAbsentRow() {
        assertEquals(emptyMap(), sample().row("zzz"))
    }

    @Test
    fun columnReturnsRowsHoldingThatColumn() {
        assertEquals(mapOf("a" to 2, "b" to 3), sample().column("y"))
    }

    @Test
    fun columnReturnsEmptyMapForAbsentColumn() {
        assertEquals(emptyMap(), sample().column("zzz"))
    }

    @Test
    fun columnKeepsARowWhoseCellHoldsNull() {
        val table = DelegatingTable<String, String, Int?>(mapOf("a" to mapOf("x" to null)))
        assertEquals(mapOf("a" to null), table.column("x"))
    }

    @Test
    fun columnAgreesWithContainsColumnForACellHoldingNull() {
        val table = DelegatingTable<String, String, Int?>(mapOf("a" to mapOf("x" to null)))
        assertEquals(table.containsColumn("x"), table.column("x").isNotEmpty())
    }

    @Test
    fun columnAgreesWithContainsColumnForAPresentCell() {
        val table = sample()
        assertEquals(table.containsColumn("y"), table.column("y").isNotEmpty())
    }

    @Test
    fun columnAgreesWithContainsColumnForAnAbsentColumn() {
        val table = sample()
        assertEquals(table.containsColumn("zzz"), table.column("zzz").isNotEmpty())
    }

    @Test
    fun columnUsesTheRowMapsOwnKeyLookup() {
        val table = DelegatingTable(mapOf("a" to CaseInsensitiveRow(mapOf("x" to 1))))
        assertEquals(mapOf("a" to 1), table.column("X"))
    }

    @Test
    fun columnAgreesWithContainsColumnUnderTheRowMapsOwnKeyLookup() {
        val table = DelegatingTable(mapOf("a" to CaseInsensitiveRow(mapOf("x" to 1))))
        assertEquals(table.containsColumn("X"), table.column("X").isNotEmpty())
    }

    @Test
    fun rowKeySetHoldsRowKeysInOrder() {
        assertEquals(listOf("a", "b"), sample().rowKeySet().toList())
    }

    @Test
    fun columnKeySetHoldsDistinctColumnKeysInFirstSeenOrder() {
        assertEquals(listOf("x", "y"), sample().columnKeySet().toList())
    }

    @Test
    fun rowMapHoldsEveryRow() {
        assertEquals(mapOf("a" to mapOf("x" to 1, "y" to 2), "b" to mapOf("y" to 3)), sample().rowMap())
    }

    @Test
    fun columnMapGroupsCellsByColumn() {
        assertEquals(mapOf("x" to mapOf("a" to 1), "y" to mapOf("a" to 2, "b" to 3)), sample().columnMap())
    }

    @Test
    fun cellSetHoldsEveryCellInRowThenColumnOrder() {
        val expected = listOf(
            TableCell("a", "x", 1),
            TableCell("a", "y", 2),
            TableCell("b", "y", 3),
        )
        assertEquals(expected, sample().cellSet().toList())
    }

    @Test
    fun valuesKeepsDuplicates() {
        val table = DelegatingTable(mapOf("a" to mapOf("x" to 1), "b" to mapOf("x" to 1)))
        assertEquals(listOf(1, 1), table.values().toList())
    }

    @Test
    fun valuesAreInRowThenColumnOrder() {
        assertEquals(listOf(1, 2, 3), sample().values().toList())
    }

    @Test
    fun transposeSwapsRowAndColumnKeys() {
        assertEquals(3, sample().transpose()["y", "b"])
    }

    @Test
    fun transposeHasSameCellCount() {
        assertEquals(3, sample().transpose().size)
    }

    @Test
    fun transposeIsIndependentOfTheSource() {
        val backing = linkedMapOf("a" to mutableMapOf("x" to 1))
        val transposed = DelegatingTable(backing).transpose()
        backing["b"] = mutableMapOf("x" to 2)
        assertEquals(1, transposed.size)
    }

    @Test
    fun forEachVisitsEveryCellInRowThenColumnOrder() {
        val visited = mutableListOf<String>()
        sample().forEach { rowKey, columnKey, value -> visited.add("$rowKey$columnKey$value") }
        assertEquals(listOf("ax1", "ay2", "by3"), visited)
    }

    @Test
    fun forEachDefaultImplementationVisitsEveryCell() {
        val visited = mutableListOf<String>()
        val table: Table<String, String, Int> = MapBackedTable(mapOf("a" to mapOf("x" to 1, "y" to 2)))
        table.forEach { rowKey, columnKey, value -> visited.add("$rowKey$columnKey$value") }
        assertEquals(listOf("ax1", "ay2"), visited)
    }

    @Test
    fun equalTablesAreEqual() {
        assertEquals(sample(), sample())
    }

    @Test
    fun tablesWithDifferentCellsAreNotEqual() {
        assertNotEquals<Table<String, String, Int>>(sample(), DelegatingTable(mapOf("a" to mapOf("x" to 1))))
    }

    @Test
    fun equalTablesOfDifferentImplementationsAreEqual() {
        val mutable: Table<String, String, Int> = DelegatingMutableTable<String, String, Int>(LinkedHashMap()).apply {
            put("a", "x", 1)
            put("a", "y", 2)
            put("b", "y", 3)
        }
        assertEquals(sample(), mutable)
    }

    @Test
    fun equalTablesHaveEqualHashCodes() {
        assertEquals(sample().hashCode(), sample().hashCode())
    }

    @Test
    fun aTableIsNotEqualToItsRowMap() {
        assertNotEquals<Any>(sample(), sample().rowMap())
    }

    @Test
    fun toStringRendersTheRowMap() {
        assertEquals("{a={x=1, y=2}, b={y=3}}", sample().toString())
    }

    @Test
    fun delegatingTableReflectsChangesInTheBackingMap() {
        val backing = linkedMapOf("a" to mutableMapOf("x" to 1))
        val table = DelegatingTable(backing)
        backing.getValue("a")["y"] = 2
        assertEquals(2, table["a", "y"])
    }

    @Test
    fun tableCellExposesItsComponents() {
        val (rowKey, columnKey, value) = TableCell("a", "x", 1)
        assertEquals(listOf<Any>("a", "x", 1), listOf<Any>(rowKey, columnKey, value))
    }
}

/**
 * A read-only row map that looks keys up case-insensitively, standing in for the comparator-backed
 * row maps that common Kotlin has no type for (a JVM `TreeMap(String.CASE_INSENSITIVE_ORDER)`).
 * A table over it must consult the row map itself rather than comparing keys with `==`.
 */
private class CaseInsensitiveRow(private val backing: Map<String, Int>) : Map<String, Int> {
    override val entries: Set<Map.Entry<String, Int>> get() = backing.entries

    override val keys: Set<String> get() = backing.keys

    override val size: Int get() = backing.size

    override val values: Collection<Int> get() = backing.values

    override fun containsKey(key: String): Boolean = backing.containsKey(key.lowercase())

    override fun containsValue(value: Int): Boolean = backing.containsValue(value)

    override fun get(key: String): Int? = backing[key.lowercase()]

    override fun isEmpty(): Boolean = backing.isEmpty()
}

/**
 * A minimal [Table] implementation that does not extend `AbstractTable`, used to exercise the
 * default method bodies declared on the interface itself.
 */
private class MapBackedTable<R, C, V>(private val rows: Map<R, Map<C, V>>) : Table<R, C, V> {
    override val size: Int get() = rows.values.sumOf { it.size }

    override fun isEmpty(): Boolean = rows.isEmpty()

    override fun get(rowKey: R, columnKey: C): V? = rows[rowKey]?.get(columnKey)

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

    override fun columnKeySet(): Set<C> = rows.values.flatMapTo(LinkedHashSet()) { it.keys }

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
        for ((rowKey, row) in rows) {
            for ((columnKey, value) in row) {
                cells.add(TableCell(rowKey, columnKey, value))
            }
        }
        return cells
    }

    override fun values(): Collection<V> = rows.values.flatMap { it.values }

    override fun transpose(): Table<C, R, V> = MapBackedTable(columnMap())
}
