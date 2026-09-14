package dev.ajthom.kollections.multimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MutableMultimapTest {
    private fun listMultimap(): MutableListMultimap<String, Int> = mutableMultimapOf("a" to 1, "a" to 2, "b" to 3)

    private fun setMultimap(): MutableSetMultimap<String, Int> = mutableSetMultimapOf("a" to 1, "a" to 2, "b" to 3)

    @Test
    fun putOnListMultimapReturnsTrue() {
        assertTrue(listMultimap().put("a", 1))
    }

    @Test
    fun putOnListMultimapAppendsDuplicate() {
        val multimap = listMultimap()
        multimap.put("a", 1)
        assertEquals(listOf(1, 2, 1), multimap["a"])
    }

    @Test
    fun putOnSetMultimapReturnsFalseForDuplicate() {
        assertFalse(setMultimap().put("a", 1))
    }

    @Test
    fun putOnSetMultimapReturnsTrueForNewValue() {
        assertTrue(setMultimap().put("a", 9))
    }

    @Test
    fun putIncrementsSize() {
        val multimap = listMultimap()
        multimap.put("c", 4)
        assertEquals(4, multimap.size)
    }

    @Test
    fun putAllKeyIterableAppendsValues() {
        val multimap = listMultimap()
        multimap.putAll("a", listOf(5, 6))
        assertEquals(listOf(1, 2, 5, 6), multimap["a"])
    }

    @Test
    fun putAllKeyIterableReturnsTrueWhenChanged() {
        assertTrue(listMultimap().putAll("a", listOf(5)))
    }

    @Test
    fun putAllKeyEmptyIterableReturnsFalse() {
        assertFalse(listMultimap().putAll("zzz", emptyList()))
    }

    @Test
    fun putAllKeyEmptyIterableDoesNotInsertKey() {
        val multimap = listMultimap()
        multimap.putAll("zzz", emptyList())
        assertFalse(multimap.containsKey("zzz"))
    }

    @Test
    fun putAllKeyIterableOnSetMultimapReturnsFalseWhenAllDuplicates() {
        assertFalse(setMultimap().putAll("a", listOf(1, 2)))
    }

    @Test
    fun putAllMultimapAddsEveryPair() {
        val multimap = listMultimap()
        multimap.putAll(multimapOf("a" to 9, "c" to 4))
        assertEquals(multimapOf("a" to 1, "a" to 2, "a" to 9, "b" to 3, "c" to 4), multimap)
    }

    @Test
    fun putAllMultimapReturnsTrueWhenChanged() {
        assertTrue(listMultimap().putAll(multimapOf("c" to 4)))
    }

    @Test
    fun putAllEmptyMultimapReturnsFalse() {
        assertFalse(listMultimap().putAll(emptyMultimap()))
    }

    @Test
    fun putAllMultimapAcceptsSubtypeKeysAndValues() {
        val multimap = mutableMultimapOf<Any, Any>()
        multimap.putAll(multimapOf("a" to 1))
        assertEquals(listOf<Any>(1), multimap["a"])
    }

    @Test
    fun putAllMapAddsEveryValue() {
        val multimap = listMultimap()
        multimap.putAll(mapOf("b" to listOf(7), "c" to setOf(4)))
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3, "b" to 7, "c" to 4), multimap)
    }

    @Test
    fun putAllMapReturnsTrueWhenChanged() {
        assertTrue(listMultimap().putAll(mapOf("c" to listOf(4))))
    }

    @Test
    fun putAllMapSkipsEmptyValueCollections() {
        val multimap = listMultimap()
        multimap.putAll(mapOf("zzz" to emptyList()))
        assertFalse(multimap.containsKey("zzz"))
    }

    @Test
    fun removeKeyValueReturnsTrueWhenPresent() {
        assertTrue(listMultimap().remove("a", 1))
    }

    @Test
    fun removeKeyValueReturnsFalseWhenAbsent() {
        assertFalse(listMultimap().remove("a", 99))
    }

    @Test
    fun removeKeyValueReturnsFalseForAbsentKey() {
        assertFalse(listMultimap().remove("zzz", 1))
    }

    @Test
    fun removeKeyValueRemovesOnlyOneOccurrence() {
        val multimap = mutableMultimapOf("a" to 1, "a" to 1)
        multimap.remove("a", 1)
        assertEquals(listOf(1), multimap["a"])
    }

    @Test
    fun removeKeyValueDropsKeyWhenLastValueRemoved() {
        val multimap = listMultimap()
        multimap.remove("b", 3)
        assertFalse(multimap.containsKey("b"))
    }

    @Test
    fun removeKeyReturnsRemovedValues() {
        assertEquals(listOf(1, 2), listMultimap().remove("a"))
    }

    @Test
    fun removeKeyDropsKey() {
        val multimap = listMultimap()
        multimap.remove("a")
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun removeKeyReturnsEmptyForAbsentKey() {
        assertEquals(emptyList(), listMultimap().remove("zzz"))
    }

    @Test
    fun removeKeyResultIsNotAView() {
        val multimap = listMultimap()
        val removed = multimap.remove("a")
        multimap.put("a", 9)
        assertEquals(listOf(1, 2), removed)
    }

    @Test
    fun removeKeyOnSetMultimapReturnsSet() {
        assertEquals(setOf(1, 2), setMultimap().remove("a"))
    }

    @Test
    fun replaceValuesReturnsOldValues() {
        assertEquals(listOf(1, 2), listMultimap().replaceValues("a", listOf(8)))
    }

    @Test
    fun replaceValuesInstallsNewValues() {
        val multimap = listMultimap()
        multimap.replaceValues("a", listOf(8, 9))
        assertEquals(listOf(8, 9), multimap["a"])
    }

    @Test
    fun replaceValuesKeepsKeyPosition() {
        val multimap = listMultimap()
        multimap.replaceValues("a", listOf(8))
        assertEquals(listOf("a", "b"), multimap.keys.toList())
    }

    @Test
    fun replaceValuesWithEmptyIterableRemovesKey() {
        val multimap = listMultimap()
        multimap.replaceValues("a", emptyList())
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun replaceValuesOnAbsentKeyReturnsEmpty() {
        assertEquals(emptyList(), listMultimap().replaceValues("zzz", listOf(1)))
    }

    @Test
    fun replaceValuesOnAbsentKeyInsertsKey() {
        val multimap = listMultimap()
        multimap.replaceValues("zzz", listOf(1))
        assertEquals(listOf(1), multimap["zzz"])
    }

    @Test
    fun replaceValuesResultIsNotAView() {
        val multimap = listMultimap()
        val old = multimap.replaceValues("a", listOf(8))
        multimap.put("a", 9)
        assertEquals(listOf(1, 2), old)
    }

    @Test
    fun clearRemovesEverything() {
        val multimap = listMultimap()
        multimap.clear()
        assertTrue(multimap.isEmpty())
    }

    @Test
    fun plusAssignPairAddsPair() {
        val multimap = listMultimap()
        multimap += "c" to 4
        assertEquals(listOf(4), multimap["c"])
    }

    @Test
    fun plusAssignPairsAddsEveryPair() {
        val multimap = listMultimap()
        multimap += listOf("c" to 4, "c" to 5)
        assertEquals(listOf(4, 5), multimap["c"])
    }

    @Test
    fun plusAssignMultimapAddsEveryPair() {
        val multimap = listMultimap()
        multimap += multimapOf("c" to 4, "a" to 9)
        assertEquals(multimapOf("a" to 1, "a" to 2, "a" to 9, "b" to 3, "c" to 4), multimap)
    }

    @Test
    fun minusAssignKeyRemovesKey() {
        val multimap = listMultimap()
        multimap -= "a"
        assertEquals(multimapOf("b" to 3), multimap)
    }

    @Test
    fun minusAssignKeysRemovesEveryKey() {
        val multimap = listMultimap()
        multimap -= listOf("a", "b")
        assertTrue(multimap.isEmpty())
    }

    @Test
    fun minusAssignPairRemovesSinglePair() {
        val multimap = listMultimap()
        multimap -= "a" to 1
        assertEquals(multimapOf("a" to 2, "b" to 3), multimap)
    }

    // self-aliasing through live views: inputs are materialised before the multimap is mutated

    @Test
    fun putAllKeyWithOwnViewDoublesValues() {
        val multimap = listMultimap()
        multimap.putAll("a", multimap["a"])
        assertEquals(listOf(1, 2, 1, 2), multimap["a"])
    }

    @Test
    fun putAllKeyWithOwnViewReturnsTrue() {
        val multimap = listMultimap()
        assertTrue(multimap.putAll("a", multimap["a"]))
    }

    @Test
    fun putAllSelfDoublesEveryKeysValues() {
        val multimap = listMultimap()
        multimap.putAll(multimap)
        assertEquals(multimapOf("a" to 1, "a" to 2, "a" to 1, "a" to 2, "b" to 3, "b" to 3), multimap)
    }

    @Test
    fun plusAssignSelfDoublesEveryKeysValues() {
        val multimap = listMultimap()
        multimap += multimap
        assertEquals(multimapOf("a" to 1, "a" to 2, "a" to 1, "a" to 2, "b" to 3, "b" to 3), multimap)
    }

    @Test
    fun putAllOwnAsMapDoublesEveryKeysValues() {
        val multimap = listMultimap()
        multimap.putAll(multimap.asMap())
        assertEquals(multimapOf("a" to 1, "a" to 2, "a" to 1, "a" to 2, "b" to 3, "b" to 3), multimap)
    }

    @Test
    fun plusAssignPairsIteratedFromSelfDoublesEveryKeysValues() {
        val multimap = listMultimap()
        multimap += multimap.iterator().asSequence().asIterable()
        assertEquals(multimapOf("a" to 1, "a" to 2, "a" to 1, "a" to 2, "b" to 3, "b" to 3), multimap)
    }

    @Test
    fun listViewAddAllOfItselfDoublesValues() {
        val multimap = listMultimap()
        multimap["a"].addAll(multimap["a"])
        assertEquals(listOf(1, 2, 1, 2), multimap["a"])
    }

    @Test
    fun listViewAddAllOfItselfReturnsTrue() {
        val multimap = listMultimap()
        assertTrue(multimap["a"].addAll(multimap["a"]))
    }

    @Test
    fun listViewAddAllAtIndexOfItselfDoublesValues() {
        val multimap = listMultimap()
        multimap["a"].addAll(0, multimap["a"])
        assertEquals(listOf(1, 2, 1, 2), multimap["a"])
    }

    @Test
    fun replaceValuesWithOwnViewKeepsValues() {
        val multimap = listMultimap()
        multimap.replaceValues("a", multimap["a"])
        assertEquals(listOf(1, 2), multimap["a"])
    }

    @Test
    fun minusAssignOwnKeysEmptiesMultimap() {
        val multimap = listMultimap()
        multimap -= multimap.keys
        assertTrue(multimap.isEmpty())
    }

    @Test
    fun putAllKeyWithOwnViewOnSetMultimapReturnsFalse() {
        val multimap = setMultimap()
        assertFalse(multimap.putAll("a", multimap["a"]))
    }

    @Test
    fun putAllKeyWithOwnViewOnSetMultimapLeavesValuesUnchanged() {
        val multimap = setMultimap()
        multimap.putAll("a", multimap["a"])
        assertEquals(setOf(1, 2), multimap["a"])
    }

    @Test
    fun putAllSelfOnSetMultimapReturnsFalse() {
        val multimap = setMultimap()
        assertFalse(multimap.putAll(multimap))
    }

    @Test
    fun plusAssignSelfOnSetMultimapLeavesContentUnchanged() {
        val multimap = setMultimap()
        multimap += multimap
        assertEquals(setMultimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun setViewAddAllOfItselfReturnsFalse() {
        val multimap = setMultimap()
        assertFalse(multimap["a"].addAll(multimap["a"]))
    }

    @Test
    fun setViewAddAllOfItselfLeavesValuesUnchanged() {
        val multimap = setMultimap()
        multimap["a"].addAll(multimap["a"])
        assertEquals(setOf(1, 2), multimap["a"])
    }

    @Test
    fun linkedHashListMultimapKeepsInsertionOrder() {
        val multimap = LinkedHashListMultimap<String, Int>()
        multimap.put("b", 1)
        multimap.put("a", 2)
        assertEquals(listOf("b", "a"), multimap.keys.toList())
    }

    @Test
    fun linkedHashSetMultimapKeepsInsertionOrder() {
        val multimap = LinkedHashSetMultimap<String, Int>()
        multimap.put("b", 2)
        multimap.put("b", 1)
        assertEquals(listOf(2, 1), multimap["b"].toList())
    }

    @Test
    fun delegatingMutableListMultimapWritesThroughToBackingMap() {
        val backing = mutableMapOf<String, MutableList<Int>>()
        DelegatingMutableListMultimap(backing).put("a", 1)
        assertEquals(mapOf("a" to listOf(1)), backing)
    }

    @Test
    fun delegatingMutableListMultimapUsesListFactory() {
        val backing = mutableMapOf<String, MutableList<Int>>()
        DelegatingMutableListMultimap(backing) { ArrayDeque() }.put("a", 1)
        assertTrue(backing["a"] is ArrayDeque<Int>)
    }

    @Test
    fun delegatingMutableSetMultimapWritesThroughToBackingMap() {
        val backing = mutableMapOf<String, MutableSet<Int>>()
        DelegatingMutableSetMultimap(backing).put("a", 1)
        assertEquals(mapOf("a" to setOf(1)), backing)
    }

    @Test
    fun delegatingMutableSetMultimapUsesSetFactory() {
        val backing = mutableMapOf<String, MutableSet<Int>>()
        DelegatingMutableSetMultimap(backing) { HashSet() }.put("a", 1)
        assertTrue(backing["a"] is HashSet<Int>)
    }
}
