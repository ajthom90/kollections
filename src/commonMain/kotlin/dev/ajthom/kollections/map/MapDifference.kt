package dev.ajthom.kollections.map

/**
 * The result of comparing two maps entry by entry, in the spirit of Guava's `MapDifference`.
 *
 * The four maps returned by an instance are snapshots taken when the difference was computed; they
 * never change afterwards, even if the compared maps do. Together they partition the keys of the two
 * maps: every key appears in exactly one of [entriesOnlyOnLeft], [entriesOnlyOnRight],
 * [entriesInCommon] and [entriesDiffering].
 *
 * @see Maps.difference
 * @see difference
 */
public interface MapDifference<K, V> {
    /** `true` when the compared maps hold exactly the same entries. */
    public val areEqual: Boolean

    /** The entries whose keys appear only in the left map. */
    public val entriesOnlyOnLeft: Map<K, V>

    /** The entries whose keys appear only in the right map. */
    public val entriesOnlyOnRight: Map<K, V>

    /** The entries that appear in both maps with equal values. */
    public val entriesInCommon: Map<K, V>

    /** The keys that appear in both maps bound to unequal values, with both values. */
    public val entriesDiffering: Map<K, ValueDifference<V>>

    /**
     * A pair of unequal values that the left and right maps bound to the same key.
     *
     * @param leftValue the value the left map bound to the key.
     * @param rightValue the value the right map bound to the key.
     */
    public data class ValueDifference<out V>(public val leftValue: V, public val rightValue: V)
}
