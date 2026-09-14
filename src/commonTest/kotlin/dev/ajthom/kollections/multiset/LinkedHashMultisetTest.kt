package dev.ajthom.kollections.multiset

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class LinkedHashMultisetTest {

    // ---------------------------------------------------------------- construction

    @Test
    fun newMultisetIsEmpty() {
        assertTrue(LinkedHashMultiset<String>().isEmpty())
    }

    @Test
    fun newMultisetHasSizeZero() {
        assertEquals(0, LinkedHashMultiset<String>().size)
    }

    @Test
    fun iterableConstructorCountsDuplicates() {
        val multiset = LinkedHashMultiset(listOf("a", "b", "a"))
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun iterableConstructorKeepsTotalSize() {
        val multiset = LinkedHashMultiset(listOf("a", "b", "a"))
        assertEquals(3, multiset.size)
    }

    @Test
    fun iterableConstructorKeepsInsertionOrder() {
        val multiset = LinkedHashMultiset(listOf("b", "a", "b"))
        assertEquals(listOf("b", "a"), multiset.elementSet.toList())
    }

    // ---------------------------------------------------------------- count / size

    @Test
    fun countOfAbsentElementIsZero() {
        assertEquals(0, LinkedHashMultiset<String>().count("a"))
    }

    @Test
    fun sizeIsTotalNumberOfOccurrences() {
        val multiset = mutableMultisetOf("a", "a", "b")
        assertEquals(3, multiset.size)
    }

    @Test
    fun isEmptyIsFalseAfterAdd() {
        val multiset = LinkedHashMultiset<String>()
        multiset.add("a")
        assertFalse(multiset.isEmpty())
    }

    // ---------------------------------------------------------------- add

    @Test
    fun addReturnsTrue() {
        assertTrue(LinkedHashMultiset<String>().add("a"))
    }

    @Test
    fun addIncrementsCount() {
        val multiset = LinkedHashMultiset<String>()
        multiset.add("a")
        multiset.add("a")
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun addWithOccurrencesReturnsPreviousCount() {
        val multiset = mutableMultisetOf("a")
        assertEquals(1, multiset.add("a", 2))
    }

    @Test
    fun addWithOccurrencesIncreasesCount() {
        val multiset = mutableMultisetOf("a")
        multiset.add("a", 2)
        assertEquals(3, multiset.count("a"))
    }

    @Test
    fun addZeroOccurrencesReturnsCurrentCount() {
        val multiset = mutableMultisetOf("a", "a")
        assertEquals(2, multiset.add("a", 0))
    }

    @Test
    fun addZeroOccurrencesLeavesCountUnchanged() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.add("a", 0)
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun addZeroOccurrencesDoesNotCreateAnEntry() {
        val multiset = LinkedHashMultiset<String>()
        multiset.add("a", 0)
        assertEquals(emptySet(), multiset.elementSet)
    }

    @Test
    fun addNegativeOccurrencesThrows() {
        assertFailsWith<IllegalArgumentException> { LinkedHashMultiset<String>().add("a", -1) }
    }

    @Test
    fun addingOccurrencesThatWouldOverflowTheCountThrows() {
        val multiset = mutableMultisetOf("a")
        assertFailsWith<IllegalArgumentException> { multiset.add("a", Int.MAX_VALUE) }
    }

    @Test
    fun addingOccurrencesThatWouldOverflowTheCountLeavesTheCountUnchanged() {
        val multiset = mutableMultisetOf("a")
        assertFailsWith<IllegalArgumentException> { multiset.add("a", Int.MAX_VALUE) }
        assertEquals(1, multiset.count("a"))
    }

    @Test
    fun addingOccurrencesUpToTheLargestCountIsAllowed() {
        val multiset = mutableMultisetOf("a")
        multiset.add("a", Int.MAX_VALUE - 1)
        assertEquals(Int.MAX_VALUE, multiset.count("a"))
    }

    @Test
    fun addAllAddsEveryOccurrence() {
        val multiset = LinkedHashMultiset<String>()
        multiset.addAll(listOf("a", "a", "b"))
        assertEquals(2, multiset.count("a"))
    }

    // ---------------------------------------------------------------- remove

    @Test
    fun removeReturnsTrueWhenPresent() {
        val multiset = mutableMultisetOf("a")
        assertTrue(multiset.remove("a"))
    }

    @Test
    fun removeReturnsFalseWhenAbsent() {
        val multiset = LinkedHashMultiset<String>()
        assertFalse(multiset.remove("a"))
    }

    @Test
    fun removeTakesAwayASingleOccurrence() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.remove("a")
        assertEquals(1, multiset.count("a"))
    }

    @Test
    fun removingLastOccurrenceDropsTheElement() {
        val multiset = mutableMultisetOf("a")
        multiset.remove("a")
        assertFalse(multiset.contains("a"))
    }

    @Test
    fun removingLastOccurrenceDropsTheElementFromElementSet() {
        val multiset = mutableMultisetOf("a")
        multiset.remove("a")
        assertEquals(emptySet(), multiset.elementSet)
    }

    @Test
    fun removeWithOccurrencesReturnsPreviousCount() {
        val multiset = mutableMultisetOf("a", "a", "a")
        assertEquals(3, multiset.remove("a", 2))
    }

    @Test
    fun removeWithOccurrencesDecreasesCount() {
        val multiset = mutableMultisetOf("a", "a", "a")
        multiset.remove("a", 2)
        assertEquals(1, multiset.count("a"))
    }

    @Test
    fun removeMoreOccurrencesThanPresentRemovesThemAll() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.remove("a", 5)
        assertEquals(0, multiset.count("a"))
    }

    @Test
    fun removeMoreOccurrencesThanPresentDropsTheElement() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.remove("a", 5)
        assertEquals(emptySet(), multiset.elementSet)
    }

    @Test
    fun removeOccurrencesOfAbsentElementReturnsZero() {
        assertEquals(0, LinkedHashMultiset<String>().remove("a", 3))
    }

    @Test
    fun removeZeroOccurrencesLeavesCountUnchanged() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.remove("a", 0)
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun removeNegativeOccurrencesThrows() {
        assertFailsWith<IllegalArgumentException> { mutableMultisetOf("a").remove("a", -1) }
    }

    @Test
    fun removeAllTakesAwayEveryOccurrence() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.removeAll(listOf("a"))
        assertEquals(0, multiset.count("a"))
    }

    @Test
    fun removeAllKeepsOtherElements() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.removeAll(listOf("a"))
        assertEquals(1, multiset.count("b"))
    }

    @Test
    fun removeAllReturnsFalseWhenNothingMatches() {
        val multiset = mutableMultisetOf("a")
        assertFalse(multiset.removeAll(listOf("z")))
    }

    @Test
    fun retainAllKeepsEveryOccurrenceOfRetainedElements() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.retainAll(listOf("a"))
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun retainAllDropsElementsThatAreNotRetained() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.retainAll(listOf("a"))
        assertEquals(setOf("a"), multiset.elementSet)
    }

    @Test
    fun retainAllReturnsFalseWhenEverythingIsRetained() {
        val multiset = mutableMultisetOf("a", "b")
        assertFalse(multiset.retainAll(listOf("a", "b")))
    }

    @Test
    fun clearEmptiesTheMultiset() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.clear()
        assertEquals(0, multiset.size)
    }

    // ---------------------------------------------------------------- setCount

    @Test
    fun setCountReturnsPreviousCount() {
        val multiset = mutableMultisetOf("a", "a")
        assertEquals(2, multiset.setCount("a", 5))
    }

    @Test
    fun setCountReplacesTheCount() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.setCount("a", 5)
        assertEquals(5, multiset.count("a"))
    }

    @Test
    fun setCountAddsAnAbsentElement() {
        val multiset = LinkedHashMultiset<String>()
        multiset.setCount("a", 2)
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun setCountToZeroRemovesTheElement() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.setCount("a", 0)
        assertEquals(emptySet(), multiset.elementSet)
    }

    @Test
    fun setCountToZeroOnAbsentElementDoesNotCreateAnEntry() {
        val multiset = LinkedHashMultiset<String>()
        multiset.setCount("a", 0)
        assertEquals(emptySet(), multiset.elementSet)
    }

    @Test
    fun setCountNegativeThrows() {
        assertFailsWith<IllegalArgumentException> { LinkedHashMultiset<String>().setCount("a", -1) }
    }

    @Test
    fun conditionalSetCountReturnsTrueWhenOldCountMatches() {
        val multiset = mutableMultisetOf("a", "a")
        assertTrue(multiset.setCount("a", 2, 4))
    }

    @Test
    fun conditionalSetCountUpdatesWhenOldCountMatches() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.setCount("a", 2, 4)
        assertEquals(4, multiset.count("a"))
    }

    @Test
    fun conditionalSetCountReturnsFalseWhenOldCountDiffers() {
        val multiset = mutableMultisetOf("a", "a")
        assertFalse(multiset.setCount("a", 1, 4))
    }

    @Test
    fun conditionalSetCountChangesNothingWhenOldCountDiffers() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.setCount("a", 1, 4)
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun conditionalSetCountAddsAnAbsentElementWhenOldCountIsZero() {
        val multiset = LinkedHashMultiset<String>()
        multiset.setCount("a", 0, 3)
        assertEquals(3, multiset.count("a"))
    }

    @Test
    fun conditionalSetCountToZeroRemovesTheElement() {
        val multiset = mutableMultisetOf("a", "a")
        multiset.setCount("a", 2, 0)
        assertEquals(emptySet(), multiset.elementSet)
    }

    @Test
    fun conditionalSetCountWithNegativeOldCountThrows() {
        assertFailsWith<IllegalArgumentException> { LinkedHashMultiset<String>().setCount("a", -1, 1) }
    }

    @Test
    fun conditionalSetCountWithNegativeNewCountThrows() {
        assertFailsWith<IllegalArgumentException> { LinkedHashMultiset<String>().setCount("a", 0, -1) }
    }

    // ---------------------------------------------------------------- contains

    @Test
    fun containsIsTrueForAPresentElement() {
        assertTrue(mutableMultisetOf("a").contains("a"))
    }

    @Test
    fun containsIsFalseForAnAbsentElement() {
        assertFalse(mutableMultisetOf("a").contains("b"))
    }

    @Test
    fun containsAllIsTrueWhenEveryElementIsPresent() {
        assertTrue(mutableMultisetOf("a", "b").containsAll(listOf("a", "b")))
    }

    @Test
    fun containsAllIsFalseWhenAnElementIsMissing() {
        assertFalse(mutableMultisetOf("a").containsAll(listOf("a", "b")))
    }

    @Test
    fun containsAllIgnoresMultiplicity() {
        assertTrue(mutableMultisetOf("a").containsAll(listOf("a", "a")))
    }

    // ---------------------------------------------------------------- iterator

    @Test
    fun iteratorYieldsEachElementCountTimes() {
        val multiset = mutableMultisetOf("a", "b", "a")
        assertEquals(listOf("a", "a", "b"), multiset.toList())
    }

    @Test
    fun iteratorRemoveTakesAwayASingleOccurrence() {
        val multiset = mutableMultisetOf("a", "a", "a")
        val iterator = multiset.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun iteratorRemoveDropsTheElementAtZero() {
        val multiset = mutableMultisetOf("a", "b")
        val iterator = multiset.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(setOf("b"), multiset.elementSet)
    }

    @Test
    fun iteratorRemoveBeforeNextThrows() {
        val iterator = mutableMultisetOf("a").iterator()
        assertFailsWith<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun iteratorRemoveTwiceInARowThrows() {
        val iterator = mutableMultisetOf("a", "a").iterator()
        iterator.next()
        iterator.remove()
        assertFailsWith<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun iteratorCanRemoveEveryOccurrence() {
        val multiset = mutableMultisetOf("a", "a", "b")
        val iterator = multiset.iterator()
        while (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        }
        assertEquals(0, multiset.size)
    }

    // ---------------------------------------------------------------- elementSet

    @Test
    fun elementSetHoldsTheDistinctElements() {
        assertEquals(setOf("a", "b"), mutableMultisetOf("a", "b", "a").elementSet)
    }

    @Test
    fun elementSetIsLiveForAdditions() {
        val multiset = LinkedHashMultiset<String>()
        val elementSet = multiset.elementSet
        multiset.add("a")
        assertEquals(setOf("a"), elementSet)
    }

    @Test
    fun elementSetRemoveDropsEveryOccurrence() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.elementSet.remove("a")
        assertEquals(0, multiset.count("a"))
    }

    @Test
    fun elementSetRemoveUpdatesSize() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.elementSet.remove("a")
        assertEquals(1, multiset.size)
    }

    @Test
    fun elementSetIteratorRemoveDropsEveryOccurrence() {
        val multiset = mutableMultisetOf("a", "a", "b")
        val iterator = multiset.elementSet.iterator()
        iterator.next()
        iterator.remove()
        assertEquals(1, multiset.size)
    }

    @Test
    fun elementSetDoesNotSupportAdd() {
        val multiset = LinkedHashMultiset<String>()
        assertFailsWith<UnsupportedOperationException> { multiset.elementSet.add("a") }
    }

    @Test
    fun elementSetClearEmptiesTheMultiset() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.elementSet.clear()
        assertTrue(multiset.isEmpty())
    }

    @Test
    fun elementSetClearMakesSizeZero() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.elementSet.clear()
        assertEquals(0, multiset.size)
    }

    @Test
    fun elementSetRemoveAllUpdatesSize() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.elementSet.removeAll(setOf("a"))
        assertEquals(1, multiset.size)
    }

    @Test
    fun elementSetRetainAllUpdatesSize() {
        val multiset = mutableMultisetOf("a", "a", "b", "b", "b")
        multiset.elementSet.retainAll(setOf("b"))
        assertEquals(3, multiset.size)
    }

    @Test
    fun elementSetRemoveOfAnAbsentElementLeavesSizeUnchanged() {
        val multiset = mutableMultisetOf("a", "a", "b")
        multiset.elementSet.remove("z")
        assertEquals(3, multiset.size)
    }

    @Test
    fun elementSetIteratorRemoveBeforeNextThrows() {
        val iterator = mutableMultisetOf("a", "b").elementSet.iterator()
        assertFailsWith<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun elementSetIteratorRemoveTwiceInARowThrows() {
        val iterator = mutableMultisetOf("a", "b").elementSet.iterator()
        iterator.next()
        iterator.remove()
        assertFailsWith<IllegalStateException> { iterator.remove() }
    }

    @Test
    fun elementSetIteratorRemoveTwiceInARowLeavesSizeUnchanged() {
        val multiset = mutableMultisetOf("a", "a", "b")
        val iterator = multiset.elementSet.iterator()
        iterator.next()
        iterator.remove()
        runCatching { iterator.remove() }
        assertEquals(1, multiset.size)
    }

    @Test
    fun elementSetSizeIsTheNumberOfDistinctElements() {
        assertEquals(2, mutableMultisetOf("a", "a", "b").elementSet.size)
    }

    @Test
    fun elementSetContainsAPresentElement() {
        assertTrue(mutableMultisetOf("a", "a").elementSet.contains("a"))
    }

    @Test
    fun elementSetDoesNotContainAnAbsentElement() {
        assertFalse(mutableMultisetOf("a").elementSet.contains("z"))
    }

    // ---------------------------------------------------------------- entries

    @Test
    fun entriesAreInInsertionOrder() {
        val multiset = mutableMultisetOf("b", "a", "b")
        val expected = listOf(Multiset.Entry("b", 2), Multiset.Entry("a", 1))
        assertEquals(expected, multiset.entries.toList())
    }

    @Test
    fun entriesHoldOneEntryPerDistinctElement() {
        assertEquals(2, mutableMultisetOf("a", "a", "b").entries.size)
    }

    @Test
    fun entriesAreLive() {
        val multiset = mutableMultisetOf("a")
        val entries = multiset.entries
        multiset.add("a")
        assertEquals(setOf(Multiset.Entry("a", 2)), entries)
    }

    @Test
    fun entriesContainsAMatchingEntry() {
        assertTrue(mutableMultisetOf("a", "a").entries.contains(Multiset.Entry("a", 2)))
    }

    @Test
    fun entriesDoesNotContainAnEntryWithTheWrongCount() {
        assertFalse(mutableMultisetOf("a", "a").entries.contains(Multiset.Entry("a", 1)))
    }

    @Test
    fun entriesOfAnEmptyMultisetAreEmpty() {
        assertTrue(LinkedHashMultiset<String>().entries.isEmpty())
    }

    // ---------------------------------------------------------------- equality

    @Test
    fun equalMultisetsAreEqualRegardlessOfOrder() {
        assertEquals(mutableMultisetOf("a", "b", "a"), mutableMultisetOf("b", "a", "a"))
    }

    @Test
    fun multisetsWithDifferentCountsAreNotEqual() {
        assertNotEquals(mutableMultisetOf("a", "a"), mutableMultisetOf("a"))
    }

    @Test
    fun multisetsWithDifferentElementsAreNotEqual() {
        assertNotEquals(mutableMultisetOf("a", "b"), mutableMultisetOf("a", "c"))
    }

    @Test
    fun aMultisetIsNotEqualToAList() {
        assertNotEquals<Any>(mutableMultisetOf("a"), listOf("a"))
    }

    // `assertEquals` compares `expected == actual` on the JVM but `actual == expected` on JS, so the
    // tests below spell the comparison out to pin down which implementation's `equals` is called.

    @Test
    fun aMultisetIsEqualToAnotherImplementationWithTheSameCounts() {
        val other: Multiset<String> = FixedMultiset(mapOf("a" to 2, "b" to 1))
        assertTrue(mutableMultisetOf("a", "b", "a") == other)
    }

    @Test
    fun aMultisetIsNotEqualToAnotherImplementationWithDifferentCounts() {
        val other: Multiset<String> = FixedMultiset(mapOf("a" to 1))
        assertFalse(mutableMultisetOf("a", "a") == other)
    }

    @Test
    fun equalMultisetsHaveEqualHashCodes() {
        assertEquals(mutableMultisetOf("a", "b", "a").hashCode(), mutableMultisetOf("b", "a", "a").hashCode())
    }

    @Test
    fun hashCodeIsTheSumOfElementHashCodeXorCount() {
        val expected = ("a".hashCode() xor 2) + ("b".hashCode() xor 1)
        assertEquals(expected, mutableMultisetOf("a", "b", "a").hashCode())
    }

    @Test
    fun aNullElementIsCountedLikeAnyOther() {
        val multiset = LinkedHashMultiset<String?>()
        multiset.add(null)
        multiset.add(null)
        assertEquals(2, multiset.count(null))
    }

    @Test
    fun hashCodeTreatsANullElementAsZero() {
        val multiset = LinkedHashMultiset<String?>()
        multiset.add(null)
        assertEquals(0 xor 1, multiset.hashCode())
    }

    @Test
    fun hashCodeOfAnEmptyMultisetIsZero() {
        assertEquals(0, LinkedHashMultiset<String>().hashCode())
    }

    // ---------------------------------------------------------------- toString

    @Test
    fun toStringShowsCountsAboveOne() {
        assertEquals("[a x 2, b]", mutableMultisetOf("a", "b", "a").toString())
    }

    @Test
    fun toStringOfAnEmptyMultisetIsEmptyBrackets() {
        assertEquals("[]", LinkedHashMultiset<String>().toString())
    }
}

/** A minimal read-only [Multiset] used to check equality across implementations. */
private class FixedMultiset<E>(private val counts: Map<E, Int>) : Multiset<E> {
    override val size: Int get() = counts.values.sum()
    override val elementSet: Set<E> get() = counts.keys
    override val entries: Set<Multiset.Entry<E>>
        get() = counts.entries.mapTo(LinkedHashSet()) { Multiset.Entry(it.key, it.value) }

    override fun count(element: E): Int = counts[element] ?: 0
    override fun contains(element: E): Boolean = counts.containsKey(element)
    override fun containsAll(elements: Collection<E>): Boolean = counts.keys.containsAll(elements)
    override fun isEmpty(): Boolean = counts.isEmpty()
    override fun iterator(): Iterator<E> =
        counts.entries.asSequence().flatMap { (element, count) -> List(count) { element }.asSequence() }.iterator()
}
