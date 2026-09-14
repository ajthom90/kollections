package dev.ajthom.kollections.bimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiMapsTest {

    @Test
    fun emptyBiMapHasNoEntries() {
        assertTrue(emptyBiMap<String, Int>().isEmpty())
    }

    @Test
    fun emptyBiMapEqualsTheEmptyMap() {
        assertEquals(emptyMap<String, Int>(), emptyBiMap<String, Int>())
    }

    @Test
    fun emptyBiMapHasAnEmptyInverse() {
        assertTrue(emptyBiMap<String, Int>().inverse().isEmpty())
    }

    @Test
    fun biMapOfHoldsThePairs() {
        assertEquals(mapOf("one" to 1, "two" to 2), biMapOf("one" to 1, "two" to 2))
    }

    @Test
    fun biMapOfBuildsTheInverse() {
        assertEquals("two", biMapOf("one" to 1, "two" to 2).inverse()[2])
    }

    @Test
    fun biMapOfWithNoPairsIsEmpty() {
        assertTrue(biMapOf<String, Int>().isEmpty())
    }

    @Test
    fun biMapOfWithADuplicateValueThrows() {
        assertFailsWith<IllegalArgumentException> { biMapOf("one" to 1, "uno" to 1) }
    }

    @Test
    fun biMapOfWithADuplicateKeyKeepsTheLastValue() {
        assertEquals(2, biMapOf("one" to 1, "one" to 2)["one"])
    }

    @Test
    fun biMapOfWithADuplicateKeyFreesTheEarlierValue() {
        assertFalse(biMapOf("one" to 1, "one" to 2).containsValue(1))
    }

    @Test
    fun biMapOfIteratesInInsertionOrder() {
        assertEquals(listOf("one", "two"), biMapOf("one" to 1, "two" to 2).keys.toList())
    }

    @Test
    fun mutableBiMapOfHoldsThePairs() {
        assertEquals(mapOf("one" to 1), mutableBiMapOf("one" to 1))
    }

    @Test
    fun mutableBiMapOfIsMutable() {
        val biMap = mutableBiMapOf("one" to 1)
        biMap["two"] = 2
        assertEquals(2, biMap["two"])
    }

    @Test
    fun mutableBiMapOfWithNoPairsIsEmpty() {
        assertTrue(mutableBiMapOf<String, Int>().isEmpty())
    }

    @Test
    fun mutableBiMapOfWithADuplicateValueThrows() {
        assertFailsWith<IllegalArgumentException> { mutableBiMapOf("one" to 1, "uno" to 1) }
    }

    @Test
    fun toBiMapCopiesTheContent() {
        assertEquals(mapOf("one" to 1), mapOf("one" to 1).toBiMap())
    }

    @Test
    fun toBiMapBuildsTheInverse() {
        assertEquals("one", mapOf("one" to 1).toBiMap().inverse()[1])
    }

    @Test
    fun toBiMapWithADuplicateValueThrows() {
        assertFailsWith<IllegalArgumentException> { mapOf("one" to 1, "uno" to 1).toBiMap() }
    }

    @Test
    fun toMutableBiMapCopiesTheContent() {
        assertEquals(mapOf("one" to 1), mapOf("one" to 1).toMutableBiMap())
    }

    @Test
    fun toMutableBiMapIsADetachedCopy() {
        val source = mutableMapOf("one" to 1)
        val copy = source.toMutableBiMap()
        source["two"] = 2
        assertFalse(copy.containsKey("two"))
    }

    @Test
    fun toMutableBiMapDoesNotWriteThroughToTheSource() {
        val source = mutableMapOf("one" to 1)
        val copy = source.toMutableBiMap()
        copy["two"] = 2
        assertFalse(source.containsKey("two"))
    }

    @Test
    fun toMutableBiMapWithADuplicateValueThrows() {
        assertFailsWith<IllegalArgumentException> { mapOf("one" to 1, "uno" to 1).toMutableBiMap() }
    }
}
