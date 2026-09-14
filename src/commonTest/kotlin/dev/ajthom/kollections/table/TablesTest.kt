package dev.ajthom.kollections.table

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TablesTest {
    private fun sample(): Table<String, String, Int> = tableOf(
        Triple("a", "x", 1),
        Triple("a", "y", 2),
        Triple("b", "y", 3),
    )

    @Test
    fun emptyTableHoldsNoCells() {
        assertTrue(emptyTable<String, String, Int>().isEmpty())
    }

    @Test
    fun emptyTableHasSizeZero() {
        assertEquals(0, emptyTable<String, String, Int>().size)
    }

    @Test
    fun tableOfHoldsEveryTriple() {
        assertEquals(3, sample().size)
    }

    @Test
    fun tableOfKeepsTripleOrder() {
        assertEquals(listOf("a", "b"), sample().rowKeySet().toList())
    }

    @Test
    fun tableOfLetsALaterTripleWin() {
        val table = tableOf(Triple("a", "x", 1), Triple("a", "x", 2))
        assertEquals(2, table["a", "x"])
    }

    @Test
    fun tableOfWithoutTriplesIsEmpty() {
        assertTrue(tableOf<String, String, Int>().isEmpty())
    }

    @Test
    fun mutableTableOfHoldsEveryTriple() {
        assertEquals(3, mutableTableOf(Triple("a", "x", 1), Triple("a", "y", 2), Triple("b", "y", 3)).size)
    }

    @Test
    fun mutableTableOfIsMutable() {
        val table = mutableTableOf(Triple("a", "x", 1))
        table.put("b", "y", 2)
        assertEquals(2, table.size)
    }

    @Test
    fun iterableOfTriplesToTableHoldsEveryCell() {
        assertEquals(2, listOf(Triple("a", "x", 1), Triple("b", "y", 2)).toTable().size)
    }

    @Test
    fun iterableOfTriplesToTableIsIndependentOfTheSource() {
        val triples = mutableListOf(Triple("a", "x", 1))
        val table = triples.toTable()
        triples.add(Triple("b", "y", 2))
        assertEquals(1, table.size)
    }

    @Test
    fun iterableOfTriplesToMutableTableHoldsEveryCell() {
        assertEquals(2, listOf(Triple("a", "x", 1), Triple("b", "y", 2)).toMutableTable().size)
    }

    @Test
    fun iterableOfTriplesToMutableTableIsMutable() {
        val table = listOf(Triple("a", "x", 1)).toMutableTable()
        table.put("b", "y", 2)
        assertEquals(2, table.size)
    }

    @Test
    fun tableToTableCopiesTheContent() {
        assertEquals(sample(), sample().toTable())
    }

    @Test
    fun tableToTableIsIndependentOfTheSource() {
        val source = mutableTableOf(Triple("a", "x", 1))
        val copy = source.toTable()
        source.put("a", "y", 2)
        assertEquals(1, copy.size)
    }

    @Test
    fun tableToTableDoesNotShareRowsWithTheSource() {
        val source = mutableTableOf(Triple("a", "x", 1))
        val copy = source.toTable()
        source.row("a")["y"] = 2
        assertNull(copy["a", "y"])
    }

    @Test
    fun tableToMutableTableCopiesTheContent() {
        assertEquals<Table<String, String, Int>>(sample(), sample().toMutableTable())
    }

    @Test
    fun tableToMutableTableIsIndependentOfTheSource() {
        val source = mutableTableOf(Triple("a", "x", 1))
        val copy = source.toMutableTable()
        source.put("a", "y", 2)
        assertEquals(1, copy.size)
    }

    @Test
    fun theSourceIsIndependentOfTableToMutableTable() {
        val source = mutableTableOf(Triple("a", "x", 1))
        source.toMutableTable().put("a", "y", 2)
        assertEquals(1, source.size)
    }

    @Test
    fun mapToTableHoldsEveryCell() {
        assertEquals(3, mapOf("a" to mapOf("x" to 1, "y" to 2), "b" to mapOf("y" to 3)).toTable().size)
    }

    @Test
    fun mapToTableIsIndependentOfTheSource() {
        val source = linkedMapOf("a" to mutableMapOf("x" to 1))
        val table = source.toTable()
        source.getValue("a")["y"] = 2
        assertEquals(1, table.size)
    }

    @Test
    fun mapToTableDropsEmptyRows() {
        assertFalse(mapOf("a" to mapOf("x" to 1), "b" to emptyMap<String, Int>()).toTable().containsRow("b"))
    }

    @Test
    fun mapAsTableHoldsEveryCell() {
        assertEquals(3, mapOf("a" to mapOf("x" to 1, "y" to 2), "b" to mapOf("y" to 3)).asTable().size)
    }

    @Test
    fun mapAsTableReflectsTheSource() {
        val source = linkedMapOf("a" to mutableMapOf("x" to 1))
        val table = source.asTable()
        source.getValue("a")["y"] = 2
        assertEquals(2, table["a", "y"])
    }

    @Test
    fun buildTableHoldsWhatTheBuilderPut() {
        val table = buildTable<String, String, Int> {
            put("a", "x", 1)
            put("b", "y", 2)
        }
        assertEquals(2, table.size)
    }

    @Test
    fun buildTableSeesRemovalsMadeByTheBuilder() {
        val table = buildTable<String, String, Int> {
            put("a", "x", 1)
            remove("a", "x")
        }
        assertTrue(table.isEmpty())
    }

    @Test
    fun setOperatorStoresTheValue() {
        val table = SimpleTable<String, String, Int>()
        table["a", "x"] = 1
        assertEquals(1, table["a", "x"])
    }

    @Test
    fun setOperatorReplacesAnExistingValue() {
        val table = mutableTableOf(Triple("a", "x", 1))
        table["a", "x"] = 2
        assertEquals(2, table["a", "x"])
    }

    @Test
    fun isNotEmptyIsTrueWithCells() {
        assertTrue(sample().isNotEmpty())
    }

    @Test
    fun isNotEmptyIsFalseWithoutCells() {
        assertFalse(emptyTable<String, String, Int>().isNotEmpty())
    }

    @Test
    fun orEmptyReturnsAnEmptyTableForNull() {
        val table: Table<String, String, Int>? = null
        assertTrue(table.orEmpty().isEmpty())
    }

    @Test
    fun orEmptyReturnsTheReceiverWhenItIsNotNull() {
        val table: Table<String, String, Int> = sample()
        assertSame(table, table.orEmpty())
    }

    @Test
    fun mapValuesTransformsEveryValue() {
        assertEquals(listOf("1", "2", "3"), sample().mapValues { it.toString() }.values().toList())
    }

    @Test
    fun mapValuesKeepsTheCellLayout() {
        assertEquals("2", sample().mapValues { it.toString() }["a", "y"])
    }

    @Test
    fun mapValuesIsIndependentOfTheSource() {
        val source = mutableTableOf(Triple("a", "x", 1))
        val mapped = source.mapValues { it * 2 }
        source.put("b", "y", 3)
        assertEquals(1, mapped.size)
    }

    @Test
    fun filterKeepsMatchingCells() {
        assertEquals(2, sample().filter { it.value > 1 }.size)
    }

    @Test
    fun filterDropsRowsThatLoseEveryCell() {
        assertFalse(sample().filter { it.rowKey == "b" }.containsRow("a"))
    }

    @Test
    fun filterCanUseTheColumnKey() {
        assertEquals(setOf("y"), sample().filter { it.columnKey == "y" }.columnKeySet())
    }

    @Test
    fun equalTablesOfEveryImplementationAreEqual() {
        val delegating: Table<String, String, Int> = DelegatingTable(
            mapOf("a" to mapOf("x" to 1, "y" to 2), "b" to mapOf("y" to 3)),
        )
        assertEquals(delegating, sample())
    }

    @Test
    fun aSimpleTableEqualsAnEqualTableOf() {
        val simple = SimpleTable<String, String, Int>()
        simple.put("a", "x", 1)
        simple.put("a", "y", 2)
        simple.put("b", "y", 3)
        assertEquals<Table<String, String, Int>>(sample(), simple)
    }

    @Test
    fun equalTablesOfDifferentImplementationsShareAHashCode() {
        assertEquals(sample().hashCode(), sample().toMutableTable().hashCode())
    }

    @Suppress("DEPRECATION")
    @Test
    fun theDeprecatedSizeFunctionCountsCells() {
        assertEquals(3, sample().size())
    }

    @Suppress("DEPRECATION")
    @Test
    fun theDeprecatedAsTableFunctionCopiesTheTable() {
        assertEquals<Table<String, String, Int>>(sample(), sample().toMutableTable().asTable())
    }

    @Suppress("DEPRECATION")
    @Test
    fun theDeprecatedAsTableFunctionIsIndependentOfTheSource() {
        val source = mutableTableOf(Triple("a", "x", 1))
        val copy = source.asTable()
        source.put("b", "y", 2)
        assertEquals(1, copy.size)
    }
}
