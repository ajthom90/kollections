package dev.ajthom.kollections.multimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SetMultimapTest {
    private val multimap: SetMultimap<String, Int> = setMultimapOf("a" to 1, "a" to 2, "b" to 3)

    @Test
    fun duplicatesCollapse() {
        assertEquals(1, setMultimapOf("a" to 1, "a" to 1).size)
    }

    @Test
    fun getReturnsSetForPresentKey() {
        assertEquals(setOf(1, 2), multimap["a"])
    }

    @Test
    fun getReturnsEmptySetForAbsentKey() {
        assertEquals(emptySet(), multimap["zzz"])
    }

    @Test
    fun sizeCountsDistinctKeyValuePairs() {
        assertEquals(3, multimap.size)
    }

    @Test
    fun valuesAreSetsPerKey() {
        assertEquals(listOf(setOf(1, 2), setOf(3)), multimap.values.toList())
    }

    @Test
    fun flatValuesAreInInsertionOrder() {
        assertEquals(listOf(1, 2, 3), multimap.flatValues.toList())
    }

    @Test
    fun entriesDestructureIntoKeyAndValueSet() {
        val entries = multimap.entries.map { (key, values) -> key to values }
        assertEquals(listOf("a" to setOf(1, 2), "b" to setOf(3)), entries)
    }

    @Test
    fun containsEntryIsTrueForPresentPair() {
        assertTrue(multimap.containsEntry("a", 1))
    }

    @Test
    fun containsEntryIsFalseForAbsentPair() {
        assertFalse(multimap.containsEntry("a", 3))
    }

    @Test
    fun isEmptyIsTrueForEmptySetMultimap() {
        assertTrue(emptySetMultimap<String, Int>().isEmpty())
    }

    @Test
    fun equalityIgnoresValueOrder() {
        assertEquals(setMultimapOf("a" to 2, "a" to 1, "b" to 3), multimap)
    }

    @Test
    fun equalSetMultimapsHaveEqualHashCodes() {
        assertEquals(setMultimapOf("b" to 3, "a" to 2, "a" to 1).hashCode(), multimap.hashCode())
    }

    @Test
    fun setMultimapNeverEqualsListMultimapWithSameContent() {
        assertNotEquals<Multimap<String, Int>>(multimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun toStringEqualsAsMapToString() {
        assertEquals(multimap.asMap().toString(), multimap.toString())
    }

    @Test
    fun forEachVisitsEachDistinctPair() {
        val visited = mutableListOf<Pair<String, Int>>()
        multimap.forEach { key, value -> visited += key to value }
        assertEquals(listOf("a" to 1, "a" to 2, "b" to 3), visited)
    }

    @Test
    fun delegatingSetMultimapWrapsMapByReference() {
        val map = mutableMapOf("a" to setOf(1))
        val wrapped = DelegatingSetMultimap(map)
        map["b"] = setOf(2)
        assertEquals(setOf(2), wrapped["b"])
    }
}
