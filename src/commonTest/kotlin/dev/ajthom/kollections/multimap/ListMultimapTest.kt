package dev.ajthom.kollections.multimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ListMultimapTest {
    private val multimap: ListMultimap<String, Int> = multimapOf("a" to 1, "a" to 2, "b" to 3)

    @Test
    fun getReturnsAllValuesForPresentKey() {
        assertEquals(listOf(1, 2), multimap["a"])
    }

    @Test
    fun getReturnsEmptyListForAbsentKey() {
        assertEquals(emptyList(), multimap["zzz"])
    }

    @Test
    fun getKeepsDuplicateValues() {
        assertEquals(listOf(1, 1), multimapOf("a" to 1, "a" to 1)["a"])
    }

    @Test
    fun sizeCountsKeyValuePairs() {
        assertEquals(3, multimap.size)
    }

    @Test
    fun keysAreInInsertionOrder() {
        assertEquals(listOf("a", "b"), multimap.keys.toList())
    }

    @Test
    fun valuesAreListsPerKeyInInsertionOrder() {
        assertEquals(listOf(listOf(1, 2), listOf(3)), multimap.values.toList())
    }

    @Test
    fun flatValuesAreInInsertionOrder() {
        assertEquals(listOf(1, 2, 3), multimap.flatValues.toList())
    }

    @Test
    fun entriesDestructureIntoKeyAndValueList() {
        val entries = multimap.entries.map { (key, values) -> key to values }
        assertEquals(listOf("a" to listOf(1, 2), "b" to listOf(3)), entries)
    }

    @Test
    fun containsKeyIsTrueForPresentKey() {
        assertTrue(multimap.containsKey("a"))
    }

    @Test
    fun containsKeyIsFalseForAbsentKey() {
        assertFalse(multimap.containsKey("zzz"))
    }

    @Test
    fun containsValueIsTrueForPresentValue() {
        assertTrue(multimap.containsValue(3))
    }

    @Test
    fun containsValueIsFalseForAbsentValue() {
        assertFalse(multimap.containsValue(99))
    }

    @Test
    fun containsEntryIsTrueForPresentPair() {
        assertTrue(multimap.containsEntry("a", 2))
    }

    @Test
    fun containsEntryIsFalseWhenValueIsUnderAnotherKey() {
        assertFalse(multimap.containsEntry("b", 1))
    }

    @Test
    fun isEmptyIsTrueForEmptyMultimap() {
        assertTrue(emptyMultimap<String, Int>().isEmpty())
    }

    @Test
    fun isEmptyIsFalseForPopulatedMultimap() {
        assertFalse(multimap.isEmpty())
    }

    @Test
    fun forEachVisitsPairsInOrder() {
        val visited = mutableListOf<Pair<String, Int>>()
        multimap.forEach { key, value -> visited += key to value }
        assertEquals(listOf("a" to 1, "a" to 2, "b" to 3), visited)
    }

    @Test
    fun iteratorExtensionYieldsPairs() {
        val visited = mutableListOf<Pair<String, Int>>()
        for ((key, value) in multimap) {
            visited += key to value
        }
        assertEquals(listOf("a" to 1, "a" to 2, "b" to 3), visited)
    }

    @Test
    fun asMapExposesKeysToValueLists() {
        assertEquals(mapOf("a" to listOf(1, 2), "b" to listOf(3)), multimap.asMap())
    }

    @Test
    fun equalMultimapsAreEqual() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun equalityIgnoresKeyOrder() {
        assertEquals(multimapOf("b" to 3, "a" to 1, "a" to 2), multimap)
    }

    @Test
    fun equalMultimapsHaveEqualHashCodes() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3).hashCode(), multimap.hashCode())
    }

    @Test
    fun multimapsWithDifferentValueOrderAreNotEqual() {
        assertNotEquals(multimapOf("a" to 2, "a" to 1, "b" to 3), multimap)
    }

    @Test
    fun listMultimapNeverEqualsSetMultimapWithSameContent() {
        assertNotEquals<Multimap<String, Int>>(setMultimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun toStringEqualsAsMapToString() {
        assertEquals(multimap.asMap().toString(), multimap.toString())
    }

    @Test
    fun delegatingListMultimapWrapsMapByReference() {
        val map = mutableMapOf("a" to listOf(1))
        val wrapped = DelegatingListMultimap(map)
        map["b"] = listOf(2)
        assertEquals(listOf(2), wrapped["b"])
    }
}
