package dev.ajthom.kollections.table

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MutableTableTest {
    private fun sample(): MutableTable<String, String, Int> = SimpleTable<String, String, Int>().apply {
        put("a", "x", 1)
        put("a", "y", 2)
        put("b", "y", 3)
    }

    @Test
    fun putReturnsNullForANewCell() {
        assertNull(SimpleTable<String, String, Int>().put("a", "x", 1))
    }

    @Test
    fun putReturnsThePreviousValue() {
        val table = sample()
        assertEquals(1, table.put("a", "x", 9))
    }

    @Test
    fun putReplacesThePreviousValue() {
        val table = sample()
        table.put("a", "x", 9)
        assertEquals(9, table["a", "x"])
    }

    @Test
    fun putCreatesTheRow() {
        val table = SimpleTable<String, String, Int>()
        table.put("a", "x", 1)
        assertTrue(table.containsRow("a"))
    }

    @Test
    fun sizeCountsCellsAcrossRows() {
        assertEquals(3, sample().size)
    }

    @Test
    fun removeReturnsTheRemovedValue() {
        assertEquals(3, sample().remove("b", "y"))
    }

    @Test
    fun removeDropsTheRowWhenItsLastCellGoes() {
        val table = sample()
        table.remove("b", "y")
        assertFalse(table.containsRow("b"))
    }

    @Test
    fun removeKeepsTheRowWhenOtherCellsRemain() {
        val table = sample()
        table.remove("a", "x")
        assertEquals(mapOf("y" to 2), table.row("a"))
    }

    @Test
    fun removeReturnsNullForAnAbsentCell() {
        assertNull(sample().remove("a", "zzz"))
    }

    @Test
    fun removeOnAnAbsentRowReturnsNull() {
        assertNull(sample().remove("zzz", "x"))
    }

    @Test
    fun removeOnAnAbsentRowDoesNotCreateIt() {
        val table = sample()
        table.remove("zzz", "x")
        assertFalse(table.containsRow("zzz"))
    }

    @Test
    fun putAllCopiesEveryCell() {
        val table = SimpleTable<String, String, Int>()
        table.putAll(sample())
        assertEquals(3, table.size)
    }

    @Test
    fun putAllOverwritesExistingCells() {
        val table = SimpleTable<String, String, Int>()
        table.put("a", "x", 99)
        table.putAll(sample())
        assertEquals(1, table["a", "x"])
    }

    @Test
    fun putAllDoesNotShareRowsWithTheSource() {
        val source = sample()
        val table = SimpleTable<String, String, Int>()
        table.putAll(source)
        source.put("a", "z", 4)
        assertNull(table["a", "z"])
    }

    @Test
    fun clearRemovesEveryCell() {
        val table = sample()
        table.clear()
        assertTrue(table.isEmpty())
    }

    @Test
    fun clearRemovesEveryRow() {
        val table = sample()
        table.clear()
        assertEquals(emptySet(), table.rowKeySet())
    }

    @Test
    fun rowViewReflectsLaterPuts() {
        val table = sample()
        val row = table.row("a")
        table.put("a", "z", 4)
        assertEquals(3, row.size)
    }

    @Test
    fun rowViewOfAnAbsentRowIsEmpty() {
        assertEquals(0, sample().row("zzz").size)
    }

    @Test
    fun rowViewPutCreatesTheRow() {
        val table = SimpleTable<String, String, Int>()
        table.row("a")["x"] = 1
        assertEquals(1, table["a", "x"])
    }

    @Test
    fun rowViewPutReturnsThePreviousValue() {
        assertEquals(1, sample().row("a").put("x", 9))
    }

    @Test
    fun rowViewRemoveIsVisibleInTheTable() {
        val table = sample()
        table.row("a").remove("x")
        assertFalse(table.contains("a", "x"))
    }

    @Test
    fun rowViewRemoveOfTheLastEntryDropsTheRow() {
        val table = sample()
        table.row("b").remove("y")
        assertFalse(table.containsRow("b"))
    }

    @Test
    fun rowViewRemoveOfAnAbsentKeyReturnsNull() {
        assertNull(sample().row("a").remove("zzz"))
    }

    @Test
    fun rowViewClearDropsTheRow() {
        val table = sample()
        table.row("a").clear()
        assertFalse(table.containsRow("a"))
    }

    @Test
    fun rowViewEntriesIteratorRemoveDropsTheRow() {
        val table = sample()
        val iterator = table.row("b").entries.iterator()
        iterator.next()
        iterator.remove()
        assertFalse(table.containsRow("b"))
    }

    @Test
    fun rowViewEntriesIteratorRemoveKeepsANonEmptyRow() {
        val table = sample()
        val iterator = table.row("a").entries.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(mapOf("y" to 2), table.row("a"))
    }

    @Test
    fun rowViewKeysRemoveDropsTheRow() {
        val table = sample()
        table.row("b").keys.remove("y")
        assertFalse(table.containsRow("b"))
    }

    @Test
    fun rowViewValuesRemoveDropsTheRow() {
        val table = sample()
        table.row("b").values.remove(3)
        assertFalse(table.containsRow("b"))
    }

    @Test
    fun rowViewEntriesOfAnAbsentRowIsEmpty() {
        assertFalse(sample().row("zzz").entries.iterator().hasNext())
    }

    @Test
    fun rowViewEqualsAPlainMapWithTheSameContent() {
        assertEquals<Map<String, Int>>(mapOf("x" to 1, "y" to 2), sample().row("a"))
    }

    @Test
    fun rowMapGetReturnsALiveRowView() {
        val table = sample()
        val row = table.rowMap().getValue("a")
        row["z"] = 4
        assertEquals(4, table["a", "z"])
    }

    @Test
    fun rowMapGetReturnsNullForAnAbsentRow() {
        assertNull(sample().rowMap()["zzz"])
    }

    @Test
    fun rowMapContainsKeyForPresentRows() {
        assertTrue(sample().rowMap().containsKey("a"))
    }

    @Test
    fun rowMapSizeCountsRows() {
        assertEquals(2, sample().rowMap().size)
    }

    @Test
    fun rowMapEntriesAreRecomputedOnEachAccess() {
        val table = sample()
        val rowMap = table.rowMap()
        table.put("c", "x", 4)
        assertEquals(3, rowMap.entries.size)
    }

    @Test
    fun rowMapEntryValuesAreLiveViews() {
        val table = sample()
        val entry = table.rowMap().entries.first { it.key == "b" }
        entry.value.clear()
        assertFalse(table.containsRow("b"))
    }

    @Test
    fun rowMapEqualsAPlainNestedMap() {
        val expected = mapOf("a" to mapOf("x" to 1, "y" to 2), "b" to mapOf("y" to 3))
        assertEquals<Map<String, Map<String, Int>>>(expected, sample().rowMap())
    }

    @Test
    fun rowKeySetRemoveDropsTheRow() {
        val table = sample()
        table.rowKeySet().remove("a")
        assertFalse(table.containsRow("a"))
    }

    @Test
    fun rowKeySetRemoveDropsEveryCellOfTheRow() {
        val table = sample()
        table.rowKeySet().remove("a")
        assertEquals(1, table.size)
    }

    @Test
    fun rowKeySetIsLive() {
        val table = sample()
        val rowKeys = table.rowKeySet()
        table.put("c", "x", 4)
        assertEquals(3, rowKeys.size)
    }

    @Test
    fun rowKeySetIteratorRemoveDropsTheRow() {
        val table = sample()
        val iterator = table.rowKeySet().iterator()
        iterator.next()
        iterator.remove()
        assertFalse(table.containsRow("a"))
    }

    @Test
    fun rowViewPutAllAddsEveryEntry() {
        val table = SimpleTable<String, String, Int>()
        table.row("a").putAll(mapOf("x" to 1, "y" to 2))
        assertEquals(2, table.size)
    }

    @Test
    fun rowViewEntriesRejectAdditions() {
        val table = sample()
        assertFailsWith<UnsupportedOperationException> {
            table.row("a").entries.add(mutableMapOf("z" to 4).entries.first())
        }
    }

    @Test
    fun putAllAcceptsATableOfSubtypes() {
        val target: MutableTable<CharSequence, CharSequence, Number> = SimpleTable()
        target.putAll(sample())
        assertEquals(3, target.size)
    }

    @Test
    fun cellSetIsASnapshot() {
        val table = sample()
        val cells = table.cellSet()
        table.put("c", "x", 4)
        assertEquals(3, cells.size)
    }

    @Test
    fun valuesIsASnapshot() {
        val table = sample()
        val values = table.values()
        table.put("c", "x", 4)
        assertEquals(3, values.size)
    }

    @Test
    fun columnIsASnapshot() {
        val table = sample()
        val column = table.column("y")
        table.put("c", "y", 4)
        assertEquals(2, column.size)
    }

    @Test
    fun columnHoldsEveryRowWithThatColumn() {
        assertEquals(mapOf("a" to 2, "b" to 3), sample().column("y"))
    }

    @Test
    fun columnMapIsASnapshot() {
        val table = sample()
        val columnMap = table.columnMap()
        table.put("c", "z", 4)
        assertEquals(2, columnMap.size)
    }

    @Test
    fun columnKeySetIsASnapshot() {
        val table = sample()
        val columnKeys = table.columnKeySet()
        table.put("c", "z", 4)
        assertEquals(2, columnKeys.size)
    }

    @Test
    fun transposeSwapsRowAndColumnKeys() {
        assertEquals(2, sample().transpose()["y", "a"])
    }

    @Test
    fun transposeIsIndependentOfTheSource() {
        val table = sample()
        val transposed = table.transpose()
        table.put("c", "y", 4)
        assertEquals(3, transposed.size)
    }

    @Test
    fun theSourceIsIndependentOfTheTranspose() {
        val table = sample()
        table.transpose().put("y", "c", 4)
        assertEquals(3, table.size)
    }

    @Test
    fun transposeIsMutable() {
        val transposed: MutableTable<String, String, Int> = sample().transpose()
        transposed.put("z", "a", 4)
        assertEquals(4, transposed["z", "a"])
    }

    @Test
    fun rowFactoryCreatesOneRowPerRowKey() {
        var created = 0
        val table = DelegatingMutableTable<String, String, Int>(LinkedHashMap()) {
            created++
            LinkedHashMap()
        }
        table.put("a", "x", 1)
        table.put("a", "y", 2)
        table.put("b", "x", 3)
        assertEquals(2, created)
    }

    @Test
    fun rowFactoryIsUsedByTheRowView() {
        var created = 0
        val table = DelegatingMutableTable<String, String, Int>(LinkedHashMap()) {
            created++
            LinkedHashMap()
        }
        table.row("a")["x"] = 1
        assertEquals(1, created)
    }

    @Test
    fun aDelegatingMutableTableWritesThroughToItsBackingMap() {
        val backing = LinkedHashMap<String, MutableMap<String, Int>>()
        val table = DelegatingMutableTable(backing)
        table.put("a", "x", 1)
        assertEquals(mapOf("a" to mapOf("x" to 1)), backing)
    }

    @Test
    fun aSimpleTableKeepsRowsInInsertionOrder() {
        val table = SimpleTable<String, String, Int>()
        table.put("b", "x", 1)
        table.put("a", "x", 2)
        assertEquals(listOf("b", "a"), table.rowKeySet().toList())
    }

    @Test
    fun toStringRendersTheRowMap() {
        assertEquals("{a={x=1, y=2}, b={y=3}}", sample().toString())
    }
}
