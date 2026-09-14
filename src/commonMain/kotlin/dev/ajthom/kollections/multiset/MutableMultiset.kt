package dev.ajthom.kollections.multiset

/**
 * A [Multiset] that supports adding and removing occurrences of its elements.
 *
 * All mutating operations keep the invariant that an element is never held with a count of `0`:
 * dropping the last occurrence of an element removes it from [elementSet] and [entries] as well.
 *
 * @param E the type of the elements held by this multiset.
 * @see LinkedHashMultiset for the default implementation.
 */
public interface MutableMultiset<E> :
    Multiset<E>,
    MutableCollection<E> {

    /**
     * The distinct elements of this multiset, as a live view: it reflects later changes to the
     * multiset, and removing an element from it removes *all* of that element's occurrences.
     *
     * The view does not support adding elements; use [add] instead.
     */
    override val elementSet: MutableSet<E>

    /**
     * One [Multiset.Entry] per distinct element, as a live *read-only* view: it reflects later
     * changes to this multiset, but nothing can be changed through it.
     *
     * Change the counts with [add], [remove] or [setCount] instead.
     */
    override val entries: Set<Multiset.Entry<E>>

    /**
     * Adds [occurrences] further occurrences of [element] and returns the count before the call.
     *
     * Adding `0` occurrences changes nothing and only reports the current count.
     *
     * @param element the element to add occurrences of.
     * @param occurrences how many occurrences to add; must not be negative.
     * @return the number of occurrences of [element] before this call.
     * @throws IllegalArgumentException if [occurrences] is negative.
     */
    public fun add(element: E, occurrences: Int): Int

    /**
     * Removes up to [occurrences] occurrences of [element] and returns the count before the call.
     *
     * Removing more occurrences than are present removes all of them rather than failing; removing
     * `0` occurrences changes nothing and only reports the current count.
     *
     * @param element the element to remove occurrences of.
     * @param occurrences how many occurrences to remove; must not be negative.
     * @return the number of occurrences of [element] before this call.
     * @throws IllegalArgumentException if [occurrences] is negative.
     */
    public fun remove(element: E, occurrences: Int): Int

    /**
     * Sets the number of occurrences of [element] to [count] and returns the count before the call.
     *
     * Setting a count of `0` removes the element entirely.
     *
     * @param element the element whose count to set.
     * @param count the new number of occurrences; must not be negative.
     * @return the number of occurrences of [element] before this call.
     * @throws IllegalArgumentException if [count] is negative.
     */
    public fun setCount(element: E, count: Int): Int

    /**
     * Sets the number of occurrences of [element] to [newCount], but only if it is currently
     * [oldCount].
     *
     * @param element the element whose count to set.
     * @param oldCount the count the element is expected to have; must not be negative.
     * @param newCount the new number of occurrences; must not be negative.
     * @return `true` if the count was changed to [newCount], or `false` if the current count is not
     * [oldCount], in which case the multiset is left untouched.
     * @throws IllegalArgumentException if [oldCount] or [newCount] is negative.
     */
    public fun setCount(element: E, oldCount: Int, newCount: Int): Boolean
}
