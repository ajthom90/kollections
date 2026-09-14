package dev.ajthom.kollections.multiset

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MultisetsTest {

    // ---------------------------------------------------------------- emptyMultiset

    @Test
    fun emptyMultisetIsEmpty() {
        assertTrue(emptyMultiset<String>().isEmpty())
    }

    @Test
    fun emptyMultisetHasSizeZero() {
        assertEquals(0, emptyMultiset<String>().size)
    }

    @Test
    fun emptyMultisetCountsNothing() {
        assertEquals(0, emptyMultiset<String>().count("a"))
    }

    @Test
    fun emptyMultisetContainsNothing() {
        assertFalse(emptyMultiset<String>().contains("a"))
    }

    @Test
    fun emptyMultisetContainsAllOfAnEmptyCollection() {
        assertTrue(emptyMultiset<String>().containsAll(emptyList()))
    }

    @Test
    fun emptyMultisetDoesNotContainAllOfANonEmptyCollection() {
        assertFalse(emptyMultiset<String>().containsAll(listOf("a")))
    }

    @Test
    fun emptyMultisetHasNoElements() {
        assertEquals(emptySet(), emptyMultiset<String>().elementSet)
    }

    @Test
    fun emptyMultisetHasNoEntries() {
        assertEquals(emptySet(), emptyMultiset<String>().entries)
    }

    @Test
    fun emptyMultisetIteratorHasNoNext() {
        assertFalse(emptyMultiset<String>().iterator().hasNext())
    }

    @Test
    fun emptyMultisetIsASingleton() {
        assertSame<Any>(emptyMultiset<String>(), emptyMultiset<Int>())
    }

    // `assertEquals` compares `expected == actual` on the JVM but `actual == expected` on JS, so the
    // tests below spell the comparison out to pin down which implementation's `equals` is called.

    @Test
    fun emptyMultisetEqualsAnEmptyLinkedHashMultiset() {
        assertTrue(emptyMultiset<String>() == LinkedHashMultiset<String>())
    }

    @Test
    fun anEmptyLinkedHashMultisetEqualsEmptyMultiset() {
        assertTrue(LinkedHashMultiset<String>() == emptyMultiset<String>())
    }

    @Test
    fun emptyMultisetDoesNotEqualANonEmptyMultiset() {
        assertFalse(emptyMultiset<String>() == multisetOf("a"))
    }

    @Test
    fun emptyMultisetHashCodeIsZero() {
        assertEquals(0, emptyMultiset<String>().hashCode())
    }

    @Test
    fun emptyMultisetToStringIsEmptyBrackets() {
        assertEquals("[]", emptyMultiset<String>().toString())
    }

    // ---------------------------------------------------------------- multisetOf

    @Test
    fun multisetOfCountsDuplicates() {
        assertEquals(2, multisetOf("a", "b", "a").count("a"))
    }

    @Test
    fun multisetOfKeepsTotalSize() {
        assertEquals(3, multisetOf("a", "b", "a").size)
    }

    @Test
    fun multisetOfWithoutArgumentsIsEmpty() {
        assertTrue(multisetOf<String>().isEmpty())
    }

    // ---------------------------------------------------------------- mutableMultisetOf

    @Test
    fun mutableMultisetOfCountsDuplicates() {
        assertEquals(2, mutableMultisetOf("a", "b", "a").count("a"))
    }

    @Test
    fun mutableMultisetOfIsMutable() {
        val multiset = mutableMultisetOf("a")
        multiset.add("a")
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun mutableMultisetOfWithoutArgumentsIsEmpty() {
        assertTrue(mutableMultisetOf<String>().isEmpty())
    }

    // ---------------------------------------------------------------- Iterable.toMultiset

    @Test
    fun iterableToMultisetCountsDuplicates() {
        assertEquals(2, listOf("a", "b", "a").toMultiset().count("a"))
    }

    @Test
    fun iterableToMultisetKeepsInsertionOrder() {
        assertEquals(listOf("b", "a"), listOf("b", "a", "b").toMultiset().elementSet.toList())
    }

    @Test
    fun iterableToMultisetOfAnEmptyListIsEmpty() {
        assertTrue(emptyList<String>().toMultiset().isEmpty())
    }

    @Test
    fun iterableToMutableMultisetCountsDuplicates() {
        assertEquals(2, listOf("a", "b", "a").toMutableMultiset().count("a"))
    }

    @Test
    fun iterableToMutableMultisetIsMutable() {
        val multiset = listOf("a").toMutableMultiset()
        multiset.add("a")
        assertEquals(2, multiset.count("a"))
    }

    @Test
    fun iterableToMutableMultisetIsACopy() {
        val source = mutableListOf("a")
        val multiset = source.toMutableMultiset()
        source.add("b")
        assertFalse(multiset.contains("b"))
    }

    // ---------------------------------------------------------------- Map.toMultiset

    @Test
    fun mapToMultisetUsesTheValuesAsCounts() {
        assertEquals(3, mapOf("a" to 3).toMultiset().count("a"))
    }

    @Test
    fun mapToMultisetSumsCountsIntoSize() {
        assertEquals(5, mapOf("a" to 3, "b" to 2).toMultiset().size)
    }

    @Test
    fun mapToMultisetSkipsZeroCounts() {
        assertEquals(setOf("a"), mapOf("a" to 1, "b" to 0).toMultiset().elementSet)
    }

    @Test
    fun mapToMultisetKeepsInsertionOrder() {
        assertEquals(listOf("b", "a"), mapOf("b" to 1, "a" to 2).toMultiset().elementSet.toList())
    }

    @Test
    fun mapToMultisetRejectsNegativeCounts() {
        assertFailsWith<IllegalArgumentException> { mapOf("a" to -1).toMultiset() }
    }

    @Test
    fun mapToMultisetOfAnEmptyMapIsEmpty() {
        assertTrue(emptyMap<String, Int>().toMultiset().isEmpty())
    }
}
