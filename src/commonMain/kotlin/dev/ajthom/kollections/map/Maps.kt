package dev.ajthom.kollections.map

import kotlin.jvm.JvmStatic

/**
 * Helpers for [Map], in the spirit of Guava's `Maps`.
 */
public object Maps {
    /**
     * Compares [left] and [right] entry by entry.
     *
     * The returned [MapDifference] is a snapshot: it keeps copies of the entries it reports, so
     * later changes to [left] or [right] do not affect it.
     *
     * @param left the map whose entries are reported as "left".
     * @param right the map whose entries are reported as "right".
     */
    @JvmStatic
    public fun <K, V> difference(left: Map<K, V>, right: Map<K, V>): MapDifference<K, V> {
        val onlyOnLeft = LinkedHashMap<K, V>()
        val inCommon = LinkedHashMap<K, V>()
        val differing = LinkedHashMap<K, MapDifference.ValueDifference<V>>()
        for ((key, leftValue) in left) {
            if (!right.containsKey(key)) {
                onlyOnLeft[key] = leftValue
                continue
            }
            val rightValue = right.getValue(key)
            if (leftValue == rightValue) {
                inCommon[key] = leftValue
            } else {
                differing[key] = MapDifference.ValueDifference(leftValue, rightValue)
            }
        }
        val onlyOnRight = LinkedHashMap<K, V>()
        for ((key, rightValue) in right) {
            if (!left.containsKey(key)) {
                onlyOnRight[key] = rightValue
            }
        }
        return SnapshotMapDifference(onlyOnLeft, onlyOnRight, inCommon, differing)
    }
}

/**
 * Returns this map, or an empty read-only map when this map is `null`.
 *
 * The Kotlin standard library provides the same function, so this copy adds nothing; it is kept
 * only so that 1.x code keeps compiling.
 */
@Deprecated(
    "The Kotlin standard library provides the same orEmpty(); this copy will be removed in 3.0.",
    replaceWith = ReplaceWith("orEmpty()", "kotlin.collections.orEmpty"),
    level = DeprecationLevel.WARNING,
)
public fun <K, V> Map<K, V>?.orEmpty(): Map<K, V> = this ?: emptyMap()

/**
 * Compares this map with [other] entry by entry, treating this map as the left one.
 *
 * @param other the map to compare with, reported as the right one.
 * @see Maps.difference
 */
public fun <K, V> Map<K, V>.difference(other: Map<K, V>): MapDifference<K, V> = Maps.difference(this, other)

/**
 * Factories for read-only maps, for those familiar with Guava's `ImmutableMap` on the JVM.
 *
 * These factories return the stdlib's read-only [Map], which cannot be modified through the [Map]
 * interface but is not deeply immutable: the keys and values they hold are not copied.
 */
public object ImmutableMap {
    /**
     * Returns an empty read-only map.
     */
    @JvmStatic
    public fun <K, V> of(): Map<K, V> = emptyMap()

