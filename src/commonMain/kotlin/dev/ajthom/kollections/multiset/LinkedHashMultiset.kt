package dev.ajthom.kollections.multiset

/**
 * A [MutableMultiset] backed by a [LinkedHashMap] from element to count, keeping the distinct
 * elements in the order in which they were first added.
 *
 * The iterator yields each element as many times as it occurs, and its `remove()` takes away a
 * single occurrence. No element is ever held with a count of `0`.
 *
 * This implementation is not thread-safe and, like the standard collections, makes no attempt to
 * detect concurrent modification.
 *
 * @param E the type of the elements held by this multiset.
 * @constructor Creates an empty multiset.
 */
public class LinkedHashMultiset<E> public constructor() :
    AbstractMutableCollection<E>(),
    MutableMultiset<E> {

    private val counts: LinkedHashMap<E, Int> = LinkedHashMap()

    private var total: Int = 0

    private val elementSetView: ElementSet = ElementSet()

    /**
     * Creates a multiset holding every element of [elements], counting duplicates.
     *
     * @param elements the elements to add; iterated once.
     */
    public constructor(elements: Iterable<E>) : this() {
        addAll(elements)
    }

    /**
     * The total number of occurrences of all elements.
     *
     * Kept up to date by every mutator, including the ones on [elementSet] and on the iterators, so
     * reading it is `O(1)`.
     */
    override val size: Int
        get() = total

    /**
     * The distinct elements of this multiset, as a live view: it reflects later changes to this
     * multiset, and changes made through it are reflected here.
     *
     * Removing an element removes all of its occurrences and shrinks [size] accordingly; adding is
     * not supported.
     */
    override val elementSet: MutableSet<E>
        get() = elementSetView

    /**
     * One [Multiset.Entry] per distinct element, in the order the elements were first added, as a
     * live read-only view: it reflects later changes to this multiset.
     */
    override val entries: Set<Multiset.Entry<E>>
        get() = EntrySet()

    /** Returns `true` if this multiset holds no element at all. */
    override fun isEmpty(): Boolean = counts.isEmpty()

    /** Returns how often [element] occurs, or `0` when it is not present. */
    override fun count(element: E): Int = counts[element] ?: 0

    /** Returns `true` if [element] occurs at least once. */
    override fun contains(element: E): Boolean = counts.containsKey(element)

    /** Returns `true` if every element of [elements] occurs at least once, ignoring multiplicity. */
    override fun containsAll(elements: Collection<E>): Boolean = counts.keys.containsAll(elements)

    /** Adds a single occurrence of [element] and always returns `true`. */
    override fun add(element: E): Boolean {
        add(element, 1)
        return true
    }

    /**
     * Adds [occurrences] further occurrences of [element], or nothing at all when [occurrences] is
     * `0`, and returns the count before the call.
     *
     * @throws IllegalArgumentException if [occurrences] is negative, or if the resulting count would
     * not fit in an [Int].
     */
    override fun add(element: E, occurrences: Int): Int {
        requireNonNegative(occurrences, "occurrences")
        val previous = counts[element] ?: 0
        require(occurrences <= Int.MAX_VALUE - previous) { "count would overflow: $previous + $occurrences" }
        if (occurrences > 0) {
            counts[element] = previous + occurrences
            total += occurrences
        }
        return previous
    }

    /**
     * Removes a single occurrence of [element].
     *
     * @return `true` if an occurrence was removed, or `false` if [element] was not present.
     */
    override fun remove(element: E): Boolean = remove(element, 1) > 0

    /**
     * Removes up to [occurrences] occurrences of [element], dropping it entirely once its count
     * reaches `0`, and returns the count before the call.
     *
     * @throws IllegalArgumentException if [occurrences] is negative.
     */
    override fun remove(element: E, occurrences: Int): Int {
        requireNonNegative(occurrences, "occurrences")
        val previous = counts[element] ?: 0
        if (occurrences > 0 && previous > 0) {
            if (occurrences >= previous) {
                counts.remove(element)
                total -= previous
            } else {
                counts[element] = previous - occurrences
                total -= occurrences
            }
        }
        return previous
    }

    /**
     * Removes *every* occurrence of each element of [elements], however often it occurs there.
     *
     * @return `true` if this multiset changed.
     */
    override fun removeAll(elements: Collection<E>): Boolean {
        var modified = false
        for (element in elements) {
            val previous = counts.remove(element)
            if (previous != null) {
                total -= previous
                modified = true
            }
        }
        return modified
    }

    /**
     * Keeps every occurrence of the elements contained in [elements] and drops all other elements
     * entirely, whatever their counts.
     *
     * @return `true` if this multiset changed.
     */
    override fun retainAll(elements: Collection<E>): Boolean {
        var modified = false
        val iterator = counts.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!elements.contains(entry.key)) {
                total -= entry.value
                iterator.remove()
                modified = true
            }
        }
        return modified
    }

    /** Removes every element and every occurrence, leaving the multiset empty. */
    override fun clear() {
        counts.clear()
        total = 0
    }

    /**
     * Sets the number of occurrences of [element] to [count], removing it when [count] is `0`, and
     * returns the count before the call.
     *
     * @throws IllegalArgumentException if [count] is negative.
     */
    override fun setCount(element: E, count: Int): Int {
        requireNonNegative(count, "count")
        val previous = counts[element] ?: 0
        if (count == 0) {
            counts.remove(element)
        } else {
            counts[element] = count
        }
        total += count - previous
        return previous
    }

    /**
     * Sets the number of occurrences of [element] to [newCount] if it is currently [oldCount].
     *
     * @return `true` if the count was changed, or `false` if the multiset was left untouched.
     * @throws IllegalArgumentException if [oldCount] or [newCount] is negative.
     */
    override fun setCount(element: E, oldCount: Int, newCount: Int): Boolean {
        requireNonNegative(oldCount, "oldCount")
        requireNonNegative(newCount, "newCount")
        if ((counts[element] ?: 0) != oldCount) {
            return false
        }
        setCount(element, newCount)
        return true
    }

    /**
     * Returns an iterator that yields each element as many times as it occurs, in the order the
     * elements were first added.
     *
     * Its `remove()` takes away a single occurrence of the element just returned, and drops the
     * element altogether when that was its last occurrence.
     */
    override fun iterator(): MutableIterator<E> = ElementIterator()

    /**
     * Returns `true` if [other] is a [Multiset] holding the same elements with the same counts,
     * whatever its implementation and whatever the order of its elements.
     */
    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is Multiset<*>) {
            return false
        }
        val otherCounts = HashMap<Any?, Int>()
        for (entry in other.entries) {
            otherCounts[entry.element] = entry.count
        }
        if (otherCounts.size != counts.size) {
            return false
        }
        for ((element, count) in counts) {
            if (otherCounts[element] != count) {
                return false
            }
        }
        return true
    }

    /** Returns the sum over the entries of `element.hashCode() xor count`. */
    override fun hashCode(): Int {
        var result = 0
        for ((element, count) in counts) {
            result += element.hashCode() xor count
        }
        return result
    }

    /** Returns a string such as `[a x 2, b]`, showing a count only for elements occurring twice or more. */
    override fun toString(): String =
        counts.entries.joinToString(separator = ", ", prefix = "[", postfix = "]") { (element, count) ->
            if (count == 1) element.toString() else "$element x $count"
        }

    private fun requireNonNegative(value: Int, name: String) {
        require(value >= 0) { "$name must not be negative but was $value" }
    }

    private inner class ElementIterator : MutableIterator<E> {
        private val entryIterator = counts.entries.iterator()
        private var current: MutableMap.MutableEntry<E, Int>? = null
        private var remaining = 0
        private var canRemove = false

        override fun hasNext(): Boolean = remaining > 0 || entryIterator.hasNext()

        override fun next(): E {
            if (remaining == 0) {
                val entry = entryIterator.next()
                current = entry
                remaining = entry.value
            }
            remaining--
            canRemove = true
            return checkNotNull(current).key
        }

        override fun remove() {
            check(canRemove) { "next() must be called before remove(), and only one remove() per next()" }
            val entry = checkNotNull(current)
            val count = entry.value
            if (count <= 1) {
                entryIterator.remove()
            } else {
                entry.setValue(count - 1)
            }
            total--
            canRemove = false
        }
    }

    /**
     * The live view returned by [elementSet]. Every removal path subtracts the removed element's
     * count from [total], so [size] stays correct; `removeAll` and `retainAll` inherit their
     * implementations, which go through [remove] or this iterator.
     */
    private inner class ElementSet : AbstractMutableSet<E>() {
        override val size: Int
            get() = counts.size

        override fun isEmpty(): Boolean = counts.isEmpty()

        override fun contains(element: E): Boolean = counts.containsKey(element)

        override fun add(element: E): Boolean =
            throw UnsupportedOperationException("adding an element to a multiset element set is not supported")

        override fun remove(element: E): Boolean {
            val previous = counts.remove(element) ?: return false
            total -= previous
            return true
        }

        override fun clear() {
            this@LinkedHashMultiset.clear()
        }

        override fun iterator(): MutableIterator<E> = ElementSetIterator()
    }

    /** The iterator of [ElementSet]; its `remove()` drops every occurrence of the element. */
    private inner class ElementSetIterator : MutableIterator<E> {
        private val entryIterator = counts.entries.iterator()
        private var lastCount = 0
        private var canRemove = false

        override fun hasNext(): Boolean = entryIterator.hasNext()

        override fun next(): E {
            val entry = entryIterator.next()
            lastCount = entry.value
            canRemove = true
            return entry.key
        }

        override fun remove() {
            check(canRemove) { "next() must be called before remove(), and only one remove() per next()" }
            entryIterator.remove()
            total -= lastCount
            lastCount = 0
            canRemove = false
        }
    }

    private inner class EntrySet : AbstractSet<Multiset.Entry<E>>() {
        override val size: Int
            get() = counts.size

        override fun isEmpty(): Boolean = counts.isEmpty()

        override fun contains(element: Multiset.Entry<E>): Boolean =
            element.count > 0 && counts[element.element] == element.count

        override fun iterator(): Iterator<Multiset.Entry<E>> {
            val entryIterator = counts.entries.iterator()
            return object : Iterator<Multiset.Entry<E>> {
                override fun hasNext(): Boolean = entryIterator.hasNext()

                override fun next(): Multiset.Entry<E> {
                    val entry = entryIterator.next()
                    return Multiset.Entry(entry.key, entry.value)
                }
            }
        }
    }
}
