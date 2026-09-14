package dev.ajthom.kollections.multimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MutableMultimapViewsTest {
    private fun listMultimap(): MutableListMultimap<String, Int> = mutableMultimapOf("a" to 1, "a" to 2, "b" to 3)

    private fun setMultimap(): MutableSetMultimap<String, Int> = mutableSetMultimapOf("a" to 1, "a" to 2, "b" to 3)

    // get(key) on a list multimap

    @Test
    fun addingThroughListViewOfAbsentKeyInsertsKey() {
        val multimap = listMultimap()
        multimap["x"].add(1)
        assertTrue(multimap.containsKey("x"))
    }

    @Test
    fun addingThroughListViewOfAbsentKeyStoresValue() {
        val multimap = listMultimap()
        multimap["x"].add(1)
        assertEquals(listOf(1), multimap["x"])
    }

    @Test
    fun listViewReflectsLaterPuts() {
        val multimap = listMultimap()
        val view = multimap["a"]
        multimap.put("a", 9)
        assertEquals(listOf(1, 2, 9), view)
    }

    @Test
    fun removingLastElementThroughListViewDropsKey() {
        val multimap = listMultimap()
        multimap["b"].remove(3)
        assertFalse(multimap.containsKey("b"))
    }

    @Test
    fun removingAtIndexThroughListViewDropsKeyWhenEmptied() {
        val multimap = listMultimap()
        multimap["b"].removeAt(0)
        assertFalse(multimap.containsKey("b"))
    }

    @Test
    fun removingThroughListViewIteratorDropsKeyWhenEmptied() {
        val multimap = listMultimap()
        val iterator = multimap["a"].iterator()
        while (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        }
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun listViewClearDropsKey() {
        val multimap = listMultimap()
        multimap["a"].clear()
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun listViewOfAbsentKeyIsEmpty() {
        assertEquals(0, listMultimap()["zzz"].size)
    }

    @Test
    fun listViewOfAbsentKeyDoesNotInsertKey() {
        val multimap = listMultimap()
        multimap["zzz"]
        assertFalse(multimap.containsKey("zzz"))
    }

    @Test
    fun listViewOfAbsentKeyBecomesLiveOnceKeyIsInserted() {
        val multimap = listMultimap()
        val view = multimap["x"]
        multimap.put("x", 7)
        assertEquals(listOf(7), view)
    }

    @Test
    fun listViewGetOnAbsentKeyThrowsIndexOutOfBounds() {
        val view = listMultimap()["zzz"]
        assertFailsWith<IndexOutOfBoundsException> { view[0] }
    }

    @Test
    fun listViewAddAtNonZeroIndexOnAbsentKeyThrows() {
        val view = listMultimap()["zzz"]
        assertFailsWith<IndexOutOfBoundsException> { view.add(1, 5) }
    }

    @Test
    fun listViewAddAtNonZeroIndexOnAbsentKeyDoesNotInsertKey() {
        val multimap = listMultimap()
        runCatching { multimap["zzz"].add(1, 5) }
        assertFalse(multimap.containsKey("zzz"))
    }

    @Test
    fun listViewAddAtIndexInsertsInPosition() {
        val multimap = listMultimap()
        multimap["a"].add(1, 5)
        assertEquals(listOf(1, 5, 2), multimap["a"])
    }

    @Test
    fun listViewSetReplacesElement() {
        val multimap = listMultimap()
        multimap["a"][0] = 5
        assertEquals(listOf(5, 2), multimap["a"])
    }

    @Test
    fun listViewAddAllOfEmptyOnAbsentKeyDoesNotInsertKey() {
        val multimap = listMultimap()
        multimap["zzz"].addAll(emptyList())
        assertFalse(multimap.containsKey("zzz"))
    }

    @Test
    fun listViewRemoveAllDropsKeyWhenEmptied() {
        val multimap = listMultimap()
        multimap["a"].removeAll(listOf(1, 2))
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun listViewRetainAllDropsKeyWhenEmptied() {
        val multimap = listMultimap()
        multimap["a"].retainAll(listOf(9))
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun listViewEqualsListWithSameElements() {
        assertEquals(listOf(1, 2), listMultimap()["a"])
    }

    // get(key) on a set multimap

    @Test
    fun addingThroughSetViewOfAbsentKeyInsertsKey() {
        val multimap = setMultimap()
        multimap["x"].add(1)
        assertTrue(multimap.containsKey("x"))
    }

    @Test
    fun setViewRejectsDuplicate() {
        val multimap = setMultimap()
        assertFalse(multimap["a"].add(1))
    }

    @Test
    fun setViewAcceptsNewValue() {
        val multimap = setMultimap()
        assertTrue(multimap["a"].add(9))
    }

    @Test
    fun removingLastElementThroughSetViewDropsKey() {
        val multimap = setMultimap()
        multimap["b"].remove(3)
        assertFalse(multimap.containsKey("b"))
    }

    @Test
    fun removingThroughSetViewIteratorDropsKeyWhenEmptied() {
        val multimap = setMultimap()
        val iterator = multimap["b"].iterator()
        iterator.next()
        iterator.remove()
        assertFalse(multimap.containsKey("b"))
    }

    @Test
    fun setViewClearDropsKey() {
        val multimap = setMultimap()
        multimap["a"].clear()
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun setViewOfAbsentKeyIsEmpty() {
        assertTrue(setMultimap()["zzz"].isEmpty())
    }

    @Test
    fun setViewContainsReflectsBacking() {
        assertTrue(setMultimap()["a"].contains(2))
    }

    @Test
    fun setViewReflectsLaterPuts() {
        val multimap = setMultimap()
        val view = multimap["a"]
        multimap.put("a", 9)
        assertEquals(setOf(1, 2, 9), view)
    }

    @Test
    fun setViewRemoveAllDropsKeyWhenEmptied() {
        val multimap = setMultimap()
        multimap["a"].removeAll(setOf(1, 2))
        assertFalse(multimap.containsKey("a"))
    }

    @Test
    fun setViewRetainAllDropsKeyWhenEmptied() {
        val multimap = setMultimap()
        multimap["a"].retainAll(setOf(9))
        assertFalse(multimap.containsKey("a"))
    }

    // keys

    @Test
    fun removingKeyThroughKeysDropsAllValues() {
        val multimap = listMultimap()
        multimap.keys.remove("a")
        assertEquals(mapOf("b" to listOf(3)), multimap.asMap())
    }

    @Test
    fun keysReflectLaterPuts() {
        val multimap = listMultimap()
        val keys = multimap.keys
        multimap.put("c", 4)
        assertEquals(setOf("a", "b", "c"), keys)
    }

    @Test
    fun keysDoNotSupportAdd() {
        val multimap = listMultimap()
        assertFailsWith<UnsupportedOperationException> { multimap.keys.add("z") }
    }

    // asMap

    @Test
    fun asMapValueIsLive() {
        val multimap = listMultimap()
        multimap.asMap()["a"]!!.add(3)
        assertEquals(listOf(1, 2, 3), multimap["a"])
    }

    @Test
    fun asMapValueDropsKeyWhenEmptied() {
        val multimap = listMultimap()
        multimap.asMap()["b"]!!.clear()
        assertFalse(multimap.containsKey("b"))
    }

    @Test
    fun asMapReturnsNullForAbsentKey() {
        assertNull(listMultimap().asMap()["zzz"])
    }

    @Test
    fun asMapReflectsLaterPuts() {
        val multimap = listMultimap()
        val map = multimap.asMap()
        multimap.put("c", 4)
        assertEquals(setOf("a", "b", "c"), map.keys)
    }

    @Test
    fun asMapEqualsPlainMapWithSameContent() {
        assertEquals<Map<String, List<Int>>>(mapOf("a" to listOf(1, 2), "b" to listOf(3)), listMultimap().asMap())
    }

    @Test
    fun asMapHashCodeMatchesPlainMap() {
        assertEquals(mapOf("a" to listOf(1, 2), "b" to listOf(3)).hashCode(), listMultimap().asMap().hashCode())
    }

    @Test
    fun asMapEntriesExposeLiveValues() {
        val multimap = listMultimap()
        multimap.asMap().entries.first { it.key == "a" }.value.add(3)
        assertEquals(listOf(1, 2, 3), multimap["a"])
    }

    @Test
    fun asMapValuesExposeLiveValues() {
        val multimap = listMultimap()
        multimap.asMap().values.first().add(3)
        assertEquals(listOf(1, 2, 3), multimap["a"])
    }

    @Test
    fun asMapSizeCountsKeys() {
        assertEquals(2, listMultimap().asMap().size)
    }

    @Test
    fun asMapContainsKeyForPresentKey() {
        assertTrue(listMultimap().asMap().containsKey("a"))
    }

    @Test
    fun asMapToStringMatchesPlainMap() {
        assertEquals(mapOf("a" to listOf(1, 2), "b" to listOf(3)).toString(), listMultimap().asMap().toString())
    }

    // snapshots

    @Test
    fun entriesAreSnapshot() {
        val multimap = listMultimap()
        val entries = multimap.entries.map { (key, values) -> key to values }
        multimap.put("a", 99)
        assertEquals(listOf("a" to listOf(1, 2), "b" to listOf(3)), entries)
    }

    @Test
    fun entriesSnapshotDoesNotSeeNewKeys() {
        val multimap = listMultimap()
        val entries = multimap.entries
        multimap.put("c", 4)
        assertEquals(2, entries.size)
    }

    @Test
    fun valuesAreSnapshot() {
        val multimap = listMultimap()
        val values = multimap.values
        multimap.put("a", 99)
        assertEquals(listOf(listOf(1, 2), listOf(3)), values.toList())
    }

    @Test
    fun flatValuesAreSnapshot() {
        val multimap = listMultimap()
        val flatValues = multimap.flatValues
        multimap.put("a", 99)
        assertEquals(listOf(1, 2, 3), flatValues.toList())
    }

    // equality against read-only multimaps

    @Test
    fun mutableListMultimapEqualsReadOnlyListMultimapWithSameContent() {
        assertEquals<Multimap<String, Int>>(multimapOf("a" to 1, "a" to 2, "b" to 3), listMultimap())
    }

    @Test
    fun readOnlyListMultimapEqualsMutableListMultimapWithSameContent() {
        assertEquals<Multimap<String, Int>>(listMultimap(), multimapOf("a" to 1, "a" to 2, "b" to 3))
    }

    @Test
    fun mutableSetMultimapEqualsReadOnlySetMultimapWithSameContent() {
        assertEquals<Multimap<String, Int>>(setMultimapOf("a" to 1, "a" to 2, "b" to 3), setMultimap())
    }

    @Test
    fun mutableListMultimapHashCodeMatchesReadOnly() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3).hashCode(), listMultimap().hashCode())
    }
}
