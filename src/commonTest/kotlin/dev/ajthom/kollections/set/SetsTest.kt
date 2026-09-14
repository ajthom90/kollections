package dev.ajthom.kollections.set

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SetsTest {
    @Test
    fun intersectionHoldsTheElementsInBothSets() {
        assertEquals(setOf(2, 3), Sets.intersection(setOf(1, 2, 3), setOf(2, 3, 4)))
    }

    @Test
    fun intersectionOfDisjointSetsIsEmpty() {
        assertTrue(Sets.intersection(setOf(1), setOf(2)).isEmpty())
    }

    @Test
    fun intersectionKeepsTheOrderOfTheLeftSet() {
        assertEquals(listOf(3, 1), Sets.intersection(setOf(3, 2, 1), setOf(1, 3)).toList())
    }

    @Test
    fun unionHoldsTheElementsOfBothSets() {
        assertEquals(setOf(1, 2, 3, 4), Sets.union(setOf(1, 2), setOf(3, 4)))
    }

    @Test
    fun unionKeepsLeftElementsFirstAndDoesNotRepeatThem() {
        assertEquals(listOf(1, 2, 3), Sets.union(setOf(1, 2), setOf(2, 3)).toList())
    }

    @Test
    fun differenceHoldsTheLeftElementsMissingFromTheRightSet() {
        assertEquals(setOf(1), Sets.difference(setOf(1, 2), setOf(2, 3)))
    }

    @Test
    fun differenceIgnoresRightOnlyElements() {
        assertFalse(Sets.difference(setOf(1, 2), setOf(2, 3)).contains(3))
    }

    @Test
    fun symmetricDifferenceHoldsTheElementsOfExactlyOneSet() {
        assertEquals(setOf(1, 3), Sets.symmetricDifference(setOf(1, 2), setOf(2, 3)))
    }

    @Test
    fun symmetricDifferenceOfEqualSetsIsEmpty() {
        assertTrue(Sets.symmetricDifference(setOf(1, 2), setOf(1, 2)).isEmpty())
    }

    @Test
    fun symmetricDifferenceListsLeftOnlyElementsFirst() {
        assertEquals(listOf(1, 3), Sets.symmetricDifference(setOf(1, 2), setOf(3, 2)).toList())
    }

    @Test
    fun powerSetOfThreeElementsHasEightMembers() {
        assertEquals(8, Sets.powerSet(setOf(1, 2, 3)).size)
    }

    @Test
    fun powerSetIteratesOverAsManyMembersAsItsSize() {
        assertEquals(8, Sets.powerSet(setOf(1, 2, 3)).toList().size)
    }

    @Test
    fun powerSetStartsWithTheEmptySet() {
        assertEquals(emptySet(), Sets.powerSet(setOf(1, 2, 3)).first())
    }

    @Test
    fun powerSetEndsWithTheFullSet() {
        assertEquals(setOf(1, 2, 3), Sets.powerSet(setOf(1, 2, 3)).last())
    }

    @Test
    fun powerSetHoldsEverySubset() {
        val expected = setOf(
            emptySet(),
            setOf(1),
            setOf(2),
            setOf(3),
            setOf(1, 2),
            setOf(1, 3),
            setOf(2, 3),
            setOf(1, 2, 3),
        )
        assertEquals(expected, Sets.powerSet(setOf(1, 2, 3)).toSet())
    }

    @Test
    fun powerSetIteratesInAscendingBitmaskOrder() {
        val expected = listOf(
            emptySet(),
            setOf(1),
            setOf(2),
            setOf(1, 2),
            setOf(3),
            setOf(1, 3),
            setOf(2, 3),
            setOf(1, 2, 3),
        )
        assertEquals(expected, Sets.powerSet(setOf(1, 2, 3)).toList())
    }

    @Test
    fun powerSetContainsASubsetOfTheInput() {
        assertTrue(Sets.powerSet(setOf(1, 2, 3)).contains(setOf(1, 3)))
    }

    @Test
    fun powerSetContainsTheEmptySet() {
        assertTrue(Sets.powerSet(setOf(1, 2, 3)).contains(emptySet()))
    }

    @Test
    fun powerSetDoesNotContainASetWithAForeignElement() {
        assertFalse(Sets.powerSet(setOf(1, 2, 3)).contains(setOf(1, 4)))
    }

    @Test
    fun powerSetOfTheEmptySetHoldsOnlyTheEmptySet() {
        assertEquals(setOf(emptySet<Int>()), Sets.powerSet(emptySet<Int>()).toSet())
    }

    @Test
    fun powerSetOfThirtyElementsReportsItsSizeWithoutIterating() {
        assertEquals(1 shl 30, Sets.powerSet((1..30).toSet()).size)
    }

    @Test
    fun powerSetOfMoreThanThirtyElementsIsRejected() {
        assertFailsWith<IllegalArgumentException> { Sets.powerSet((1..31).toSet()) }
    }

    @Test
    fun combinationsHoldsEveryChoiceOfKElements() {
        assertEquals(setOf(setOf(1, 2), setOf(1, 3), setOf(2, 3)), Sets.combinations(setOf(1, 2, 3), 2))
    }

    @Test
    fun combinationsHasNChooseKMembers() {
        assertEquals(3, Sets.combinations(setOf(1, 2, 3), 2).size)
    }

    @Test
    fun combinationsIteratesInLexicographicIndexOrder() {
        val expected = listOf(setOf(1, 2), setOf(1, 3), setOf(2, 3))
        assertEquals(expected, Sets.combinations(setOf(1, 2, 3), 2).toList())
    }

    @Test
    fun combinationsOfSizeZeroHoldsOnlyTheEmptySet() {
        assertEquals(setOf(emptySet<Int>()), Sets.combinations(setOf(1, 2, 3), 0).toSet())
    }

    @Test
    fun combinationsOfSizeZeroHasOneMember() {
        assertEquals(1, Sets.combinations(setOf(1, 2, 3), 0).size)
    }

    @Test
    fun combinationsLargerThanTheSetIsEmpty() {
        assertTrue(Sets.combinations(setOf(1, 2, 3), 4).isEmpty())
    }

    @Test
    fun combinationsLargerThanTheSetHasNoMembers() {
        assertEquals(0, Sets.combinations(setOf(1, 2, 3), 4).size)
    }

    @Test
    fun combinationsOfTheWholeSetHoldsOnlyThatSet() {
        assertEquals(setOf(setOf(1, 2, 3)), Sets.combinations(setOf(1, 2, 3), 3).toSet())
    }

    @Test
    fun combinationsContainsAChoiceOfTheRightSize() {
        assertTrue(Sets.combinations(setOf(1, 2, 3), 2).contains(setOf(2, 3)))
    }

    @Test
    fun combinationsDoesNotContainAChoiceOfTheWrongSize() {
        assertFalse(Sets.combinations(setOf(1, 2, 3), 2).contains(setOf(1, 2, 3)))
    }

    @Test
    fun combinationsDoesNotContainAChoiceWithAForeignElement() {
        assertFalse(Sets.combinations(setOf(1, 2, 3), 2).contains(setOf(1, 4)))
    }

    @Test
    fun combinationsOfANegativeSizeIsRejected() {
        assertFailsWith<IllegalArgumentException> { Sets.combinations(setOf(1, 2, 3), -1) }
    }

    @Test
    fun combinationsThatDoNotFitInAnIntAreRejected() {
        assertFailsWith<IllegalArgumentException> { Sets.combinations((1..100).toSet(), 50) }
    }

    @Test
    fun cartesianProductHasOneListPerCombinationOfElements() {
        assertEquals(4, Sets.cartesianProduct<Any>(setOf(1, 2), setOf("a", "b")).size)
    }

    @Test
    fun cartesianProductIteratesWithTheLastSetVaryingFastest() {
        val expected = listOf(
            listOf<Any>(1, "a"),
            listOf<Any>(1, "b"),
            listOf<Any>(2, "a"),
            listOf<Any>(2, "b"),
        )
        assertEquals(expected, Sets.cartesianProduct<Any>(setOf(1, 2), setOf("a", "b")).toList())
    }

    @Test
    fun cartesianProductOfThreeSetsHasTheProductOfTheirSizes() {
        assertEquals(12, Sets.cartesianProduct(setOf(1, 2), setOf(3, 4, 5), setOf(6, 7)).size)
    }

    @Test
    fun cartesianProductWithAnEmptySetIsEmpty() {
        assertTrue(Sets.cartesianProduct(setOf(1, 2), emptySet<Int>()).isEmpty())
    }

    @Test
    fun cartesianProductWithAnEmptySetHasNoMembers() {
        assertEquals(0, Sets.cartesianProduct(setOf(1, 2), emptySet<Int>()).size)
    }

    @Test
    fun cartesianProductOfNoSetsHoldsOneEmptyList() {
        assertEquals(setOf(emptyList<Int>()), Sets.cartesianProduct<Int>().toSet())
    }

    @Test
    fun cartesianProductOfNoSetsHasOneMember() {
        assertEquals(1, Sets.cartesianProduct<Int>().size)
    }

    @Test
    fun cartesianProductContainsATupleBuiltFromTheSets() {
        assertTrue(Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)).contains(listOf(2, 3)))
    }

    @Test
    fun cartesianProductDoesNotContainATupleWithAForeignElement() {
        assertFalse(Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)).contains(listOf(2, 5)))
    }

    @Test
    fun cartesianProductDoesNotContainATupleOfTheWrongLength() {
        assertFalse(Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)).contains(listOf(1)))
    }

    @Test
    fun cartesianProductOfAListOfSetsBehavesLikeTheVarargOverload() {
        val fromList = Sets.cartesianProduct(listOf(setOf(1, 2), setOf(3, 4)))
        assertEquals(Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)), fromList)
    }

    @Test
    fun cartesianProductOfAnEmptyListOfSetsHoldsOneEmptyList() {
        assertEquals(setOf(emptyList<Int>()), Sets.cartesianProduct(emptyList<Set<Int>>()).toSet())
    }

    @Test
    fun cartesianProductThatDoesNotFitInAnIntIsRejected() {
        val axis = (1..2000).toSet()
        assertFailsWith<IllegalArgumentException> { Sets.cartesianProduct(axis, axis, axis) }
    }

    @Test
    fun powerSetsOfEqualSetsAreEqual() {
        assertEquals(Sets.powerSet(setOf(1, 2, 3)), Sets.powerSet(setOf(1, 2, 3)))
    }

    @Test
    fun powerSetsOfDifferentSetsAreNotEqual() {
        assertNotEquals(Sets.powerSet(setOf(1, 2)), Sets.powerSet(setOf(1, 3)))
    }

    @Test
    fun powerSetEqualsAPlainSetHoldingTheSameSubsets() {
        assertEquals(Sets.powerSet(setOf(1, 2)), setOf(emptySet(), setOf(1), setOf(2), setOf(1, 2)))
    }

    @Test
    fun powerSetsOfEqualSetsHaveEqualHashCodes() {
        assertEquals(Sets.powerSet(setOf(1, 2, 3)).hashCode(), Sets.powerSet(setOf(1, 2, 3)).hashCode())
    }

    @Test
    fun powerSetHashCodeIsTheSumOfTheHashCodesOfItsMembers() {
        val expected = Sets.powerSet(setOf(1, 2, 3)).sumOf { it.hashCode() }
        assertEquals(expected, Sets.powerSet(setOf(1, 2, 3)).hashCode())
    }

    @Test
    fun powerSetOfTheEmptySetHashesLikeASetHoldingTheEmptySet() {
        assertEquals(setOf(emptySet<Int>()).hashCode(), Sets.powerSet(emptySet<Int>()).hashCode())
    }

    @Test
    fun powerSetOfThirtyElementsHashesWithoutIteratingItsMembers() {
        assertEquals((1..30).toSet().hashCode() shl 29, Sets.powerSet((1..30).toSet()).hashCode())
    }

    @Test
    fun powerSetRendersItsInputSet() {
        assertEquals("powerSet([1, 2])", Sets.powerSet(setOf(1, 2)).toString())
    }

    @Test
    fun combinationsOfEqualInputsAreEqual() {
        assertEquals(Sets.combinations(setOf(1, 2, 3), 2), Sets.combinations(setOf(1, 2, 3), 2))
    }

    @Test
    fun combinationsOfDifferentSetsAreNotEqual() {
        assertNotEquals(Sets.combinations(setOf(1, 2, 3), 2), Sets.combinations(setOf(1, 2, 4), 2))
    }

    @Test
    fun combinationsOfDifferentSizesAreNotEqual() {
        assertNotEquals(Sets.combinations(setOf(1, 2, 3), 2), Sets.combinations(setOf(1, 2, 3), 1))
    }

    @Test
    fun combinationsEqualsAPlainSetHoldingTheSameChoices() {
        assertEquals(Sets.combinations(setOf(1, 2, 3), 2), setOf(setOf(1, 2), setOf(1, 3), setOf(2, 3)))
    }

    @Test
    fun combinationsOfEqualInputsHaveEqualHashCodes() {
        val left = Sets.combinations(setOf(1, 2, 3), 2).hashCode()
        assertEquals(left, Sets.combinations(setOf(1, 2, 3), 2).hashCode())
    }

    @Test
    fun combinationsHashCodeIsTheSumOfTheHashCodesOfItsMembers() {
        val expected = Sets.combinations(setOf(1, 2, 3, 4), 2).sumOf { it.hashCode() }
        assertEquals(expected, Sets.combinations(setOf(1, 2, 3, 4), 2).hashCode())
    }

    @Test
    fun combinationsOfSizeZeroHashesLikeASetHoldingTheEmptySet() {
        assertEquals(setOf(emptySet<Int>()).hashCode(), Sets.combinations(setOf(1, 2, 3), 0).hashCode())
    }

    @Test
    fun combinationsLargerThanTheSetHashesLikeTheEmptySet() {
        assertEquals(emptySet<Set<Int>>().hashCode(), Sets.combinations(setOf(1, 2, 3), 4).hashCode())
    }

    @Test
    fun combinationsOfManyMembersHashesWithoutIteratingThem() {
        val expected = binomial(29, 14) * (1..30).toSet().hashCode()
        assertEquals(expected, Sets.combinations((1..30).toSet(), 15).hashCode())
    }

    @Test
    fun combinationsRendersItsInputSetAndSize() {
        assertEquals("combinations([1, 2, 3], 2)", Sets.combinations(setOf(1, 2, 3), 2).toString())
    }

    @Test
    fun cartesianProductsOfEqualInputsAreEqual() {
        val left = Sets.cartesianProduct(setOf(1, 2), setOf(3, 4))
        assertEquals(left, Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)))
    }

    @Test
    fun cartesianProductsOfDifferentInputsAreNotEqual() {
        val left = Sets.cartesianProduct(setOf(1, 2), setOf(3, 4))
        assertNotEquals(left, Sets.cartesianProduct(setOf(1, 2), setOf(3, 5)))
    }

    @Test
    fun cartesianProductEqualsAPlainSetHoldingTheSameTuples() {
        val expected = setOf(listOf(1, 3), listOf(1, 4), listOf(2, 3), listOf(2, 4))
        assertEquals(Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)), expected)
    }

    @Test
    fun cartesianProductsOfEqualInputsHaveEqualHashCodes() {
        val left = Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)).hashCode()
        assertEquals(left, Sets.cartesianProduct(setOf(1, 2), setOf(3, 4)).hashCode())
    }

    @Test
    fun cartesianProductHashCodeIsTheSumOfTheHashCodesOfItsMembers() {
        val expected = Sets.cartesianProduct(setOf(1, 2), setOf(3, 4, 5)).sumOf { it.hashCode() }
        assertEquals(expected, Sets.cartesianProduct(setOf(1, 2), setOf(3, 4, 5)).hashCode())
    }

    @Test
    fun cartesianProductOfNoSetsHashesLikeASetHoldingTheEmptyList() {
        assertEquals(setOf(emptyList<Int>()).hashCode(), Sets.cartesianProduct<Int>().hashCode())
    }

    @Test
    fun cartesianProductWithAnEmptySetHashesLikeTheEmptySet() {
        val product = Sets.cartesianProduct(setOf(1, 2), emptySet<Int>())
        assertEquals(emptySet<List<Int>>().hashCode(), product.hashCode())
    }

    @Test
    fun cartesianProductRendersItsInputSets() {
        val product = Sets.cartesianProduct(setOf(1, 2), setOf(3, 4))
        assertEquals("cartesianProduct([[1, 2], [3, 4]])", product.toString())
    }

    @Test
    fun combinationsOfSizeZeroOverDifferentSetsAreEqual() {
        assertEquals(Sets.combinations(setOf(1, 2, 3), 0), Sets.combinations(setOf(9), 0))
    }

    @Test
    fun combinationsOfSizeZeroOverDifferentSetsAreEqualTheOtherWayRound() {
        assertEquals(Sets.combinations(setOf(9), 0), Sets.combinations(setOf(1, 2, 3), 0))
    }

    @Test
    fun combinationsOfSizeZeroOverDifferentSetsHaveEqualHashCodes() {
        val left = Sets.combinations(setOf(1, 2, 3), 0).hashCode()
        assertEquals(left, Sets.combinations(setOf(9), 0).hashCode())
    }

    @Test
    fun combinationsLargerThanDifferentSetsAreEqual() {
        assertEquals(Sets.combinations(setOf(1, 2), 5), Sets.combinations(setOf(3, 4), 5))
    }

    @Test
    fun combinationsLargerThanDifferentSetsAreEqualTheOtherWayRound() {
        assertEquals(Sets.combinations(setOf(3, 4), 5), Sets.combinations(setOf(1, 2), 5))
    }

    @Test
    fun combinationsLargerThanDifferentSetsHaveEqualHashCodes() {
        val left = Sets.combinations(setOf(1, 2), 5).hashCode()
        assertEquals(left, Sets.combinations(setOf(3, 4), 5).hashCode())
    }

    @Test
    fun emptyCartesianProductsOverDifferentAxesAreEqual() {
        val left = Sets.cartesianProduct(setOf(1), emptySet<Int>())
        assertEquals(left, Sets.cartesianProduct(setOf(2), emptySet<Int>()))
    }

    @Test
    fun emptyCartesianProductsOverDifferentAxesAreEqualTheOtherWayRound() {
        val left = Sets.cartesianProduct(setOf(2), emptySet<Int>())
        assertEquals(left, Sets.cartesianProduct(setOf(1), emptySet<Int>()))
    }

    @Test
    fun emptyCartesianProductsOverDifferentAxesHaveEqualHashCodes() {
        val left = Sets.cartesianProduct(setOf(1), emptySet<Int>()).hashCode()
        assertEquals(left, Sets.cartesianProduct(setOf(2), emptySet<Int>()).hashCode())
    }

    @Test
    fun combinationsOfSizeZeroEqualsAPlainSetHoldingTheEmptySet() {
        assertEquals(Sets.combinations(setOf(1, 2, 3), 0), setOf(emptySet<Int>()))
    }

    @Test
    fun combinationsLargerThanTheSetEqualsThePlainEmptySet() {
        assertEquals(Sets.combinations(setOf(1, 2), 5), emptySet<Set<Int>>())
    }

    @Test
    fun emptyCartesianProductEqualsThePlainEmptySet() {
        assertEquals(Sets.cartesianProduct(setOf(1), emptySet<Int>()), emptySet<List<Int>>())
    }

    @Test
    fun binomialOfANegativeChoiceIsZero() {
        assertEquals(0, binomial(3, -1))
    }

    @Test
    fun binomialOfAChoiceLargerThanTheCountIsZero() {
        assertEquals(0, binomial(3, 4))
    }

    @Test
    fun binomialCountsTheWaysToChoose() {
        assertEquals(10, binomial(5, 3))
    }
}
