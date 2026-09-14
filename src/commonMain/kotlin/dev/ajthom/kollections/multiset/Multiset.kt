package dev.ajthom.kollections.multiset

/**
 * A collection that counts how often each of its elements occurs, also known as a bag.
 *
 * A multiset behaves like a [Collection] whose [size] is the total number of occurrences, while
 * [elementSet] exposes the distinct elements and [count] the multiplicity of a single element. No
 * element is ever held with a count of `0`: an element with no occurrences is simply absent.
 *
 * Two multisets are equal when they hold the same elements with the same counts, regardless of the
 * order in which those elements were added and regardless of the implementation class.
 *
 * @param E the type of the elements held by this multiset.
 * @see MutableMultiset for the mutable counterpart.
 * @see LinkedHashMultiset for the default implementation.
 */
public interface Multiset<E> : Collection<E> {

    /** The total number of occurrences of all elements, so `2` for a multiset holding `a` twice. */
    override val size: Int

    /**
     * The distinct elements of this multiset, each appearing exactly once.
     *
     * Implementations that are mutable document whether this set is a live view or a snapshot.
     */
    public val elementSet: Set<E>

    /**
     * One [Entry] per distinct element, pairing the element with its count.
     *
     * Implementations that are mutable document whether this set is a live view or a snapshot.
     */
    public val entries: Set<Entry<E>>

    /**
     * Returns how often [element] occurs in this multiset, or `0` when it is not present.
     *
     * @param element the element to count.
     */
    public fun count(element: E): Int

    /**
     * A distinct element of a multiset together with the number of times it occurs.
     *
     * @param E the type of the element.
     * @property element the distinct element.
     * @property count the number of occurrences of [element]; always greater than `0` for entries
     * obtained from a multiset.
     */
    public data class Entry<out E>(public val element: E, public val count: Int)
}
