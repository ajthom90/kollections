package dev.ajthom.kollections.bimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class HashBiMapTest {

    @Test
    fun newHashBiMapIsEmpty() {
        assertEquals(0, HashBiMap<String, Int>().size)
    }

    @Test
    fun newHashBiMapReportsIsEmpty() {
        assertTrue(HashBiMap<String, Int>().isEmpty())
    }

    @Test
    fun putStoresTheValueForTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap["one"])
    }

    @Test
    fun putReturnsNullForANewKey() {
        val biMap = HashBiMap<String, Int>()
        assertNull(biMap.put("one", 1))
    }

    @Test
    fun putIsReflectedInTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals("one", biMap.inverse()[1])
    }

    @Test
    fun putIncreasesTheSize() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.size)
    }

    @Test
    fun putSameKeyReturnsThePreviousValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.put("one", 2))
    }

    @Test
    fun putSameKeyRebindsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["one"] = 2
        assertEquals(2, biMap["one"])
    }

    @Test
    fun putSameKeyFreesTheOldValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["one"] = 2
        assertNull(biMap.inverse()[1])
    }

    @Test
    fun putSameKeyDoesNotGrowTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["one"] = 2
        assertEquals(1, biMap.inverse().size)
    }

    @Test
    fun putValueBoundToAnotherKeyThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertFailsWith<IllegalArgumentException> { biMap["uno"] = 1 }
    }

    @Test
    fun putValueBoundToAnotherKeyNamesTheValueInTheMessage() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val failure = assertFailsWith<IllegalArgumentException> { biMap["uno"] = 1 }
        assertTrue(failure.message.orEmpty().contains("1"))
    }

    @Test
    fun putValueBoundToAnotherKeyLeavesTheMapUnchanged() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        runCatching { biMap["uno"] = 1 }
        assertEquals(mapOf("one" to 1), biMap)
    }

    @Test
    fun putSameKeySameValueReturnsTheValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.put("one", 1))
    }

    @Test
    fun putSameKeySameValueLeavesTheMapUnchanged() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.put("one", 1)
        assertEquals(mapOf("one" to 1), biMap)
    }

    @Test
    fun forcePutEvictsTheKeyBoundToTheValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.forcePut("uno", 1)
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun forcePutBindsTheNewKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.forcePut("uno", 1)
        assertEquals(1, biMap["uno"])
    }

    @Test
    fun forcePutUpdatesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.forcePut("uno", 1)
        assertEquals("uno", biMap.inverse()[1])
    }

    @Test
    fun forcePutReturnsThePreviousValueForTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        assertEquals(2, biMap.forcePut("two", 1))
    }

    @Test
    fun forcePutKeepsTheSizeConsistent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        biMap.forcePut("two", 1)
        assertEquals(1, biMap.size)
    }

    @Test
    fun forcePutOnANewPairBehavesLikePut() {
        val biMap = HashBiMap<String, Int>()
        assertNull(biMap.forcePut("one", 1))
    }

    @Test
    fun removeReturnsThePreviousValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.remove("one"))
    }

    @Test
    fun removeDropsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.remove("one")
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun removeUpdatesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.remove("one")
        assertNull(biMap.inverse()[1])
    }

    @Test
    fun removeOfAnAbsentKeyReturnsNull() {
        val biMap = HashBiMap<String, Int>()
        assertNull(biMap.remove("one"))
    }

    @Test
    fun removeOfAnAbsentKeyLeavesTheInverseUnchanged() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.remove("two")
        assertEquals(1, biMap.inverse().size)
    }

    @Test
    fun clearEmptiesTheMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.clear()
        assertEquals(0, biMap.size)
    }

    @Test
    fun clearEmptiesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.clear()
        assertEquals(0, biMap.inverse().size)
    }

    @Test
    fun containsValueFindsAStoredValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertTrue(biMap.containsValue(1))
    }

    @Test
    fun containsValueRejectsAnAbsentValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertFalse(biMap.containsValue(2))
    }

    @Test
    fun inverseOfInverseIsTheSameInstance() {
        val biMap = HashBiMap<String, Int>()
        assertSame(biMap, biMap.inverse().inverse())
    }

    @Test
    fun inverseIsTheSameInstanceOnEveryCall() {
        val biMap = HashBiMap<String, Int>()
        assertSame(biMap.inverse(), biMap.inverse())
    }

    @Test
    fun putOnTheInverseIsVisibleInTheForwardMap() {
        val biMap = HashBiMap<String, Int>()
        biMap.inverse()[1] = "one"
        assertEquals(1, biMap["one"])
    }

    @Test
    fun removeOnTheInverseIsVisibleInTheForwardMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.inverse().remove(1)
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun clearOnTheInverseEmptiesTheForwardMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.inverse().clear()
        assertTrue(biMap.isEmpty())
    }

    @Test
    fun forcePutOnTheInverseEvictsTheForwardBinding() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.inverse().forcePut(2, "one")
        assertEquals(2, biMap["one"])
    }

    @Test
    fun putOnTheInverseWithABoundValueThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertFailsWith<IllegalArgumentException> { biMap.inverse()[2] = "one" }
    }

    @Test
    fun keysReflectsTheContent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        assertEquals(setOf("one", "two"), biMap.keys)
    }

    @Test
    fun keysIsALiveViewOfLaterPuts() {
        val biMap = HashBiMap<String, Int>()
        val keys = biMap.keys
        biMap["one"] = 1
        assertEquals(setOf("one"), keys)
    }

    @Test
    fun keysRemoveDropsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.keys.remove("one")
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun keysRemoveUpdatesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.keys.remove("one")
        assertFalse(biMap.inverse().containsKey(1))
    }

    @Test
    fun keysIteratorRemoveUpdatesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.keys.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(0, biMap.inverse().size)
    }

    @Test
    fun valuesReflectsTheContent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        assertEquals(setOf(1, 2), biMap.values)
    }

    @Test
    fun valuesIsTheInverseKeySet() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(biMap.inverse().keys, biMap.values)
    }

    @Test
    fun valuesIsALiveViewOfLaterPuts() {
        val biMap = HashBiMap<String, Int>()
        val values = biMap.values
        biMap["one"] = 1
        assertEquals(setOf(1), values)
    }

    @Test
    fun valuesRemoveDropsTheValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.values.remove(1)
        assertFalse(biMap.containsValue(1))
    }

    @Test
    fun valuesRemoveDropsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.values.remove(1)
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun valuesIteratorRemoveDropsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.values.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(0, biMap.size)
    }

    @Test
    fun entriesReflectsTheContent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.entries.size)
    }

    @Test
    fun entriesExposesTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals("one", biMap.entries.first().key)
    }

    @Test
    fun entriesExposesTheValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.entries.first().value)
    }

    @Test
    fun entriesIteratesInInsertionOrder() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        assertEquals(listOf("one", "two"), biMap.entries.map { it.key })
    }

    @Test
    fun entriesContainsAMatchingEntry() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertTrue(biMap.entries.contains(biMap.entries.first()))
    }

    @Test
    fun entriesIteratorRemoveDropsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.entries.iterator()
        iterator.next()
        iterator.remove()
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun entriesIteratorRemoveUpdatesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.entries.iterator()
        iterator.next()
        iterator.remove()
        assertFalse(biMap.inverse().containsKey(1))
    }

    @Test
    fun entriesIteratorRemoveBeforeNextThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertFailsWith<IllegalStateException> { biMap.entries.iterator().remove() }
    }

    @Test
    fun entriesRemoveDropsTheKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.entries.remove(biMap.entries.first())
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun entriesClearEmptiesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.entries.clear()
        assertEquals(0, biMap.inverse().size)
    }

    @Test
    fun entrySetValueReturnsThePreviousValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.entries.first().setValue(2))
    }

    @Test
    fun entrySetValueUpdatesTheForwardMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.entries.first().setValue(2)
        assertEquals(2, biMap["one"])
    }

    @Test
    fun entrySetValueUpdatesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.entries.first().setValue(2)
        assertEquals("one", biMap.inverse()[2])
    }

    @Test
    fun entrySetValueFreesTheOldValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.entries.first().setValue(2)
        assertFalse(biMap.inverse().containsKey(1))
    }

    @Test
    fun entrySetValueUpdatesTheEntryItself() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        entry.setValue(2)
        assertEquals(2, entry.value)
    }

    @Test
    fun entrySetValueToAValueBoundElsewhereThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        assertFailsWith<IllegalArgumentException> { biMap.entries.first().setValue(2) }
    }

    @Test
    fun entrySetValueToItsOwnValueIsANoOp() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(1, biMap.entries.first().setValue(1))
    }

    @Test
    fun entryValueReflectsAValueChangedThroughTheMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        assertEquals(2, entry.value)
    }

    @Test
    fun entryValueOnARemovedEntryThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap.remove("one")
        assertFailsWith<IllegalStateException> { entry.value }
    }

    @Test
    fun iteratorRemoveAfterARebindEmptiesTheForwardMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.entries.iterator()
        iterator.next()
        biMap["one"] = 2
        iterator.remove()
        assertEquals(0, biMap.size)
    }

    @Test
    fun iteratorRemoveAfterARebindDropsTheReboundValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.entries.iterator()
        iterator.next()
        biMap["one"] = 2
        iterator.remove()
        assertFalse(biMap.containsValue(2))
    }

    @Test
    fun iteratorRemoveAfterARebindEmptiesTheInverse() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.entries.iterator()
        iterator.next()
        biMap["one"] = 2
        iterator.remove()
        assertEquals(0, biMap.inverse().size)
    }

    @Test
    fun setValueAfterARebindReturnsTheCurrentValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        assertEquals(2, entry.setValue(3))
    }

    @Test
    fun setValueAfterARebindLeavesOneInverseEntry() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        entry.setValue(3)
        assertEquals(1, biMap.inverse().size)
    }

    @Test
    fun setValueAfterARebindFreesTheReboundValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        entry.setValue(3)
        assertFalse(biMap.containsValue(2))
    }

    @Test
    fun setValueAfterARebindBindsTheNewValue() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        entry.setValue(3)
        assertEquals("one", biMap.inverse()[3])
    }

    @Test
    fun setValueAfterARebindKeepsAnotherKeysBindingIntact() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        biMap["uno"] = 1
        entry.setValue(3)
        assertEquals("uno", biMap.inverse()[1])
    }

    @Test
    fun setValueAfterARebindKeepsTheInverseTheSameSizeAsTheMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        biMap["uno"] = 1
        entry.setValue(3)
        assertEquals(biMap.size, biMap.inverse().size)
    }

    @Test
    fun setValueAfterARebindLeavesTheOtherKeyBound() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap["one"] = 2
        biMap["uno"] = 1
        entry.setValue(3)
        assertTrue(biMap.containsValue(1))
    }

    @Test
    fun entrySetValueOnARemovedEntryThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val entry = biMap.entries.first()
        biMap.remove("one")
        assertFailsWith<IllegalStateException> { entry.setValue(2) }
    }

    @Test
    fun entryEqualsAMatchingMapEntry() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(mapOf("one" to 1).entries.first(), biMap.entries.first())
    }

    @Test
    fun entryHashCodeMatchesTheMapEntryContract() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals("one".hashCode() xor 1.hashCode(), biMap.entries.first().hashCode())
    }

    @Test
    fun entryToStringUsesTheStandardFormat() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals("one=1", biMap.entries.first().toString())
    }

    @Test
    fun modifyingTheEntriesViewIsVisibleThroughTheInverseEntries() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.entries.first().setValue(2)
        assertEquals(listOf(2), biMap.inverse().entries.map { it.key })
    }

    @Test
    fun inverseEntriesIteratorRemoveDropsTheForwardKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        val iterator = biMap.inverse().entries.iterator()
        iterator.next()
        iterator.remove()
        assertFalse(biMap.containsKey("one"))
    }

    @Test
    fun inverseEntrySetValueUpdatesTheForwardMap() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.inverse().entries.first().setValue("uno")
        assertEquals(1, biMap["uno"])
    }

    @Test
    fun inverseKeysRemoveDropsTheForwardKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.inverse().keys.remove(1)
        assertTrue(biMap.isEmpty())
    }

    @Test
    fun inverseValuesRemoveDropsTheForwardKey() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap.inverse().values.remove("one")
        assertTrue(biMap.isEmpty())
    }

    @Test
    fun equalsAPlainMapWithTheSameContent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        biMap["two"] = 2
        assertEquals(mapOf("one" to 1, "two" to 2), biMap)
    }

    @Test
    fun doesNotEqualAMapWithDifferentContent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertFalse(biMap == mapOf("one" to 2))
    }

    @Test
    fun hashCodeMatchesAPlainMapWithTheSameContent() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals(mapOf("one" to 1).hashCode(), biMap.hashCode())
    }

    @Test
    fun toStringUsesTheStandardMapFormat() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertEquals("{one=1}", biMap.toString())
    }

    @Test
    fun putAllStoresEveryPair() {
        val biMap = HashBiMap<String, Int>()
        biMap.putAll(mapOf("one" to 1, "two" to 2))
        assertEquals(mapOf("one" to 1, "two" to 2), biMap)
    }

    @Test
    fun putAllWithADuplicateValueThrows() {
        val biMap = HashBiMap<String, Int>()
        biMap["one"] = 1
        assertFailsWith<IllegalArgumentException> { biMap.putAll(mapOf("uno" to 1)) }
    }

    @Test
    fun supportsNullValues() {
        val biMap = HashBiMap<String, String?>()
        biMap["one"] = null
        assertEquals("one", biMap.inverse()[null])
    }

    @Test
    fun rebindingAwayFromANullValueFreesIt() {
        val biMap = HashBiMap<String, String?>()
        biMap["one"] = null
        biMap["one"] = "1"
        assertFalse(biMap.inverse().containsKey(null))
    }

    @Test
    fun rebindingAValueKeepsTheKeyWhereItIs() {
        val biMap = mutableBiMapOf("a" to 1, "b" to 2)
        biMap["a"] = 3
        assertEquals(listOf("a", "b"), biMap.keys.toList())
    }

    @Test
    fun rebindingAValueMovesTheNewValueToTheEndOfTheInverseOrder() {
        val biMap = mutableBiMapOf("a" to 1, "b" to 2)
        biMap["a"] = 3
        assertEquals(listOf(2, 3), biMap.inverse().keys.toList())
    }
}
