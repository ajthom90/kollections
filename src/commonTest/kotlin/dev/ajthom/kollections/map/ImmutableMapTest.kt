package dev.ajthom.kollections.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImmutableMapTest {
    @Test
    fun ofWithoutArgumentsIsEmpty() {
        assertTrue(ImmutableMap.of<String, Int>().isEmpty())
    }

    @Test
    fun ofOnePair() {
        assertEquals(mapOf("a" to 1), ImmutableMap.of("a", 1))
    }

    @Test
    fun ofTwoPairs() {
        assertEquals(mapOf("a" to 1, "b" to 2), ImmutableMap.of("a", 1, "b", 2))
    }

    @Test
    fun ofThreePairs() {
        assertEquals(mapOf("a" to 1, "b" to 2, "c" to 3), ImmutableMap.of("a", 1, "b", 2, "c", 3))
    }

    @Test
    fun ofFourPairs() {
        assertEquals(
            mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4),
        )
    }

    @Test
    fun ofFivePairs() {
        assertEquals(
            mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5),
        )
    }

    @Test
    fun ofSixPairs() {
        assertEquals(
            mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5, "f" to 6),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6),
        )
    }

    @Test
    fun ofSevenPairs() {
        assertEquals(
            mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5, "f" to 6, "g" to 7),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7),
        )
    }

    @Test
    fun ofEightPairs() {
        assertEquals(
            mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5, "f" to 6, "g" to 7, "h" to 8),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8),
        )
    }

    @Test
    fun ofNinePairs() {
        assertEquals(
            mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5, "f" to 6, "g" to 7, "h" to 8, "i" to 9),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8, "i", 9),
        )
    }

    @Test
    fun ofTenPairs() {
        assertEquals(
            mapOf(
                "a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5,
                "f" to 6, "g" to 7, "h" to 8, "i" to 9, "j" to 10,
            ),
            ImmutableMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5, "f", 6, "g", 7, "h", 8, "i", 9, "j", 10),
        )
    }

    @Test
    fun ofKeepsInsertionOrder() {
        assertEquals(listOf("b", "a"), ImmutableMap.of("b", 1, "a", 2).keys.toList())
    }

    @Test
    fun copyOfHoldsTheSameEntries() {
        assertEquals(mapOf("a" to 1, "b" to 2), ImmutableMap.copyOf(mapOf("a" to 1, "b" to 2)))
    }

    @Test
    fun copyOfDoesNotSeeLaterChangesToTheSource() {
        val source = mutableMapOf("a" to 1)
        val copy = ImmutableMap.copyOf(source)
        source["b"] = 2
        assertEquals(mapOf("a" to 1), copy)
    }

    @Test
    fun copyOfAnEmptyMapIsEmpty() {
        assertTrue(ImmutableMap.copyOf(emptyMap<String, Int>()).isEmpty())
    }
}
