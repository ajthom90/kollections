package dev.ajthom.kollections.set

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImmutableSetTest {
    @Test
    fun ofWithoutArgumentsIsEmpty() {
        assertTrue(ImmutableSet.of<Int>().isEmpty())
    }

    @Test
    fun ofASingleItemHoldsThatItem() {
        assertEquals(setOf(1), ImmutableSet.of(1))
    }

    @Test
    fun ofSeveralItemsHoldsThemAll() {
        assertEquals(setOf(1, 2, 3), ImmutableSet.of(1, 2, 3))
    }

    @Test
    fun ofDropsRepeatedItems() {
        assertEquals(setOf(1, 2), ImmutableSet.of(1, 2, 1))
    }

    @Test
    fun ofCountsRepeatedItemsOnce() {
        assertEquals(2, ImmutableSet.of(1, 2, 1).size)
    }

    @Test
    fun ofKeepsTheOrderTheItemsWereGivenIn() {
        assertEquals(listOf(3, 1, 2), ImmutableSet.of(3, 1, 2).toList())
    }

    @Test
    fun copyOfHoldsTheElementsOfTheSource() {
        assertEquals(setOf(1, 2), ImmutableSet.copyOf(listOf(1, 2)))
    }

    @Test
    fun copyOfDropsRepeatedElements() {
        assertEquals(setOf(1, 2), ImmutableSet.copyOf(listOf(1, 2, 2, 1)))
    }

    @Test
    fun copyOfDoesNotSeeLaterChangesToTheSource() {
        val source = mutableListOf(1)
        val copy = ImmutableSet.copyOf(source)
        source.add(2)
        assertEquals(setOf(1), copy)
    }

    @Test
    fun copyOfAnEmptyIterableIsEmpty() {
        assertTrue(ImmutableSet.copyOf(emptyList<Int>()).isEmpty())
    }
}
