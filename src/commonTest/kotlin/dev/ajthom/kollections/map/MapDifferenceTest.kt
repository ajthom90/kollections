package dev.ajthom.kollections.map

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MapDifferenceTest {
    private val left: Map<String, Int> = mapOf("common" to 1, "onlyLeft" to 2, "differing" to 3)
    private val right: Map<String, Int> = mapOf("common" to 1, "onlyRight" to 4, "differing" to 5)
    private val difference: MapDifference<String, Int> = Maps.difference(left, right)

    @Test
    fun equalMapsAreReportedAsEqual() {
        assertTrue(Maps.difference(mapOf("a" to 1), mapOf("a" to 1)).areEqual)
    }

    @Test
    fun equalMapsHaveNoEntriesOnlyOnLeft() {
        assertEquals(emptyMap(), Maps.difference(mapOf("a" to 1), mapOf("a" to 1)).entriesOnlyOnLeft)
    }

    @Test
    fun equalMapsHaveNoEntriesOnlyOnRight() {
        assertEquals(emptyMap(), Maps.difference(mapOf("a" to 1), mapOf("a" to 1)).entriesOnlyOnRight)
    }

    @Test
    fun equalMapsHaveNoDifferingEntries() {
        assertEquals(emptyMap(), Maps.difference(mapOf("a" to 1), mapOf("a" to 1)).entriesDiffering)
    }

    @Test
    fun equalMapsHaveEveryEntryInCommon() {
        assertEquals(mapOf("a" to 1), Maps.difference(mapOf("a" to 1), mapOf("a" to 1)).entriesInCommon)
    }

    @Test
    fun differingMapsAreNotReportedAsEqual() {
        assertFalse(difference.areEqual)
    }

    @Test
    fun entriesOnlyOnLeftHoldsKeysMissingFromTheRightMap() {
        assertEquals(mapOf("onlyLeft" to 2), difference.entriesOnlyOnLeft)
    }

    @Test
    fun entriesOnlyOnRightHoldsKeysMissingFromTheLeftMap() {
        assertEquals(mapOf("onlyRight" to 4), difference.entriesOnlyOnRight)
    }

    @Test
    fun entriesInCommonHoldsKeysWithEqualValues() {
        assertEquals(mapOf("common" to 1), difference.entriesInCommon)
    }

    @Test
    fun entriesDifferingHoldsKeysWithUnequalValues() {
        assertEquals(mapOf("differing" to MapDifference.ValueDifference(3, 5)), difference.entriesDiffering)
    }

    @Test
    fun valueDifferenceExposesTheLeftValue() {
        assertEquals(3, difference.entriesDiffering.getValue("differing").leftValue)
    }

    @Test
    fun valueDifferenceExposesTheRightValue() {
        assertEquals(5, difference.entriesDiffering.getValue("differing").rightValue)
    }

    @Test
    fun aKeyOnlyOnTheRightIsNotReportedAsDiffering() {
        assertFalse(difference.entriesDiffering.containsKey("onlyRight"))
    }

    @Test
    fun nullValuesOnBothSidesCountAsCommon() {
        val withNulls = Maps.difference(mapOf("a" to null), mapOf("a" to null))
        assertEquals(mapOf("a" to null), withNulls.entriesInCommon)
    }

    @Test
    fun aNullValueOnOneSideOnlyIsReportedAsDiffering() {
        val withNulls = Maps.difference(mapOf("a" to null), mapOf("a" to 1))
        assertEquals(mapOf("a" to MapDifference.ValueDifference(null, 1)), withNulls.entriesDiffering)
    }

    @Test
    fun twoDifferencesOverTheSameMapsAreEqual() {
        assertEquals(Maps.difference(left, right), Maps.difference(left, right))
    }

    @Test
    fun twoDifferencesOverTheSameMapsShareAHashCode() {
        assertEquals(Maps.difference(left, right).hashCode(), Maps.difference(left, right).hashCode())
    }

    @Test
    fun differencesOverDifferentMapsAreNotEqual() {
        assertNotEquals(difference, Maps.difference(left, left))
    }

    @Test
    fun aDifferenceIsNotEqualToAnUnrelatedObject() {
        assertNotEquals<Any?>(difference, "difference")
    }

    @Test
    fun theToStringOfAnEqualDifferenceSaysSo() {
        assertEquals("equal", Maps.difference(mapOf("a" to 1), mapOf("a" to 1)).toString())
    }

    @Test
    fun theToStringOfAnUnequalDifferenceNamesTheDifferingKey() {
        assertTrue(difference.toString().contains("differing"))
    }

    @Test
    fun valueDifferencesWithTheSameValuesAreEqual() {
        assertEquals(MapDifference.ValueDifference(1, 2), MapDifference.ValueDifference(1, 2))
    }
}
