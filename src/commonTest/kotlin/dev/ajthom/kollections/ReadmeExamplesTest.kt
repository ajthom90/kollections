package dev.ajthom.kollections

import dev.ajthom.kollections.bimap.biMapOf
import dev.ajthom.kollections.bimap.mutableBiMapOf
import dev.ajthom.kollections.iterable.Iterables
import dev.ajthom.kollections.list.ImmutableList
import dev.ajthom.kollections.map.ImmutableMap
import dev.ajthom.kollections.map.MapDifference
import dev.ajthom.kollections.map.difference
import dev.ajthom.kollections.multimap.buildMultimap
import dev.ajthom.kollections.multimap.filterKeys
import dev.ajthom.kollections.multimap.inverse
import dev.ajthom.kollections.multimap.iterator
import dev.ajthom.kollections.multimap.multimapOf
import dev.ajthom.kollections.multimap.multimapWith
import dev.ajthom.kollections.multimap.mutableMultimapOf
import dev.ajthom.kollections.multimap.plus
import dev.ajthom.kollections.multimap.plusAssign
import dev.ajthom.kollections.multimap.setMultimapOf
import dev.ajthom.kollections.multimap.transformValues
import dev.ajthom.kollections.multiset.multisetOf
import dev.ajthom.kollections.multiset.mutableMultisetOf
import dev.ajthom.kollections.set.ImmutableSet
import dev.ajthom.kollections.set.Sets
import dev.ajthom.kollections.table.buildTable
import dev.ajthom.kollections.table.filter
import dev.ajthom.kollections.table.mapValues
import dev.ajthom.kollections.table.mutableTableOf
import dev.ajthom.kollections.table.set
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Pins every snippet in README.md so the documentation cannot drift from the API.
 * Keep these in sync with the README when either changes.
 */
class ReadmeExamplesTest {
    @Test
    fun multimapSection() {
        val byLetter = multimapOf("a" to 1, "a" to 2, "b" to 3)
        assertEquals(listOf(1, 2), byLetter["a"])
        assertEquals(emptyList(), byLetter["z"])
        assertEquals(3, byLetter.size)
        assertEquals(setOf("a", "b"), byLetter.keys)
        assertEquals(listOf(1, 2, 3), byLetter.flatValues.toList())
        assertTrue(byLetter.containsEntry("a", 2))

        val pairs = mutableListOf<Pair<String, Int>>()
        for ((key, value) in byLetter) pairs += key to value
        assertEquals(listOf("a" to 1, "a" to 2, "b" to 3), pairs)

        val tags = setMultimapOf("post" to "kotlin", "post" to "kotlin")
        assertEquals(1, tags.size)

        val grouped = buildMultimap<String, Int> {
            put("a", 1)
            putAll("b", listOf(2, 3))
        }
        assertEquals(listOf(2, 3), grouped["b"])

        val words = listOf("apple", "avocado", "banana")
        val byFirstChar = words.multimapWith { it.first() }
        assertEquals(listOf("apple", "avocado"), byFirstChar['a'])
        assertEquals(listOf('a'), byFirstChar.inverse()["apple"])
        assertEquals(setOf('a'), byFirstChar.filterKeys { it == 'a' }.keys)
        assertEquals(listOf(5, 7), byFirstChar.transformValues { it.length }['a'])
        assertEquals(listOf("cherry"), (byFirstChar + ('c' to "cherry"))['c'])
    }

    @Test
    fun mutableMultimapSection() {
        val mm = mutableMultimapOf<String, Int>()
        mm["a"].add(1)
        assertEquals(listOf(1), mm["a"])
        mm.put("a", 2)
        assertTrue(mm.remove("a", 1))
        mm.replaceValues("b", listOf(7, 8))
        mm += "c" to 9
        mm.keys.remove("b")
        assertEquals(mutableMultimapOf("a" to 2, "c" to 9), mm)
    }

    @Test
    fun tableSection() {
        val table = buildTable<String, String, Int> {
            put("2023", "Q1", 10)
            put("2023", "Q2", 20)
            put("2024", "Q1", 15)
        }
        assertEquals(20, table["2023", "Q2"])
        assertEquals(mapOf("Q1" to 10, "Q2" to 20), table.row("2023"))
        assertEquals(mapOf("2023" to 10, "2024" to 15), table.column("Q1"))
        assertEquals(3, table.size)
        assertEquals(20, table.transpose()["Q2", "2023"])
        assertEquals(40, table.mapValues { it * 2 }["2023", "Q2"])
        assertEquals(1, table.filter { (row, _, value) -> row == "2023" && value > 10 }.size)

        val sales = mutableTableOf<String, String, Int>()
        sales["2025", "Q1"] = 30
        sales.row("2025")["Q2"] = 40
        assertEquals(mapOf("Q1" to 30, "Q2" to 40), sales.row("2025"))
    }

    @Test
    fun multisetSection() {
        val bag = multisetOf("a", "b", "a")
        assertEquals(2, bag.count("a"))
        assertEquals(3, bag.size)
        assertEquals(setOf("a", "b"), bag.elementSet)
        assertEquals(listOf("a" to 2, "b" to 1), bag.entries.map { it.element to it.count })

        val counts = mutableMultisetOf<String>()
        counts.add("x", 3)
        counts.remove("x")
        assertEquals(2, counts.count("x"))
        counts.setCount("x", 0)
        assertTrue(counts.isEmpty())
    }

    @Test
    fun biMapSection() {
        val codes = biMapOf("US" to 1, "CA" to 2)
        assertEquals("CA", codes.inverse()[2])

        val mutable = mutableBiMapOf<String, Int>()
        mutable["US"] = 1
        assertFailsWith<IllegalArgumentException> { mutable["MX"] = 1 }
        mutable.forcePut("MX", 1)
        assertEquals(mapOf("MX" to 1), mutable)
    }

    @Test
    fun helpersSection() {
        assertEquals(listOf(1, 2, 3), ImmutableList.of(1, 2, 3))
        assertEquals(setOf("a", "b"), ImmutableSet.of("a", "b"))
        assertEquals(mapOf("k" to "v"), ImmutableMap.of("k", "v"))
        assertEquals(1, Iterables.getFirst(listOf(1, 2), default = 0))
        assertEquals(42, Iterables.only(listOf(42)))
        assertFailsWith<IllegalArgumentException> { Iterables.only(listOf(1, 2)) }

        assertEquals(setOf(2, 3), Sets.intersection(setOf(1, 2, 3), setOf(2, 3, 4)))
        assertEquals(setOf(1, 3), Sets.symmetricDifference(setOf(1, 2), setOf(2, 3)))
        assertEquals(listOf(emptySet(), setOf(1), setOf(2), setOf(1, 2)), Sets.powerSet(setOf(1, 2)).toList())
        assertEquals(setOf(setOf(1, 2), setOf(1, 3), setOf(2, 3)), Sets.combinations(setOf(1, 2, 3), 2))
        assertEquals(
            listOf(listOf(1, "a"), listOf(1, "b"), listOf(2, "a"), listOf(2, "b")),
            Sets.cartesianProduct(setOf(1, 2), setOf("a", "b")).toList(),
        )

        val diff = mapOf("a" to 1, "b" to 2).difference(mapOf("b" to 3, "c" to 4))
        assertEquals(mapOf("a" to 1), diff.entriesOnlyOnLeft)
        assertEquals(mapOf("b" to MapDifference.ValueDifference(2, 3)), diff.entriesDiffering)
    }
}
