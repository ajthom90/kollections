package dev.ajthom.kollections.iterable

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IterablesTest {
    /** An [Iterable] that records how many times an iterator was requested from it. */
    private class CountingIterable<T>(private val items: List<T>) : Iterable<T> {
        var iteratorCount: Int = 0
            private set

        override fun iterator(): Iterator<T> {
            iteratorCount++
            return items.iterator()
        }
    }

    /** A [List] that records how many times an iterator was requested from it. */
    private class CountingList<T>(private val items: List<T>) : List<T> by items {
        var iteratorCount: Int = 0
            private set

        override fun iterator(): Iterator<T> {
            iteratorCount++
            return items.iterator()
        }
    }

    @Test
    fun getFirstReturnsTheFirstElement() {
        assertEquals(1, Iterables.getFirst(listOf(1, 2, 3)))
    }

    @Test
    fun getFirstThrowsWhenTheIterableIsEmpty() {
        assertFailsWith<NoSuchElementException> { Iterables.getFirst(emptyList<Int>()) }
    }

    @Test
    fun getFirstWithDefaultReturnsTheFirstElement() {
        assertEquals(1, Iterables.getFirst(listOf(1, 2, 3), 9))
    }

    @Test
    fun getFirstWithDefaultReturnsTheDefaultWhenEmpty() {
        assertEquals(9, Iterables.getFirst(emptyList(), 9))
    }

    @Test
    fun getFirstWithDefaultReturnsANullFirstElementRatherThanTheDefault() {
        assertNull(Iterables.getFirst(listOf(null, 2), 9))
    }

    @Test
    fun getFirstWithDefaultRequestsExactlyOneIterator() {
        val iterable = CountingIterable(listOf(1, 2))
        Iterables.getFirst(iterable, 9)
        assertEquals(1, iterable.iteratorCount)
    }

    @Test
    fun getLastReturnsTheLastElement() {
        assertEquals(3, Iterables.getLast(listOf(1, 2, 3)))
    }

    @Test
    fun getLastThrowsWhenTheIterableIsEmpty() {
        assertFailsWith<NoSuchElementException> { Iterables.getLast(emptyList<Int>()) }
    }

    @Test
    fun getLastWithDefaultReturnsTheLastElement() {
        assertEquals(3, Iterables.getLast(listOf(1, 2, 3), 9))
    }

    @Test
    fun getLastWithDefaultReturnsTheDefaultWhenEmpty() {
        assertEquals(9, Iterables.getLast(emptyList(), 9))
    }

    @Test
    fun getLastWithDefaultReturnsANullLastElementRatherThanTheDefault() {
        assertNull(Iterables.getLast(listOf(1, null), 9))
    }

    @Test
    fun getLastWithDefaultRequestsExactlyOneIterator() {
        val iterable = CountingIterable(listOf(1, 2))
        Iterables.getLast(iterable, 9)
        assertEquals(1, iterable.iteratorCount)
    }

    @Test
    fun onlyReturnsTheSingleElement() {
        assertEquals(1, Iterables.only(listOf(1)))
    }

    @Test
    fun onlyThrowsNoSuchElementExceptionWhenTheIterableIsEmpty() {
        assertFailsWith<NoSuchElementException> { Iterables.only(emptyList<Int>()) }
    }

    @Test
    fun onlyThrowsIllegalArgumentExceptionWhenThereIsMoreThanOneElement() {
        assertFailsWith<IllegalArgumentException> { Iterables.only(listOf(1, 2)) }
    }

    @Test
    fun onlyReportsWhyItFailedWhenThereIsMoreThanOneElement() {
        val failure = assertFailsWith<IllegalArgumentException> { Iterables.only(listOf(1, 2)) }
        assertTrue(failure.message.orEmpty().contains("more than one"))
    }

    @Test
    fun onlyWithDefaultReturnsTheSingleElement() {
        assertEquals(1, Iterables.only(listOf(1), 9))
    }

    @Test
    fun onlyWithDefaultReturnsTheDefaultWhenEmpty() {
        assertEquals(9, Iterables.only(emptyList(), 9))
    }

    @Test
    fun onlyWithDefaultThrowsIllegalArgumentExceptionWhenThereIsMoreThanOneElement() {
        assertFailsWith<IllegalArgumentException> { Iterables.only(listOf(1, 2), 9) }
    }

    @Test
    fun onlyWithDefaultReturnsANullSingleElementRatherThanTheDefault() {
        assertNull(Iterables.only(listOf(null), 9))
    }

    @Test
    fun onlyWithDefaultRequestsExactlyOneIterator() {
        val iterable = CountingIterable(listOf(1))
        Iterables.only(iterable, 9)
        assertEquals(1, iterable.iteratorCount)
    }

    @Test
    fun onlyWithDefaultWorksOnASinglePassIterable() {
        val iterable = sequenceOf(1).constrainOnce().asIterable()
        assertEquals(1, Iterables.only(iterable, 9))
    }

    @Test
    fun onlyWorksOnASinglePassIterable() {
        val iterable = sequenceOf(1).constrainOnce().asIterable()
        assertEquals(1, Iterables.only(iterable))
    }

    @Test
    fun getFirstWorksOnASinglePassIterable() {
        val iterable = sequenceOf(1, 2).constrainOnce().asIterable()
        assertEquals(1, Iterables.getFirst(iterable, 9))
    }

    @Test
    fun getLastWorksOnASinglePassIterable() {
        val iterable = sequenceOf(1, 2).constrainOnce().asIterable()
        assertEquals(2, Iterables.getLast(iterable, 9))
    }

    @Test
    fun getLastWithDefaultOnAListDoesNotRequestAnIterator() {
        val list = CountingList(listOf(1, 2, 3))
        Iterables.getLast(list, 9)
        assertEquals(0, list.iteratorCount)
    }

    @Test
    fun getLastWithDefaultOnAListReturnsTheLastElement() {
        assertEquals(3, Iterables.getLast(CountingList(listOf(1, 2, 3)), 9))
    }

    @Test
    fun getLastWithDefaultOnAnEmptyListReturnsTheDefault() {
        assertEquals(9, Iterables.getLast(CountingList(emptyList()), 9))
    }

    @Test
    fun getLastWithDefaultOnAListReturnsANullLastElementRatherThanTheDefault() {
        assertNull(Iterables.getLast(CountingList(listOf(1, null)), 9))
    }
}
