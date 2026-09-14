package dev.ajthom.kollections.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImmutableListTest {
    @Test
    fun ofWithoutArgumentsIsEmpty() {
        assertTrue(ImmutableList.of<Int>().isEmpty())
    }

    @Test
    fun ofASingleItemHoldsThatItem() {
        assertEquals(listOf(1), ImmutableList.of(1))
    }

    @Test
    fun ofSeveralItemsKeepsTheirOrder() {
        assertEquals(listOf(3, 1, 2), ImmutableList.of(3, 1, 2))
    }

    @Test
    fun ofKeepsRepeatedItems() {
        assertEquals(listOf(1, 1), ImmutableList.of(1, 1))
    }

    @Test
    fun copyOfHoldsTheElementsOfTheSource() {
        assertEquals(listOf(1, 2), ImmutableList.copyOf(setOf(1, 2)))
    }

    @Test
    fun copyOfKeepsRepeatedElements() {
        assertEquals(listOf(1, 1, 2), ImmutableList.copyOf(listOf(1, 1, 2)))
    }

    @Test
    fun copyOfDoesNotSeeLaterChangesToTheSource() {
        val source = mutableListOf(1)
        val copy = ImmutableList.copyOf(source)
        source.add(2)
        assertEquals(listOf(1), copy)
    }

    @Test
    fun copyOfAnEmptyIterableIsEmpty() {
        assertTrue(ImmutableList.copyOf(emptyList<Int>()).isEmpty())
    }
}
