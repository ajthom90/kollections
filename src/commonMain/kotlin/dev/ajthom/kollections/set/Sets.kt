package dev.ajthom.kollections.set

import kotlin.jvm.JvmStatic

/**
 * Helpers for [Set], in the spirit of Guava's `Sets`.
 *
 * [intersection], [union], [difference] and [symmetricDifference] return eagerly computed sets that
 * keep the iteration order of their arguments. [powerSet], [combinations] and [cartesianProduct]
 * return lazy read-only views: they compute their members while they are iterated, so they can
 * describe far more members than could be held in memory at once.
 */
public object Sets {
    /**
     * The largest set [powerSet] accepts; the power set of 31 elements no longer fits in an [Int].
     */
    private const val MAX_POWER_SET_INPUT: Int = 30

    /**
     * Returns the elements held by both [left] and [right], in the order of [left].
     *
     * @param left the set whose order the result keeps.
     * @param right the set the elements of [left] are looked up in.
     */
    @JvmStatic
    public fun <T> intersection(left: Set<T>, right: Set<T>): Set<T> = left.intersect(right)

    /**
     * Returns the elements held by either [left] or [right], with the elements of [left] first.
     *
     * @param left the set whose elements come first.
     * @param right the set whose elements follow, minus the ones already in [left].
     */
    @JvmStatic
    public fun <T> union(left: Set<T>, right: Set<T>): Set<T> = left.union(right)

    /**
     * Returns the elements held by [left] but not by [right], in the order of [left].
     *
     * @param left the set to take elements from.
     * @param right the set of elements to leave out.
     */
    @JvmStatic
    public fun <T> difference(left: Set<T>, right: Set<T>): Set<T> = left.subtract(right)

    /**
     * Returns the elements held by exactly one of [left] and [right], those of [left] first.
     *
     * @param left the set whose exclusive elements come first.
     * @param right the set whose exclusive elements follow.
     */
    @JvmStatic
    public fun <T> symmetricDifference(left: Set<T>, right: Set<T>): Set<T> {
        val result = LinkedHashSet<T>()
        for (element in left) {
            if (element !in right) result.add(element)
        }
        for (element in right) {
            if (element !in left) result.add(element)
        }
        return result
    }

    /**
     * Returns a lazy read-only view of every subset of [set], including the empty set and [set].
     *
     * The view holds `2^n` members for a set of `n` elements. It is iterated in ascending bitmask
     * order over the elements of [set], so the empty set comes first and the full set comes last,
     * and its members are computed one at a time as they are asked for. A set is a member exactly
     * when [set] holds all of its elements.
     *
     * The elements of [set] are snapshotted when the view is created, so later changes to [set] do
     * not affect it.
     *
     * @param set the set whose subsets are described.
     * @throws IllegalArgumentException if [set] holds more than 30 elements, because the size of the
     * result would not fit in an [Int].
     */
    @JvmStatic
    public fun <T> powerSet(set: Set<T>): Set<Set<T>> = PowerSet(set)

    /**
     * Returns a lazy read-only view of every subset of [set] that holds exactly [k] elements.
     *
     * The view holds `n choose k` members for a set of `n` elements. It is iterated in lexicographic
     * order over the positions of the elements in [set], and its members are computed one at a time
     * as they are asked for. A [k] of `0` yields a single empty set; a [k] larger than the size of
     * [set] yields no members at all.
     *
     * The elements of [set] are snapshotted when the view is created, so later changes to [set] do
     * not affect it.
     *
     * @param set the set to choose elements from.
     * @param k how many elements each member holds.
     * @throws IllegalArgumentException if [k] is negative, or if the number of members would not fit
     * in an [Int].
     */
    @JvmStatic
    public fun <T> combinations(set: Set<T>, k: Int): Set<Set<T>> = Combinations(set, k)

    /**
     * Returns a lazy read-only view of every list that takes one element from each of [sets].
     *
     * Each member is a list as long as [sets], holding an element of `sets[0]` at index `0`, an
     * element of `sets[1]` at index `1`, and so on. The view holds as many members as the product of
     * the sizes of [sets]: it is iterated like an odometer, with the last set varying fastest, and
     * its members are computed one at a time as they are asked for. If any of [sets] is empty the
     * view is empty; if no set is given at all the view holds a single empty list.
     *
     * The elements of [sets] are snapshotted when the view is created, so later changes to them do
     * not affect it.
     *
     * @param sets the sets to draw each position of the resulting lists from, in order.
     * @throws IllegalArgumentException if the number of members would not fit in an [Int].
     */
    @JvmStatic
    public fun <T> cartesianProduct(vararg sets: Set<T>): Set<List<T>> = CartesianProduct(sets.toList())

