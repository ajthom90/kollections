package dev.ajthom.kollections.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapsTest {
    @Suppress("DEPRECATION")
    @Test
    fun orEmptyReturnsAnEmptyMapForNull() {
        val map: Map<String, Int>? = null
        assertEquals(emptyMap(), map.orEmpty())
    }

    @Suppress("DEPRECATION")
    @Test
    fun orEmptyReturnsTheReceiverWhenItIsNotNull() {
        val map: Map<String, Int>? = mapOf("a" to 1)
        assertEquals(mapOf("a" to 1), map.orEmpty())
    }

    @Test
    fun differenceComparesTwoMaps() {
        val difference = Maps.difference(mapOf("a" to 1), mapOf("a" to 2))
        assertEquals(mapOf("a" to MapDifference.ValueDifference(1, 2)), difference.entriesDiffering)
    }

    @Test
    fun differenceExtensionComparesTheReceiverWithTheArgument() {
        val difference = mapOf("a" to 1).difference(mapOf("a" to 2))
        assertEquals(mapOf("a" to MapDifference.ValueDifference(1, 2)), difference.entriesDiffering)
    }

    @Test
    fun differenceExtensionAgreesWithTheObjectFunction() {
        val left = mapOf("a" to 1, "b" to 2)
        val right = mapOf("b" to 3, "c" to 4)
        assertEquals(Maps.difference(left, right), left.difference(right))
    }

    @Test
    fun differenceOfEmptyMapsIsEqual() {
        assertTrue(Maps.difference(emptyMap<String, Int>(), emptyMap()).areEqual)
    }
}
