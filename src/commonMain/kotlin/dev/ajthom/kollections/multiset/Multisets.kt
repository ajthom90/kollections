package dev.ajthom.kollections.multiset

/**
 * Returns an empty read-only multiset.
 *
 * The returned multiset is immutable and shared, and it is equal to every other empty multiset.
 * Unlike [multisetOf] and [toMultiset] it is not backed by a mutable class, but the read-only
 * [Multiset] type is a contract rather than a guarantee either way, so callers must not cast the
 * result to a [MutableMultiset].
 *
 * @param E the type of the elements of the (empty) multiset.
 */
@Suppress("UNCHECKED_CAST")
public fun <E> emptyMultiset(): Multiset<E> = EmptyMultiset as Multiset<E>

/**
 * Returns a read-only multiset holding [elements], counting duplicates.
 *
 * Like the standard library's [listOf], a non-empty result is backed by a mutable class: the
 * read-only [Multiset] type is a contract rather than a guarantee, so callers must not cast the
 * result to a [MutableMultiset] in order to change it.
 *
 * @param E the type of the elements.
 * @param elements the elements of the multiset; repeated elements raise their count.
 */
public fun <E> multisetOf(vararg elements: E): Multiset<E> =
    if (elements.isEmpty()) emptyMultiset() else LinkedHashMultiset(elements.asList())

/**
 * Returns a new [MutableMultiset] holding [elements], counting duplicates.
 *
 * @param E the type of the elements.
 * @param elements the initial elements of the multiset; repeated elements raise their count.
 */
public fun <E> mutableMultisetOf(vararg elements: E): MutableMultiset<E> = LinkedHashMultiset(elements.asList())

/**
 * Returns a read-only multiset holding the elements of this iterable, counting duplicates.
 *
 * The iterable is iterated once and the result is a copy: later changes to it are not reflected.
 *
 * Like the standard library's [listOf], the returned instance is backed by a mutable class: the
 * read-only [Multiset] type is a contract rather than a guarantee, so callers must not cast the
 * result to a [MutableMultiset] in order to change it.
 *
 * @param E the type of the elements.
 */
public fun <E> Iterable<E>.toMultiset(): Multiset<E> = LinkedHashMultiset(this)

/**
 * Returns a new [MutableMultiset] holding the elements of this iterable, counting duplicates.
 *
 * The iterable is iterated once and the result is a copy: later changes to it are not reflected.
 *
 * @param E the type of the elements.
 */
public fun <E> Iterable<E>.toMutableMultiset(): MutableMultiset<E> = LinkedHashMultiset(this)

/**
 * Returns a read-only multiset that holds every key of this map as many times as its value says.
 *
 * Keys mapped to `0` are skipped, so the result never holds an element with a count of `0`.
 *
 * Like the standard library's [listOf], the returned instance is backed by a mutable class: the
 * read-only [Multiset] type is a contract rather than a guarantee, so callers must not cast the
 * result to a [MutableMultiset] in order to change it.
 *
 * @param E the type of the elements, that is, of the keys of this map.
 * @throws IllegalArgumentException if any value is negative.
 */
public fun <E> Map<E, Int>.toMultiset(): Multiset<E> {
    val multiset = LinkedHashMultiset<E>()
    for ((element, count) in this) {
        require(count >= 0) { "count for $element must not be negative but was $count" }
        if (count > 0) {
            multiset.setCount(element, count)
        }
    }
    return multiset
}

/**
 * The shared empty multiset returned by [emptyMultiset].
 *
 * The element type is `Any?` rather than `Nothing` so that the cast in [emptyMultiset] leaves the
 * members callable with an element of any type.
 */
private object EmptyMultiset : Multiset<Any?> {
    override val size: Int get() = 0
    override val elementSet: Set<Any?> get() = emptySet()
    override val entries: Set<Multiset.Entry<Any?>> get() = emptySet()

    override fun count(element: Any?): Int = 0
    override fun contains(element: Any?): Boolean = false
    override fun containsAll(elements: Collection<Any?>): Boolean = elements.isEmpty()
    override fun isEmpty(): Boolean = true
    override fun iterator(): Iterator<Any?> = emptyList<Any?>().iterator()

    override fun equals(other: Any?): Boolean = other is Multiset<*> && other.isEmpty()
    override fun hashCode(): Int = 0
    override fun toString(): String = "[]"
}