    /**
     * Returns a lazy read-only view of every list that takes one element from each of [sets].
     *
     * Each member is a list as long as [sets], holding an element of `sets[0]` at index `0`, an
     * element of `sets[1]` at index `1`, and so on. The view holds as many members as the product of
     * the sizes of [sets]: it is iterated like an odometer, with the last set varying fastest, and
     * its members are computed one at a time as they are asked for. If any of [sets] is empty the
     * view is empty; if [sets] itself is empty the view holds a single empty list.
     *
     * The elements of [sets] are snapshotted when the view is created, so later changes to them do
     * not affect it.
     *
     * @param sets the sets to draw each position of the resulting lists from, in order.
     * @throws IllegalArgumentException if the number of members would not fit in an [Int].
     */
    @JvmStatic
    public fun <T> cartesianProduct(sets: List<Set<T>>): Set<List<T>> = CartesianProduct(sets)

    /** The lazy view returned by [powerSet]. */
    private class PowerSet<T>(input: Set<T>) : AbstractSet<Set<T>>() {
        private val elements: List<T> = input.toList()
        private val members: Set<T> = elements.toSet()

        init {
            require(elements.size <= MAX_POWER_SET_INPUT) {
                "power set input size must be at most $MAX_POWER_SET_INPUT but was ${elements.size}"
            }
        }

        override val size: Int get() = 1 shl elements.size

        override fun contains(element: Set<T>): Boolean = members.containsAll(element)

        override fun iterator(): Iterator<Set<T>> = (0 until size).asSequence().map(::subsetAt).iterator()

        /**
         * Returns `true` when [other] is a power set of an equal input set, without looking at
         * either view's members; any other set is compared member by member as usual.
         */
        override fun equals(other: Any?): Boolean =
            if (other is PowerSet<*>) members == other.members else super.equals(other)

        /**
         * Returns the sum of the hash codes of the members, as [Set] requires, computed from the
         * input set alone: every element occurs in exactly half of the `2^n` subsets, so the sum is
         * the hash code of the input shifted left by `n - 1`. The empty input has a single member,
         * the empty set, whose hash code is `0`.
         */
        override fun hashCode(): Int =
            if (elements.isEmpty()) members.hashCode() else members.hashCode() shl (elements.size - 1)

        /** Returns a description of this view, such as `powerSet([1, 2])`. */
        override fun toString(): String = "powerSet($members)"

        /** Returns the subset whose elements are the ones selected by the bits set in [mask]. */
        private fun subsetAt(mask: Int): Set<T> {
            val subset = LinkedHashSet<T>()
            for (index in elements.indices) {
                if ((mask shr index) and 1 == 1) subset.add(elements[index])
            }
            return subset
        }
    }

    /** The lazy view returned by [combinations]. */
    private class Combinations<T>(input: Set<T>, private val k: Int) : AbstractSet<Set<T>>() {
        private val elements: List<T> = input.toList()
        private val members: Set<T> = elements.toSet()

        init {
            require(k >= 0) { "k must not be negative but was $k" }
        }

        override val size: Int = binomial(elements.size, k)

        override fun contains(element: Set<T>): Boolean = element.size == k && members.containsAll(element)

        override fun iterator(): Iterator<Set<T>> = sequence<Set<T>> {
            if (k > elements.size) return@sequence
            val indices = IntArray(k) { it }
            while (true) {
                val subset = LinkedHashSet<T>()
                for (index in indices) subset.add(elements[index])
                yield(subset)
                if (!advance(indices)) break
            }
        }.iterator()

        /**
         * Moves [indices] to the next combination in lexicographic order, returning `false` when
         * [indices] already held the last one.
         */
        private fun advance(indices: IntArray): Boolean {
            val last = elements.size - indices.size
            var position = indices.size - 1
            while (position >= 0 && indices[position] == last + position) position--
            if (position < 0) return false
            indices[position]++
            for (next in position + 1 until indices.size) indices[next] = indices[next - 1] + 1
            return true
        }

        /**
         * Returns `true` when [other] chooses the same number of elements from an equal input set,
         * without looking at either view's members; any other set is compared member by member.
         *
         * The shortcut is taken only while both views are non-degenerate, because the input does
         * not determine the members otherwise: a `k` of `0` yields the single empty member and a
         * `k` above the input size yields none at all, whatever the input was.
         */
        override fun equals(other: Any?): Boolean =
            if (other is Combinations<*> && isNonDegenerate() && other.isNonDegenerate()) {
                k == other.k && members == other.members
            } else {
                super.equals(other)
            }

