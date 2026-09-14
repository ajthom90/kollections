package dev.ajthom.kollections.multimap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class MultimapsTest {
    private val pairs = listOf("a" to 1, "a" to 2, "b" to 3)

    // factories

    @Test
    fun emptyMultimapIsEmpty() {
        assertTrue(emptyMultimap<String, Int>().isEmpty())
    }

    @Test
    fun emptySetMultimapIsEmpty() {
        assertTrue(emptySetMultimap<String, Int>().isEmpty())
    }

    @Test
    fun multimapOfPairsKeepsOrder() {
        assertEquals(pairs, multimapOf("a" to 1, "a" to 2, "b" to 3).iterator().asSequence().toList())
    }

    @Test
    fun multimapOfNoPairsIsEmpty() {
        assertTrue(multimapOf<String, Int>().isEmpty())
    }

    @Test
    fun multimapOfMapCopiesContent() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), multimapOf(mapOf("a" to listOf(1, 2), "b" to setOf(3))))
    }

    @Test
    fun multimapOfMapIsIndependentOfSourceMap() {
        val source = mutableMapOf("a" to listOf(1))
        val multimap = multimapOf(source)
        source["b"] = listOf(2)
        assertEquals(multimapOf("a" to 1), multimap)
    }

    @Test
    fun multimapOfMapSkipsEmptyValueCollections() {
        assertFalse(multimapOf(mapOf("a" to emptyList<Int>())).containsKey("a"))
    }

    @Test
    fun setMultimapOfPairsCollapsesDuplicates() {
        assertEquals(setOf(1), setMultimapOf("a" to 1, "a" to 1)["a"])
    }

    @Test
    fun setMultimapOfMapCopiesContent() {
        val multimap = setMultimapOf(mapOf("a" to listOf(1, 2, 1), "b" to listOf(3)))
        assertEquals(setMultimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun mutableMultimapOfPairsKeepsOrder() {
        assertEquals(pairs, mutableMultimapOf("a" to 1, "a" to 2, "b" to 3).iterator().asSequence().toList())
    }

    @Test
    fun mutableSetMultimapOfPairsCollapsesDuplicates() {
        assertEquals(1, mutableSetMultimapOf("a" to 1, "a" to 1).size)
    }

    // asMultimap views

    @Test
    fun asMultimapIsAViewOfTheMap() {
        val source = mutableMapOf("a" to listOf(1))
        val multimap = source.asMultimap()
        source["b"] = listOf(2)
        assertEquals(listOf(2), multimap["b"])
    }

    @Test
    fun asMultimapExposesSameMap() {
        val source = mapOf("a" to listOf(1))
        assertSame(source, source.asMultimap().asMap())
    }

    @Test
    fun asSetMultimapIsAViewOfTheMap() {
        val source = mutableMapOf("a" to setOf(1))
        val multimap = source.asSetMultimap()
        source["b"] = setOf(2)
        assertEquals(setOf(2), multimap["b"])
    }

    // conversions

    @Test
    fun pairsToMultimap() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), pairs.toMultimap())
    }

    @Test
    fun pairsToSetMultimapCollapsesDuplicates() {
        assertEquals(setMultimapOf("a" to 1), listOf("a" to 1, "a" to 1).toSetMultimap())
    }

    @Test
    fun pairsToMutableMultimapIsMutable() {
        val multimap = pairs.toMutableMultimap()
        multimap.put("c", 4)
        assertEquals(4, multimap.size)
    }

    @Test
    fun pairsToMutableSetMultimapCollapsesDuplicates() {
        assertEquals(1, listOf("a" to 1, "a" to 1).toMutableSetMultimap().size)
    }

    @Test
    fun mapToMultimap() {
        val multimap = mapOf("a" to listOf(1, 2), "b" to listOf(3)).toMultimap()
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun mapToSetMultimap() {
        assertEquals(setMultimapOf("a" to 1, "b" to 3), mapOf("a" to listOf(1, 1), "b" to listOf(3)).toSetMultimap())
    }

    @Test
    fun mapToMutableMultimapIsIndependentOfSource() {
        val source = mutableMapOf("a" to mutableListOf(1))
        val multimap = source.toMutableMultimap()
        source["a"]!!.add(2)
        assertEquals(listOf(1), multimap["a"])
    }

    @Test
    fun mapToMutableSetMultimapCollapsesDuplicates() {
        assertEquals(setOf(1), mapOf("a" to listOf(1, 1)).toMutableSetMultimap()["a"])
    }

    @Test
    fun multimapToMultimapCopiesContent() {
        val copy = mutableMultimapOf("a" to 1, "a" to 2, "b" to 3).toMultimap()
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), copy)
    }

    @Test
    fun multimapToMultimapIsIndependentOfSource() {
        val source = mutableMultimapOf("a" to 1)
        val copy = source.toMultimap()
        source.put("a", 2)
        assertEquals(listOf(1), copy["a"])
    }

    @Test
    fun multimapToSetMultimapCollapsesDuplicates() {
        assertEquals(setMultimapOf("a" to 1), multimapOf("a" to 1, "a" to 1).toSetMultimap())
    }

    @Test
    fun multimapToMutableMultimapIsIndependentOfSource() {
        val source = mutableMultimapOf("a" to 1)
        val copy = source.toMutableMultimap()
        copy.put("a", 2)
        assertEquals(listOf(1), source["a"])
    }

    @Test
    fun multimapToMutableSetMultimapCollapsesDuplicates() {
        assertEquals(setOf(1), multimapOf("a" to 1, "a" to 1).toMutableSetMultimap()["a"])
    }

    // builders

    @Test
    fun buildMultimapCollectsPuts() {
        val multimap = buildMultimap {
            put("a", 1)
            put("a", 2)
            put("b", 3)
        }
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), multimap)
    }

    @Test
    fun buildSetMultimapCollapsesDuplicates() {
        val multimap = buildSetMultimap {
            put("a", 1)
            put("a", 1)
        }
        assertEquals(setMultimapOf("a" to 1), multimap)
    }

    // grouping

    @Test
    fun multimapWithGroupsByKeySelector() {
        assertEquals(multimapOf(1 to "a", 1 to "b", 2 to "cc"), listOf("a", "b", "cc").multimapWith { it.length })
    }

    @Test
    fun multimapWithTransformsValues() {
        val multimap = listOf("a", "b", "cc").multimapWith({ it.length }, { it.uppercase() })
        assertEquals(multimapOf(1 to "A", 1 to "B", 2 to "CC"), multimap)
    }

    @Test
    fun multimapWithDestinationReturnsDestination() {
        val destination = mutableSetMultimapOf<Int, String>()
        assertSame(destination, listOf("a", "b").multimapWith(destination) { it.length })
    }

    @Test
    fun multimapWithDestinationFillsDestination() {
        val destination = mutableMultimapOf<Int, String>()
        listOf("a", "b", "cc").multimapWith(destination) { it.length }
        assertEquals(multimapOf(1 to "a", 1 to "b", 2 to "cc"), destination)
    }

    // transforms

    @Test
    fun transformValuesOnListMultimapKeepsDuplicates() {
        assertEquals(multimapOf("a" to 0, "a" to 0), multimapOf("a" to 1, "a" to 2).transformValues { it / 3 })
    }

    @Test
    fun transformValuesOnSetMultimapCollapsesDuplicates() {
        assertEquals(setMultimapOf("a" to 0), setMultimapOf("a" to 1, "a" to 2).transformValues { it / 3 })
    }

    @Test
    fun transformValuesOnPlainMultimapReturnsListFlavour() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1, "a" to 2)
        assertEquals(multimapOf("a" to 0, "a" to 0), multimap.transformValues { it / 3 })
    }

    @Test
    fun mapKeysMergesCollisionsInOrder() {
        assertEquals(multimapOf("x" to 1, "x" to 2, "x" to 3), multimapOf("a" to 1, "a" to 2, "b" to 3).mapKeys { "x" })
    }

    @Test
    fun mapKeysOnSetMultimapUnionsCollisions() {
        assertEquals(setMultimapOf("x" to 1, "x" to 2), setMultimapOf("a" to 1, "b" to 1, "b" to 2).mapKeys { "x" })
    }

    @Test
    fun mapKeysOnPlainMultimapReturnsListFlavour() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1)
        assertEquals(multimapOf("A" to 1), multimap.mapKeys { it.uppercase() })
    }

    @Test
    fun filterKeysKeepsMatchingKeys() {
        assertEquals(multimapOf("b" to 3), multimapOf("a" to 1, "a" to 2, "b" to 3).filterKeys { it == "b" })
    }

    @Test
    fun filterKeysOnSetMultimapReturnsSetFlavour() {
        assertEquals(setMultimapOf("b" to 3), setMultimapOf("a" to 1, "b" to 3).filterKeys { it == "b" })
    }

    @Test
    fun filterKeysOnPlainMultimapReturnsListFlavour() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1, "b" to 3)
        assertEquals(multimapOf("b" to 3), multimap.filterKeys { it == "b" })
    }

    @Test
    fun filterValuesDropsKeysLeftEmpty() {
        assertEquals(multimapOf("a" to 2), multimapOf("a" to 1, "a" to 2, "b" to 3).filterValues { it == 2 })
    }

    @Test
    fun filterValuesOnSetMultimapReturnsSetFlavour() {
        assertEquals(setMultimapOf("a" to 2), setMultimapOf("a" to 1, "a" to 2, "b" to 3).filterValues { it == 2 })
    }

    @Test
    fun filterValuesOnPlainMultimapReturnsListFlavour() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1, "a" to 2)
        assertEquals(multimapOf("a" to 2), multimap.filterValues { it == 2 })
    }

    @Test
    fun filterKeepsPairsMatchingPredicate() {
        val filtered = multimapOf("a" to 1, "a" to 2, "b" to 3).filter { key, value -> key == "a" && value > 1 }
        assertEquals(multimapOf("a" to 2), filtered)
    }

    @Test
    fun filterOnSetMultimapReturnsSetFlavour() {
        assertEquals(setMultimapOf("b" to 3), setMultimapOf("a" to 1, "b" to 3).filter { _, value -> value > 1 })
    }

    @Test
    fun filterOnPlainMultimapReturnsListFlavour() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1, "b" to 3)
        assertEquals(multimapOf("b" to 3), multimap.filter { _, value -> value > 1 })
    }

    @Test
    fun inverseOnListMultimapSwapsKeysAndValues() {
        assertEquals(multimapOf(1 to "a", 1 to "b", 2 to "a"), multimapOf("a" to 1, "a" to 2, "b" to 1).inverse())
    }

    @Test
    fun inverseOnListMultimapKeepsDuplicates() {
        assertEquals(multimapOf(1 to "a", 1 to "a"), multimapOf("a" to 1, "a" to 1).inverse())
    }

    @Test
    fun inverseOnSetMultimapReturnsSetFlavour() {
        assertEquals(setMultimapOf(1 to "a", 1 to "b"), setMultimapOf("a" to 1, "b" to 1).inverse())
    }

    @Test
    fun inverseOnPlainMultimapReturnsListFlavour() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1, "b" to 1)
        assertEquals(multimapOf(1 to "a", 1 to "b"), multimap.inverse())
    }

    // operators on list multimaps

    @Test
    fun listPlusPairAppendsPair() {
        assertEquals(multimapOf("a" to 1, "a" to 2), multimapOf("a" to 1) + ("a" to 2))
    }

    @Test
    fun listPlusPairLeavesOriginalUnchanged() {
        val original = multimapOf("a" to 1)
        original + ("a" to 2)
        assertEquals(multimapOf("a" to 1), original)
    }

    @Test
    fun listPlusPairsAppendsEveryPair() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), multimapOf("a" to 1) + listOf("a" to 2, "b" to 3))
    }

    @Test
    fun listPlusMultimapAppendsEveryPair() {
        assertEquals(multimapOf("a" to 1, "a" to 2, "b" to 3), multimapOf("a" to 1) + multimapOf("a" to 2, "b" to 3))
    }

    @Test
    fun listPlusDoesNotAliasMutableSource() {
        val source = mutableMultimapOf("a" to 1)
        val sum = source + ("b" to 2)
        source.put("a", 9)
        assertEquals(multimapOf("a" to 1, "b" to 2), sum)
    }

    @Test
    fun listMinusKeyRemovesKey() {
        assertEquals(multimapOf("b" to 3), multimapOf("a" to 1, "a" to 2, "b" to 3) - "a")
    }

    @Test
    fun listMinusKeysRemovesEveryKey() {
        assertEquals(multimapOf("c" to 4), multimapOf("a" to 1, "b" to 3, "c" to 4) - listOf("a", "b"))
    }

    @Test
    fun listMinusPairRemovesOneOccurrence() {
        assertEquals(multimapOf("a" to 1, "b" to 3), multimapOf("a" to 1, "a" to 1, "b" to 3) - ("a" to 1))
    }

    @Test
    fun listMinusPairDropsKeyWhenEmptied() {
        assertFalse((multimapOf("a" to 1, "b" to 3) - ("a" to 1)).containsKey("a"))
    }

    // operators on set multimaps

    @Test
    fun setPlusPairAddsPair() {
        assertEquals(setMultimapOf("a" to 1, "a" to 2), setMultimapOf("a" to 1) + ("a" to 2))
    }

    @Test
    fun setPlusPairsCollapsesDuplicates() {
        assertEquals(setMultimapOf("a" to 1, "b" to 3), setMultimapOf("a" to 1) + listOf("a" to 1, "b" to 3))
    }

    @Test
    fun setPlusMultimapUnionsValues() {
        assertEquals(setMultimapOf("a" to 1, "a" to 2), setMultimapOf("a" to 1) + multimapOf("a" to 1, "a" to 2))
    }

    @Test
    fun setMinusKeyRemovesKey() {
        assertEquals(setMultimapOf("b" to 3), setMultimapOf("a" to 1, "b" to 3) - "a")
    }

    @Test
    fun setMinusKeysRemovesEveryKey() {
        assertEquals(setMultimapOf("c" to 4), setMultimapOf("a" to 1, "b" to 3, "c" to 4) - listOf("a", "b"))
    }

    @Test
    fun setMinusPairRemovesPair() {
        assertEquals(setMultimapOf("a" to 2), setMultimapOf("a" to 1, "a" to 2) - ("a" to 1))
    }

    @Test
    fun setMinusPairDropsKeyWhenEmptied() {
        assertFalse((setMultimapOf("a" to 1, "b" to 3) - ("a" to 1)).containsKey("a"))
    }

    // null handling and emptiness

    @Test
    fun orEmptyOnNullReturnsEmptyMultimap() {
        val multimap: Multimap<String, Int>? = null
        assertTrue(multimap.orEmpty().isEmpty())
    }

    @Test
    fun orEmptyOnNonNullReturnsSameInstance() {
        val multimap: Multimap<String, Int> = multimapOf("a" to 1)
        assertSame(multimap, multimap.orEmpty())
    }

    @Test
    fun orEmptyOnNullListMultimapReturnsListMultimap() {
        val multimap: ListMultimap<String, Int>? = null
        assertEquals(emptyList(), multimap.orEmpty()["a"])
    }

    @Test
    fun orEmptyOnNullSetMultimapReturnsSetMultimap() {
        val multimap: SetMultimap<String, Int>? = null
        assertEquals(emptySet(), multimap.orEmpty()["a"])
    }

    @Test
    fun isNotEmptyIsTrueForPopulatedMultimap() {
        assertTrue(multimapOf("a" to 1).isNotEmpty())
    }

    @Test
    fun isNotEmptyIsFalseForEmptyMultimap() {
        assertFalse(emptyMultimap<String, Int>().isNotEmpty())
    }

    // iteration

    @Test
    fun iteratorOverEmptyMultimapHasNoElements() {
        assertFalse(emptyMultimap<String, Int>().iterator().hasNext())
    }

    @Test
    fun iteratorSeesEveryPair() {
        assertEquals(pairs, multimapOf("a" to 1, "a" to 2, "b" to 3).iterator().asSequence().toList())
    }

    // deprecated aliases

    @Suppress("DEPRECATION")
    @Test
    fun deprecatedDelegatingMultimapStillConstructs() {
        assertEquals(listOf(1), DelegatingMultimap(mapOf("a" to listOf(1)))["a"])
    }

    @Suppress("DEPRECATION")
    @Test
    fun deprecatedDelegatingMutableMultimapStillConstructs() {
        val multimap = DelegatingMutableMultimap(mutableMapOf<String, MutableList<Int>>())
        multimap.put("a", 1)
        assertEquals(listOf(1), multimap["a"])
    }

    @Suppress("DEPRECATION")
    @Test
    fun deprecatedMutableLinkedHashMultimapStillConstructs() {
        val multimap = MutableLinkedHashMultimap<String, Int>()
        multimap.put("a", 1)
        assertEquals(listOf(1), multimap["a"])
    }

    @Test
    fun toMultimapOnReadOnlyMultimapReturnsNewInstance() {
        val multimap = multimapOf("a" to 1)
        assertNotSame(multimap, multimap.toMultimap())
    }
}
