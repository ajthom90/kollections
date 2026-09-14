package dev.ajthom.kollections.iterable

import kotlin.jvm.JvmStatic

/**
 * Helpers for [Iterable], in the spirit of Guava's `Iterables`.
 *
 * Every helper obtains an iterator from its argument exactly once and advances it as little as
 * possible, so these functions are safe to use with single-pass iterables such as the one returned
 * by [Sequence.asIterable].
 */
public object Iterables {
    /**
     * Returns the first element of [iter].
     *
     * @param iter the iterable to read from; its iterator is requested once.
     * @throws NoSuchElementException if [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getFirst(iter: Iterable<T>): T = Iterators.getFirst(iter.iterator())

    /**
     * Returns the first element of [iter], or [default] when [iter] is empty.
     *
     * Emptiness is decided with [Iterator.hasNext], so a first element that is itself `null` is
     * returned as `null` rather than being replaced by [default].
     *
     * @param iter the iterable to read from; its iterator is requested once.
     * @param default the value to return when [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getFirst(iter: Iterable<T>, default: T): T = Iterators.getFirst(iter.iterator(), default)

    /**
     * Returns the last element of [iter].
     *
     * @param iter the iterable to read from; its iterator is requested once and fully consumed.
     * @throws NoSuchElementException if [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getLast(iter: Iterable<T>): T = Iterators.getLast(iter.iterator())

    /**
     * Returns the last element of [iter], or [default] when [iter] is empty.
     *
     * Emptiness is decided with [List.isEmpty] or [Iterator.hasNext], so a last element that is
     * itself `null` is returned as `null` rather than being replaced by [default].
     *
     * @param iter the iterable to read from; a [List] is indexed instead of iterated, and any other
     * iterable has its iterator requested once and fully consumed.
     * @param default the value to return when [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getLast(iter: Iterable<T>, default: T): T {
        if (iter is List<T>) return if (iter.isEmpty()) default else iter[iter.size - 1]
        return Iterators.getLast(iter.iterator(), default)
    }

    /**
     * Returns the single element of [iter].
     *
     * @param iter the iterable to read from; its iterator is requested once.
     * @throws NoSuchElementException if [iter] has no elements.
     * @throws IllegalArgumentException if [iter] has more than one element.
     */
    @JvmStatic
    public fun <T> only(iter: Iterable<T>): T = Iterators.only(iter.iterator())

    /**
     * Returns the single element of [iter], or [default] when [iter] is empty.
     *
     * Emptiness is decided with [Iterator.hasNext], so a single element that is itself `null` is
     * returned as `null` rather than being replaced by [default].
     *
     * @param iter the iterable to read from; its iterator is requested once.
     * @param default the value to return when [iter] has no elements.
     * @throws IllegalArgumentException if [iter] has more than one element.
     */
    @JvmStatic
    public fun <T> only(iter: Iterable<T>, default: T): T = Iterators.only(iter.iterator(), default)
}
