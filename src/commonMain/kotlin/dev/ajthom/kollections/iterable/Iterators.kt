package dev.ajthom.kollections.iterable

import kotlin.jvm.JvmStatic

/**
 * Helpers for [Iterator], in the spirit of Guava's `Iterators`.
 *
 * Every helper advances the iterator it is given as little as possible and never assumes the
 * iterator can be traversed more than once, so these functions are safe to use with the
 * single-pass iterators produced by sequences.
 */
public object Iterators {
    private const val NO_ELEMENTS: String = "expected at least one element but there were none"
    private const val NONE_FOR_ONLY: String = "expected exactly one element but there were none"
    private const val TOO_MANY_FOR_ONLY: String = "expected exactly one element but there was more than one"

    /**
     * Returns the first element of [iter], consuming exactly one element.
     *
     * @param iter the iterator to read from.
     * @throws NoSuchElementException if [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getFirst(iter: Iterator<T>): T {
        if (!iter.hasNext()) throw NoSuchElementException(NO_ELEMENTS)
        return iter.next()
    }

    /**
     * Returns the first element of [iter], or [default] when [iter] is empty.
     *
     * Emptiness is decided with [Iterator.hasNext], so a first element that is itself `null` is
     * returned as `null` rather than being replaced by [default].
     *
     * @param iter the iterator to read from.
     * @param default the value to return when [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getFirst(iter: Iterator<T>, default: T): T {
        if (!iter.hasNext()) return default
        return iter.next()
    }

    /**
     * Returns the last element of [iter], exhausting the iterator.
     *
     * @param iter the iterator to read from.
     * @throws NoSuchElementException if [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getLast(iter: Iterator<T>): T {
        if (!iter.hasNext()) throw NoSuchElementException(NO_ELEMENTS)
        return lastOf(iter)
    }

    /**
     * Returns the last element of [iter], or [default] when [iter] is empty, exhausting the iterator.
     *
     * Emptiness is decided with [Iterator.hasNext], so a last element that is itself `null` is
     * returned as `null` rather than being replaced by [default].
     *
     * @param iter the iterator to read from.
     * @param default the value to return when [iter] has no elements.
     */
    @JvmStatic
    public fun <T> getLast(iter: Iterator<T>, default: T): T {
        if (!iter.hasNext()) return default
        return lastOf(iter)
    }

    /**
     * Returns the single element of [iter].
     *
     * @param iter the iterator to read from.
     * @throws NoSuchElementException if [iter] has no elements.
     * @throws IllegalArgumentException if [iter] has more than one element.
     */
    @JvmStatic
    public fun <T> only(iter: Iterator<T>): T {
        if (!iter.hasNext()) throw NoSuchElementException(NONE_FOR_ONLY)
        return onlyOf(iter)
    }

    /**
     * Returns the single element of [iter], or [default] when [iter] is empty.
     *
     * Emptiness is decided with [Iterator.hasNext], so a single element that is itself `null` is
     * returned as `null` rather than being replaced by [default].
     *
     * @param iter the iterator to read from.
     * @param default the value to return when [iter] has no elements.
     * @throws IllegalArgumentException if [iter] has more than one element.
     */
    @JvmStatic
    public fun <T> only(iter: Iterator<T>, default: T): T {
        if (!iter.hasNext()) return default
        return onlyOf(iter)
    }

    /** Reads every remaining element of a non-empty [iter] and returns the last one. */
    private fun <T> lastOf(iter: Iterator<T>): T {
        var last = iter.next()
        while (iter.hasNext()) {
            last = iter.next()
        }
        return last
    }

    /** Reads the single remaining element of a non-empty [iter], failing if there are more. */
    private fun <T> onlyOf(iter: Iterator<T>): T {
        val value = iter.next()
        require(!iter.hasNext()) { TOO_MANY_FOR_ONLY }
        return value
    }
}
