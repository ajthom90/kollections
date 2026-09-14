package dev.ajthom.kollections.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IteratorsTest {
    @Test
    fun getFirstReturnsTheFirstElement() {
        assertEquals(1, Iterators.getFirst(listOf(1, 2, 3).iterator()))
    }

    @Test
    fun getFirstThrowsWhenTheIteratorIsEmpty() {
        assertFailsWith<NoSuchElementException> { Iterators.getFirst(emptyList<Int>().iterator()) }
    }

    @Test
    fun getFirstConsumesOnlyTheFirstElement() {
        val iterator = listOf(1, 2, 3).iterator()
        Iterators.getFirst(iterator)
        assertEquals(2, iterator.next())
    }

    @Test
    fun getFirstWithDefaultReturnsTheFirstElement() {
        assertEquals(1, Iterators.getFirst(listOf(1, 2, 3).iterator(), 9))
    }

    @Test
    fun getFirstWithDefaultReturnsTheDefaultWhenEmpty() {
        assertEquals(9, Iterators.getFirst(emptyList<Int>().iterator(), 9))
    }

    @Test
    fun getFirstWithDefaultReturnsANullFirstElementRatherThanTheDefault() {
        assertNull(Iterators.getFirst(listOf(null, 2).iterator(), 9))
    }

    @Test
    fun getLastReturnsTheLastElement() {
        assertEquals(3, Iterators.getLast(listOf(1, 2, 3).iterator()))
    }

    @Test
    fun getLastThrowsWhenTheIteratorIsEmpty() {
        assertFailsWith<NoSuchElementException> { Iterators.getLast(emptyList<Int>().iterator()) }
    }

    @Test
    fun getLastWithDefaultReturnsTheLastElement() {
        assertEquals(3, Iterators.getLast(listOf(1, 2, 3).iterator(), 9))
    }

    @Test
    fun getLastWithDefaultReturnsTheDefaultWhenEmpty() {
        assertEquals(9, Iterators.getLast(emptyList<Int>().iterator(), 9))
    }

    @Test
    fun getLastWithDefaultReturnsANullLastElementRatherThanTheDefault() {
        assertNull(Iterators.getLast(listOf(1, null).iterator(), 9))
    }

    @Test
    fun getLastExhaustsTheIterator() {
        val iterator = listOf(1, 2, 3).iterator()
        Iterators.getLast(iterator)
        assertFalse(iterator.hasNext())
    }

    @Test
    fun onlyReturnsTheSingleElement() {
        assertEquals(1, Iterators.only(listOf(1).iterator()))
    }

    @Test
    fun onlyThrowsNoSuchElementExceptionWhenTheIteratorIsEmpty() {
        assertFailsWith<NoSuchElementException> { Iterators.only(emptyList<Int>().iterator()) }
    }

    @Test
    fun onlyThrowsIllegalArgumentExceptionWhenThereIsMoreThanOneElement() {
        assertFailsWith<IllegalArgumentException> { Iterators.only(listOf(1, 2).iterator()) }
    }

    @Test
    fun onlyReportsWhyItFailedWhenThereIsMoreThanOneElement() {
        val failure = assertFailsWith<IllegalArgumentException> { Iterators.only(listOf(1, 2).iterator()) }
        assertTrue(failure.message.orEmpty().contains("more than one"))
    }

    @Test
    fun onlyWithDefaultReturnsTheSingleElement() {
        assertEquals(1, Iterators.only(listOf(1).iterator(), 9))
    }

    @Test
    fun onlyWithDefaultReturnsTheDefaultWhenEmpty() {
        assertEquals(9, Iterators.only(emptyList<Int>().iterator(), 9))
    }

    @Test
    fun onlyWithDefaultThrowsIllegalArgumentExceptionWhenThereIsMoreThanOneElement() {
        assertFailsWith<IllegalArgumentException> { Iterators.only(listOf(1, 2).iterator(), 9) }
    }

    @Test
    fun onlyWithDefaultReturnsANullSingleElementRatherThanTheDefault() {
        assertNull(Iterators.only(listOf<Int?>(null).iterator(), 9))
    }
}
