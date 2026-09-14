package dev.ajthom.kollections.list

import kotlin.jvm.JvmStatic

/**
 * Factories for read-only lists, for those familiar with Guava's `ImmutableList` on the JVM.
 *
 * These factories return the stdlib's read-only [List], which cannot be modified through the [List]
 * interface but is not deeply immutable: the elements it holds are not copied.
 */
public object ImmutableList {
    /**
     * Returns an empty read-only list.
     */
    @JvmStatic
    public fun <T> of(): List<T> = emptyList()

    /**
     * Returns a read-only list holding only [item].
     *
     * @param item the single element of the result.
     */
    @JvmStatic
    public fun <T> of(item: T): List<T> = listOf(item)

    /**
     * Returns a read-only list holding the given items, in the order they were given.
     *
     * @param item the elements of the result.
     */
    @JvmStatic
    public fun <T> of(vararg item: T): List<T> = listOf(*item)

    /**
     * Returns a read-only list holding the elements of [iterable], in iteration order.
     *
     * The copy does not see later changes to [iterable].
     *
     * @param iterable the source of the elements to copy.
     */
    @JvmStatic
    public fun <T> copyOf(iterable: Iterable<T>): List<T> = iterable.toList()
}
