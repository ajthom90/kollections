package dev.ajthom.kollections.set

import kotlin.jvm.JvmStatic

/**
 * Factories for read-only sets, for those familiar with Guava's `ImmutableSet` on the JVM.
 *
 * These factories return the stdlib's read-only [Set], which cannot be modified through the [Set]
 * interface but is not deeply immutable: the elements it holds are not copied.
 */
public object ImmutableSet {
    /**
     * Returns an empty read-only set.
     */
    @JvmStatic
    public fun <T> of(): Set<T> = emptySet()

    /**
     * Returns a read-only set holding only [item].
     *
     * @param item the single element of the result.
     */
    @JvmStatic
    public fun <T> of(item: T): Set<T> = setOf(item)

    /**
     * Returns a read-only set holding the given items, in the order they were given.
     *
     * Repeated items are held once, as in [setOf].
     *
     * @param item the elements of the result.
     */
    @JvmStatic
    public fun <T> of(vararg item: T): Set<T> = setOf(*item)

    /**
     * Returns a read-only set holding the elements of [iterable], in iteration order.
     *
     * Repeated elements are held once. The copy does not see later changes to [iterable].
     *
     * @param iterable the source of the elements to copy.
     */
    @JvmStatic
    public fun <T> copyOf(iterable: Iterable<T>): Set<T> = iterable.toSet()
}