    /**
     * Returns a read-only map holding the given one key-value pair.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(key1: K, value1: V): Map<K, V> = mapOf(key1 to value1)

    /**
     * Returns a read-only map holding the given two key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(key1: K, value1: V, key2: K, value2: V): Map<K, V> = mapOf(key1 to value1, key2 to value2)

    /**
     * Returns a read-only map holding the given three key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(key1: K, value1: V, key2: K, value2: V, key3: K, value3: V): Map<K, V> =
        mapOf(key1 to value1, key2 to value2, key3 to value3)

    /**
     * Returns a read-only map holding the given four key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(key1: K, value1: V, key2: K, value2: V, key3: K, value3: V, key4: K, value4: V): Map<K, V> =
        mapOf(
            key1 to value1,
            key2 to value2,
            key3 to value3,
            key4 to value4,
        )

    /**
     * Returns a read-only map holding the given five key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(
        key1: K,
        value1: V,
        key2: K,
        value2: V,
        key3: K,
        value3: V,
        key4: K,
        value4: V,
        key5: K,
        value5: V,
    ): Map<K, V> = mapOf(
        key1 to value1,
        key2 to value2,
        key3 to value3,
        key4 to value4,
        key5 to value5,
    )

    /**
     * Returns a read-only map holding the given six key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(
        key1: K,
        value1: V,
        key2: K,
        value2: V,
        key3: K,
        value3: V,
        key4: K,
        value4: V,
        key5: K,
        value5: V,
        key6: K,
        value6: V,
    ): Map<K, V> = mapOf(
        key1 to value1,
        key2 to value2,
        key3 to value3,
        key4 to value4,
        key5 to value5,
        key6 to value6,
    )

    /**
     * Returns a read-only map holding the given seven key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(
        key1: K,
        value1: V,
        key2: K,
        value2: V,
        key3: K,
        value3: V,
        key4: K,
        value4: V,
        key5: K,
        value5: V,
        key6: K,
        value6: V,
        key7: K,
        value7: V,
    ): Map<K, V> = mapOf(
        key1 to value1,
        key2 to value2,
        key3 to value3,
        key4 to value4,
        key5 to value5,
        key6 to value6,
        key7 to value7,
    )

    /**
     * Returns a read-only map holding the given eight key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(
        key1: K,
        value1: V,
        key2: K,
        value2: V,
        key3: K,
        value3: V,
        key4: K,
        value4: V,
        key5: K,
        value5: V,
        key6: K,
        value6: V,
        key7: K,
        value7: V,
        key8: K,
        value8: V,
    ): Map<K, V> = mapOf(
        key1 to value1,
        key2 to value2,
        key3 to value3,
        key4 to value4,
        key5 to value5,
        key6 to value6,
        key7 to value7,
        key8 to value8,
    )

    /**
     * Returns a read-only map holding the given nine key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(
        key1: K,
        value1: V,
        key2: K,
        value2: V,
        key3: K,
        value3: V,
        key4: K,
        value4: V,
        key5: K,
        value5: V,
        key6: K,
        value6: V,
        key7: K,
        value7: V,
        key8: K,
        value8: V,
        key9: K,
        value9: V,
    ): Map<K, V> = mapOf(
        key1 to value1,
        key2 to value2,
        key3 to value3,
        key4 to value4,
        key5 to value5,
        key6 to value6,
        key7 to value7,
        key8 to value8,
        key9 to value9,
    )

    /**
     * Returns a read-only map holding the given ten key-value pairs.
     *
     * If a key is repeated, the last value given for it wins, as in [mapOf].
     */
    @JvmStatic
    public fun <K, V> of(
        key1: K,
        value1: V,
        key2: K,
        value2: V,
        key3: K,
        value3: V,
        key4: K,
        value4: V,
        key5: K,
        value5: V,
        key6: K,
        value6: V,
        key7: K,
        value7: V,
        key8: K,
        value8: V,
        key9: K,
        value9: V,
        key10: K,
        value10: V,
    ): Map<K, V> = mapOf(
        key1 to value1,
        key2 to value2,
        key3 to value3,
        key4 to value4,
        key5 to value5,
        key6 to value6,
        key7 to value7,
        key8 to value8,
        key9 to value9,
        key10 to value10,
    )

    /**
     * Returns a read-only copy of [map].
     *
     * The copy keeps the iteration order of [map] and does not see later changes to it.
     */
    @JvmStatic
    public fun <K, V> copyOf(map: Map<K, V>): Map<K, V> = map.toMap()
}

/** The snapshot [MapDifference] returned by [Maps.difference]. */
private class SnapshotMapDifference<K, V>(
    override val entriesOnlyOnLeft: Map<K, V>,
    override val entriesOnlyOnRight: Map<K, V>,
    override val entriesInCommon: Map<K, V>,
    override val entriesDiffering: Map<K, MapDifference.ValueDifference<V>>,
) : MapDifference<K, V> {
    override val areEqual: Boolean
        get() = entriesOnlyOnLeft.isEmpty() && entriesOnlyOnRight.isEmpty() && entriesDiffering.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapDifference<*, *>) return false
        return entriesOnlyOnLeft == other.entriesOnlyOnLeft &&
            entriesOnlyOnRight == other.entriesOnlyOnRight &&
            entriesInCommon == other.entriesInCommon &&
            entriesDiffering == other.entriesDiffering
    }

    override fun hashCode(): Int {
        var result = entriesOnlyOnLeft.hashCode()
        result = 31 * result + entriesOnlyOnRight.hashCode()
        result = 31 * result + entriesInCommon.hashCode()
        result = 31 * result + entriesDiffering.hashCode()
        return result
    }

    override fun toString(): String = if (areEqual) {
        "equal"
    } else {
        "not equal: only on left=$entriesOnlyOnLeft: only on right=$entriesOnlyOnRight: " +
            "value differences=$entriesDiffering"
    }
}