        /** Returns `true` while the members of this view determine, and are determined by, its input. */
        private fun isNonDegenerate(): Boolean = k in 1..elements.size

        /**
         * Returns the sum of the hash codes of the members, as [Set] requires, computed from the
         * input alone: every element occurs in `(n - 1) choose (k - 1)` of the members, so the sum
         * is that many times the hash code of the input set. The factor is `0` both when `k` is `0`
         * (the single member is the empty set) and when `k` exceeds `n` (there are no members).
         */
        override fun hashCode(): Int = binomial(elements.size - 1, k - 1) * members.hashCode()

        /** Returns a description of this view, such as `combinations([1, 2, 3], 2)`. */
        override fun toString(): String = "combinations($members, $k)"
    }

    /** The lazy view returned by [cartesianProduct]. */
    private class CartesianProduct<T>(sets: List<Set<T>>) : AbstractSet<List<T>>() {
        private val axes: List<List<T>> = sets.map { it.toList() }
        private val axisMembers: List<Set<T>> = axes.map { it.toSet() }

        override val size: Int = productOf(axes)

        override fun contains(element: List<T>): Boolean {
            if (element.size != axes.size) return false
            return axes.indices.all { element[it] in axisMembers[it] }
        }

        override fun iterator(): Iterator<List<T>> = (0 until size).asSequence().map(::tupleAt).iterator()

        /**
         * Returns the [index]th list in odometer order. Only called when the view is not empty, so
         * every axis is known to hold at least one element.
         */
        private fun tupleAt(index: Int): List<T> {
            var remaining = index
            val tuple = mutableListOf<T>()
            for (axis in axes.indices.reversed()) {
                val values = axes[axis]
                tuple.add(values[remaining % values.size])
                remaining /= values.size
            }
            tuple.reverse()
            return tuple
        }

        /**
         * Returns `true` when [other] draws from the same sets in the same order, without looking at
         * either view's members; any other set is compared member by member as usual.
         *
         * The shortcut is taken only while both views hold at least one member, because an empty
         * axis leaves the view empty whatever the other axes held.
         */
        override fun equals(other: Any?): Boolean = if (other is CartesianProduct<*> && size > 0 && other.size > 0) {
            axisMembers == other.axisMembers
        } else {
            super.equals(other)
        }

        /**
         * Returns the sum of the hash codes of the members, as [Set] requires, computed from the
         * axes alone. A member is a list, so it hashes to `31^m + sum(31^(m - 1 - i) * h(e_i))` for
         * `m` axes; each element of axis `i` occurs in `size / axes[i].size` members, which gives
         * the sum below. An empty view has no members and so hashes to `0`.
         */
        override fun hashCode(): Int {
            if (size == 0) return 0
            var result = size * powerOf31(axes.size)
            for (axis in axes.indices) {
                result += powerOf31(axes.size - 1 - axis) * (size / axes[axis].size) * axisMembers[axis].hashCode()
            }
            return result
        }

        /** Returns a description of this view, such as `cartesianProduct([[1, 2], [3, 4]])`. */
        override fun toString(): String = "cartesianProduct($axisMembers)"
    }
}

/**
 * Returns the number of ways to choose [choose] of [count] elements, which is `0` when [choose] is
 * negative or larger than [count].
 *
 * Internal rather than private so that it can be tested on its own; it is not part of the public
 * API.
 *
 * @throws IllegalArgumentException if the result does not fit in an [Int].
 */
internal fun binomial(count: Int, choose: Int): Int {
    if (choose < 0 || choose > count) return 0
    val steps = minOf(choose, count - choose)
    var result = 1L
    for (step in 1..steps) {
        result = result * (count - steps + step) / step
        require(result <= Int.MAX_VALUE) {
            "the number of ways to choose $choose of $count elements must fit in an Int but was $result"
        }
    }
    return result.toInt()
}

/**
 * Returns the product of the sizes of [axes], which is `1` when [axes] is empty.
 *
 * @throws IllegalArgumentException if the product does not fit in an [Int].
 */
private fun <T> productOf(axes: List<List<T>>): Int {
    var product = 1L
    for (axis in axes) {
        product *= axis.size
        require(product <= Int.MAX_VALUE) {
            "the size of the cartesian product of ${axes.map { it.size }} must fit in an Int but was $product"
        }
    }
    return product.toInt()
}

/** Returns `31` raised to [exponent], wrapping around like every other [Int] multiplication. */
private fun powerOf31(exponent: Int): Int {
    var result = 1
    repeat(exponent) { result *= 31 }
    return result
}
