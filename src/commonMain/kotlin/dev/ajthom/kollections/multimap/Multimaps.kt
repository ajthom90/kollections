package dev.ajthom.kollections.multimap

// Factories

/** Returns an empty read-only [ListMultimap]. */
public fun <K, V> emptyMultimap(): ListMultimap<K, V> = DelegatingListMultimap(emptyMap())

/** Returns an empty read-only [SetMultimap]. */
public fun <K, V> emptySetMultimap(): SetMultimap<K, V> = DelegatingSetMultimap(emptyMap())

/** Returns a read-only [ListMultimap] holding the given [pairs], in order. */
public fun <K, V> multimapOf(vararg pairs: Pair<K, V>): ListMultimap<K, V> = pairs.asList().toMultimap()

/**
 * Returns a read-only [ListMultimap] associating each key of [map] with a copy of its values. Keys whose
 * values are empty are left out. Later changes to [map] are not reflected.
 */
public fun <K, V> multimapOf(map: Map<K, Iterable<V>>): ListMultimap<K, V> = map.toMultimap()

/** Returns a read-only [SetMultimap] holding the given [pairs], in order, with duplicates collapsed. */
public fun <K, V> setMultimapOf(vararg pairs: Pair<K, V>): SetMultimap<K, V> = pairs.asList().toSetMultimap()

/**
 * Returns a read-only [SetMultimap] associating each key of [map] with the distinct values of its collection.
 * Keys whose values are empty are left out. Later changes to [map] are not reflected.
 */
public fun <K, V> setMultimapOf(map: Map<K, Iterable<V>>): SetMultimap<K, V> = map.toSetMultimap()

/** Returns a new insertion-ordered [MutableListMultimap] holding the given [pairs], in order. */
public fun <K, V> mutableMultimapOf(vararg pairs: Pair<K, V>): MutableListMultimap<K, V> =
    pairs.asList().toMutableMultimap()

/**
 * Returns a new insertion-ordered [MutableSetMultimap] holding the given [pairs], in order, with duplicates
 * collapsed.
 */
public fun <K, V> mutableSetMultimapOf(vararg pairs: Pair<K, V>): MutableSetMultimap<K, V> =
    pairs.asList().toMutableSetMultimap()

// Conversions from key-value pairs

/** Returns a read-only [ListMultimap] holding these pairs, in order. */
public fun <K, V> Iterable<Pair<K, V>>.toMultimap(): ListMultimap<K, V> {
    val result = LinkedHashMap<K, MutableList<V>>()
    for ((key, value) in this) {
        result.getOrPut(key) { ArrayList() }.add(value)
    }
    return DelegatingListMultimap(result)
}

/** Returns a read-only [SetMultimap] holding these pairs, in order, with duplicates collapsed. */
public fun <K, V> Iterable<Pair<K, V>>.toSetMultimap(): SetMultimap<K, V> {
    val result = LinkedHashMap<K, MutableSet<V>>()
    for ((key, value) in this) {
        result.getOrPut(key) { LinkedHashSet() }.add(value)
    }
    return DelegatingSetMultimap(result)
}

/** Returns a new insertion-ordered [MutableListMultimap] holding these pairs, in order. */
public fun <K, V> Iterable<Pair<K, V>>.toMutableMultimap(): MutableListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().also { it += this }

/** Returns a new insertion-ordered [MutableSetMultimap] holding these pairs, in order, with duplicates collapsed. */
public fun <K, V> Iterable<Pair<K, V>>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().also { it += this }

// Conversions from maps of collections

/**
 * Returns a read-only [ListMultimap] associating each key of this map with a copy of its values. Keys whose
 * values are empty are left out.
 */
public fun <K, V> Map<K, Iterable<V>>.toMultimap(): ListMultimap<K, V> =
    DelegatingListMultimap(copyValues { it.toList() })

/**
 * Returns a read-only [SetMultimap] associating each key of this map with the distinct values of its
 * collection. Keys whose values are empty are left out.
 */
public fun <K, V> Map<K, Iterable<V>>.toSetMultimap(): SetMultimap<K, V> =
    DelegatingSetMultimap(copyValues { it.toSet() })

/**
 * Returns a new insertion-ordered [MutableListMultimap] associating each key of this map with a copy of its
 * values. Keys whose values are empty are left out.
 */
public fun <K, V> Map<K, Iterable<V>>.toMutableMultimap(): MutableListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().also { it.putAll(this) }

/**
 * Returns a new insertion-ordered [MutableSetMultimap] associating each key of this map with the distinct
 * values of its collection. Keys whose values are empty are left out.
 */
public fun <K, V> Map<K, Iterable<V>>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().also { it.putAll(this) }

// Conversions from multimaps

/** Returns a read-only [ListMultimap] holding a copy of this multimap's key-value pairs, in order. */
public fun <K, V> Multimap<K, V>.toMultimap(): ListMultimap<K, V> =
    DelegatingListMultimap(asMap().copyValues { it.toList() })

/** Returns a read-only [SetMultimap] holding this multimap's key-value pairs, in order, with duplicates collapsed. */
public fun <K, V> Multimap<K, V>.toSetMultimap(): SetMultimap<K, V> =
    DelegatingSetMultimap(asMap().copyValues { it.toSet() })

/** Returns a new insertion-ordered [MutableListMultimap] holding a copy of this multimap's key-value pairs. */
public fun <K, V> Multimap<K, V>.toMutableMultimap(): MutableListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().also { it.putAll(this) }

/**
 * Returns a new insertion-ordered [MutableSetMultimap] holding this multimap's key-value pairs with duplicates
 * collapsed.
 */
public fun <K, V> Multimap<K, V>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().also { it.putAll(this) }

// Views

/**
 * Returns a read-only [ListMultimap] view of this map: later changes to the map are visible through it. The
 * map must never associate a key with an empty list.
 */
public fun <K, V> Map<K, List<V>>.asMultimap(): ListMultimap<K, V> = DelegatingListMultimap(this)

/**
 * Returns a read-only [SetMultimap] view of this map: later changes to the map are visible through it. The
 * map must never associate a key with an empty set.
 */
public fun <K, V> Map<K, Set<V>>.asSetMultimap(): SetMultimap<K, V> = DelegatingSetMultimap(this)

// Builders

/** Builds a read-only [ListMultimap] by populating a [MutableListMultimap] with [builderAction]. */
public inline fun <K, V> buildMultimap(builderAction: MutableListMultimap<K, V>.() -> Unit): ListMultimap<K, V> =
    LinkedHashListMultimap<K, V>().apply(builderAction).toMultimap()

/** Builds a read-only [SetMultimap] by populating a [MutableSetMultimap] with [builderAction]. */
public inline fun <K, V> buildSetMultimap(builderAction: MutableSetMultimap<K, V>.() -> Unit): SetMultimap<K, V> =
    LinkedHashSetMultimap<K, V>().apply(builderAction).toSetMultimap()

// Grouping

/** Groups the elements by the key returned by [keySelector] into a read-only [ListMultimap]. */
public inline fun <K, V> Iterable<V>.multimapWith(keySelector: (V) -> K): ListMultimap<K, V> =
    DelegatingListMultimap(groupBy(keySelector))

/**
 * Groups the values returned by [valueTransform] by the key returned by [keySelector] into a read-only
 * [ListMultimap].
 */
public inline fun <K, V, R> Iterable<V>.multimapWith(
    keySelector: (V) -> K,
    valueTransform: (V) -> R,
): ListMultimap<K, R> = DelegatingListMultimap(groupBy(keySelector, valueTransform))

/** Adds each element to [destination] under the key returned by [keySelector], then returns [destination]. */
public inline fun <K, V, M : MutableMultimap<in K, in V>> Iterable<V>.multimapWith(
    destination: M,
    keySelector: (V) -> K,
): M {
    for (element in this) {
        destination.put(keySelector(element), element)
    }
    return destination
}

// Transformations

/** Returns a read-only [ListMultimap] with every value replaced by the result of [transform]. */
public inline fun <K, V, R> ListMultimap<K, V>.transformValues(transform: (V) -> R): ListMultimap<K, R> =
    DelegatingListMultimap(asMap().mapValues { (_, values) -> values.map(transform) })

/**
 * Returns a read-only [SetMultimap] with every value replaced by the result of [transform]; values that
 * become equal collapse.
 */
public inline fun <K, V, R> SetMultimap<K, V>.transformValues(transform: (V) -> R): SetMultimap<K, R> =
    DelegatingSetMultimap(asMap().mapValues { (_, values) -> values.mapTo(LinkedHashSet(), transform) })

/** Returns a read-only [ListMultimap] with every value replaced by the result of [transform]. */
public inline fun <K, V, R> Multimap<K, V>.transformValues(transform: (V) -> R): ListMultimap<K, R> =
    DelegatingListMultimap(asMap().mapValues { (_, values) -> values.map(transform) })

/**
 * Returns a read-only [ListMultimap] with every key replaced by the result of [transform]. When several keys
 * map to the same new key, their values are concatenated in key order.
 */
public inline fun <K, V, R> ListMultimap<K, V>.mapKeys(transform: (K) -> R): ListMultimap<R, V> {
    val result = LinkedHashMap<R, MutableList<V>>()
    for ((key, values) in asMap()) {
        result.getOrPut(transform(key)) { ArrayList() }.addAll(values)
    }
    return DelegatingListMultimap(result)
}

/**
 * Returns a read-only [SetMultimap] with every key replaced by the result of [transform]. When several keys
 * map to the same new key, their value sets are united.
 */
public inline fun <K, V, R> SetMultimap<K, V>.mapKeys(transform: (K) -> R): SetMultimap<R, V> {
    val result = LinkedHashMap<R, MutableSet<V>>()
    for ((key, values) in asMap()) {
        result.getOrPut(transform(key)) { LinkedHashSet() }.addAll(values)
    }
    return DelegatingSetMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] with every key replaced by the result of [transform]. When several keys
 * map to the same new key, their values are concatenated in key order.
 */
public inline fun <K, V, R> Multimap<K, V>.mapKeys(transform: (K) -> R): ListMultimap<R, V> {
    val result = LinkedHashMap<R, MutableList<V>>()
    for ((key, values) in asMap()) {
        result.getOrPut(transform(key)) { ArrayList() }.addAll(values)
    }
    return DelegatingListMultimap(result)
}

/** Returns a read-only [ListMultimap] holding only the keys matching [predicate], with their values. */
public inline fun <K, V> ListMultimap<K, V>.filterKeys(predicate: (K) -> Boolean): ListMultimap<K, V> {
    val result = LinkedHashMap<K, List<V>>()
    for ((key, values) in asMap()) {
        if (predicate(key)) result[key] = values.toList()
    }
    return DelegatingListMultimap(result)
}

/** Returns a read-only [SetMultimap] holding only the keys matching [predicate], with their values. */
public inline fun <K, V> SetMultimap<K, V>.filterKeys(predicate: (K) -> Boolean): SetMultimap<K, V> {
    val result = LinkedHashMap<K, Set<V>>()
    for ((key, values) in asMap()) {
        if (predicate(key)) result[key] = values.toSet()
    }
    return DelegatingSetMultimap(result)
}

/** Returns a read-only [ListMultimap] holding only the keys matching [predicate], with their values. */
public inline fun <K, V> Multimap<K, V>.filterKeys(predicate: (K) -> Boolean): ListMultimap<K, V> {
    val result = LinkedHashMap<K, List<V>>()
    for ((key, values) in asMap()) {
        if (predicate(key)) result[key] = values.toList()
    }
    return DelegatingListMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] holding only the values matching [predicate]. Keys left without values
 * are dropped.
 */
public inline fun <K, V> ListMultimap<K, V>.filterValues(predicate: (V) -> Boolean): ListMultimap<K, V> {
    val result = LinkedHashMap<K, List<V>>()
    for ((key, values) in asMap()) {
        val kept = values.filter(predicate)
        if (kept.isNotEmpty()) result[key] = kept
    }
    return DelegatingListMultimap(result)
}

/**
 * Returns a read-only [SetMultimap] holding only the values matching [predicate]. Keys left without values
 * are dropped.
 */
public inline fun <K, V> SetMultimap<K, V>.filterValues(predicate: (V) -> Boolean): SetMultimap<K, V> {
    val result = LinkedHashMap<K, Set<V>>()
    for ((key, values) in asMap()) {
        val kept = values.filterTo(LinkedHashSet(), predicate)
        if (kept.isNotEmpty()) result[key] = kept
    }
    return DelegatingSetMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] holding only the values matching [predicate]. Keys left without values
 * are dropped.
 */
public inline fun <K, V> Multimap<K, V>.filterValues(predicate: (V) -> Boolean): ListMultimap<K, V> {
    val result = LinkedHashMap<K, List<V>>()
    for ((key, values) in asMap()) {
        val kept = values.filter(predicate)
        if (kept.isNotEmpty()) result[key] = kept
    }
    return DelegatingListMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] holding only the key-value pairs matching [predicate]. Keys left without
 * values are dropped.
 */
public inline fun <K, V> ListMultimap<K, V>.filter(predicate: (key: K, value: V) -> Boolean): ListMultimap<K, V> {
    val result = LinkedHashMap<K, List<V>>()
    for ((key, values) in asMap()) {
        val kept = values.filter { predicate(key, it) }
        if (kept.isNotEmpty()) result[key] = kept
    }
    return DelegatingListMultimap(result)
}

/**
 * Returns a read-only [SetMultimap] holding only the key-value pairs matching [predicate]. Keys left without
 * values are dropped.
 */
public inline fun <K, V> SetMultimap<K, V>.filter(predicate: (key: K, value: V) -> Boolean): SetMultimap<K, V> {
    val result = LinkedHashMap<K, Set<V>>()
    for ((key, values) in asMap()) {
        val kept = values.filterTo(LinkedHashSet()) { predicate(key, it) }
        if (kept.isNotEmpty()) result[key] = kept
    }
    return DelegatingSetMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] holding only the key-value pairs matching [predicate]. Keys left without
 * values are dropped.
 */
public inline fun <K, V> Multimap<K, V>.filter(predicate: (key: K, value: V) -> Boolean): ListMultimap<K, V> {
    val result = LinkedHashMap<K, List<V>>()
    for ((key, values) in asMap()) {
        val kept = values.filter { predicate(key, it) }
        if (kept.isNotEmpty()) result[key] = kept
    }
    return DelegatingListMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] associating each value of this multimap with the keys it appears under,
 * in the order the pairs are visited. A value that appears several times under one key yields that key as
 * many times.
 */
public fun <K, V> ListMultimap<K, V>.inverse(): ListMultimap<V, K> = invertToLists()

/**
 * Returns a read-only [SetMultimap] associating each value of this multimap with the distinct keys it appears
 * under, in the order the pairs are visited.
 */
public fun <K, V> SetMultimap<K, V>.inverse(): SetMultimap<V, K> {
    val result = LinkedHashMap<V, MutableSet<K>>()
    for ((key, value) in this) {
        result.getOrPut(value) { LinkedHashSet() }.add(key)
    }
    return DelegatingSetMultimap(result)
}

/**
 * Returns a read-only [ListMultimap] associating each value of this multimap with the keys it appears under,
 * in the order the pairs are visited.
 */
public fun <K, V> Multimap<K, V>.inverse(): ListMultimap<V, K> = invertToLists()

// Operators on list multimaps

/** Returns a read-only [ListMultimap] holding this multimap's pairs followed by [pair]. */
public operator fun <K, V> ListMultimap<K, V>.plus(pair: Pair<K, V>): ListMultimap<K, V> =
    copyThen { it.put(pair.first, pair.second) }

/** Returns a read-only [ListMultimap] holding this multimap's pairs followed by every pair of [pairs]. */
public operator fun <K, V> ListMultimap<K, V>.plus(pairs: Iterable<Pair<K, V>>): ListMultimap<K, V> =
    copyThen { it += pairs }

/** Returns a read-only [ListMultimap] holding this multimap's pairs followed by every pair of [other]. */
public operator fun <K, V> ListMultimap<K, V>.plus(other: Multimap<out K, out V>): ListMultimap<K, V> =
    copyThen { it.putAll(other) }

/** Returns a read-only [ListMultimap] holding this multimap's pairs except those under [key]. */
public operator fun <K, V> ListMultimap<K, V>.minus(key: K): ListMultimap<K, V> = copyThen { it.remove(key) }

/** Returns a read-only [ListMultimap] holding this multimap's pairs except those under any of [keys]. */
public operator fun <K, V> ListMultimap<K, V>.minus(keys: Iterable<K>): ListMultimap<K, V> = copyThen { it -= keys }

/** Returns a read-only [ListMultimap] holding this multimap's pairs with one occurrence of [pair] removed. */
public operator fun <K, V> ListMultimap<K, V>.minus(pair: Pair<K, V>): ListMultimap<K, V> =
    copyThen { it.remove(pair.first, pair.second) }

// Operators on set multimaps

/** Returns a read-only [SetMultimap] holding this multimap's pairs and [pair]. */
public operator fun <K, V> SetMultimap<K, V>.plus(pair: Pair<K, V>): SetMultimap<K, V> =
    copyThen { it.put(pair.first, pair.second) }

/** Returns a read-only [SetMultimap] holding this multimap's pairs and every pair of [pairs]. */
public operator fun <K, V> SetMultimap<K, V>.plus(pairs: Iterable<Pair<K, V>>): SetMultimap<K, V> =
    copyThen { it += pairs }

/** Returns a read-only [SetMultimap] holding this multimap's pairs and every pair of [other]. */
public operator fun <K, V> SetMultimap<K, V>.plus(other: Multimap<out K, out V>): SetMultimap<K, V> =
    copyThen { it.putAll(other) }

/** Returns a read-only [SetMultimap] holding this multimap's pairs except those under [key]. */
public operator fun <K, V> SetMultimap<K, V>.minus(key: K): SetMultimap<K, V> = copyThen { it.remove(key) }

/** Returns a read-only [SetMultimap] holding this multimap's pairs except those under any of [keys]. */
public operator fun <K, V> SetMultimap<K, V>.minus(keys: Iterable<K>): SetMultimap<K, V> = copyThen { it -= keys }

/** Returns a read-only [SetMultimap] holding this multimap's pairs except [pair]. */
public operator fun <K, V> SetMultimap<K, V>.minus(pair: Pair<K, V>): SetMultimap<K, V> =
    copyThen { it.remove(pair.first, pair.second) }

// Operators on mutable multimaps

/** Adds [pair] to this multimap. */
public operator fun <K, V> MutableMultimap<K, V>.plusAssign(pair: Pair<K, V>) {
    put(pair.first, pair.second)
}

/**
 * Adds every pair of [pairs] to this multimap. [pairs] is read completely before anything is added, so it may
 * be derived from this multimap's own views.
 */
public operator fun <K, V> MutableMultimap<K, V>.plusAssign(pairs: Iterable<Pair<K, V>>) {
    for ((key, value) in pairs.toList()) {
        put(key, value)
    }
}

/** Adds every key-value pair of [other] to this multimap. */
public operator fun <K, V> MutableMultimap<K, V>.plusAssign(other: Multimap<out K, out V>) {
    putAll(other)
}

/** Removes every value associated with [key] from this multimap. */
public operator fun <K, V> MutableMultimap<K, V>.minusAssign(key: K) {
    remove(key)
}

/**
 * Removes every value associated with each of [keys] from this multimap. [keys] is read completely before
 * anything is removed, so it may be this multimap's own [MutableMultimap.keys] view.
 */
public operator fun <K, V> MutableMultimap<K, V>.minusAssign(keys: Iterable<K>) {
    for (key in keys.toList()) {
        remove(key)
    }
}

/** Removes a single association of [pair]'s value with its key from this multimap. */
public operator fun <K, V> MutableMultimap<K, V>.minusAssign(pair: Pair<K, V>) {
    remove(pair.first, pair.second)
}

// Iteration and emptiness

/**
 * Returns an iterator over every key-value pair of this multimap, in the order of [Multimap.keys] and, within
 * a key, in the order of its values. This lets `for ((key, value) in multimap)` work.
 */
public operator fun <K, V> Multimap<K, V>.iterator(): Iterator<Pair<K, V>> =
    asMap().asSequence().flatMap { (key, values) -> values.asSequence().map { key to it } }.iterator()

/** Returns this multimap if it is not `null`, or an empty read-only [ListMultimap] otherwise. */
public fun <K, V> Multimap<K, V>?.orEmpty(): Multimap<K, V> = this ?: emptyMultimap()

/** Returns this multimap if it is not `null`, or an empty read-only [ListMultimap] otherwise. */
public fun <K, V> ListMultimap<K, V>?.orEmpty(): ListMultimap<K, V> = this ?: emptyMultimap()

/** Returns this multimap if it is not `null`, or an empty read-only [SetMultimap] otherwise. */
public fun <K, V> SetMultimap<K, V>?.orEmpty(): SetMultimap<K, V> = this ?: emptySetMultimap()

/** Returns `true` if this multimap contains at least one key-value pair. */
public fun Multimap<*, *>.isNotEmpty(): Boolean = !isEmpty()

// Private helpers

private inline fun <K, V, C : Collection<V>> Map<K, Iterable<V>>.copyValues(copy: (Iterable<V>) -> C): Map<K, C> {
    val result = LinkedHashMap<K, C>()
    for ((key, values) in this) {
        val copied = copy(values)
        if (copied.isNotEmpty()) result[key] = copied
    }
    return result
}

private fun <K, V> Multimap<K, V>.invertToLists(): ListMultimap<V, K> {
    val result = LinkedHashMap<V, MutableList<K>>()
    for ((key, value) in this) {
        result.getOrPut(value) { ArrayList() }.add(key)
    }
    return DelegatingListMultimap(result)
}

private inline fun <K, V> ListMultimap<K, V>.copyThen(
    mutation: (MutableListMultimap<K, V>) -> Unit,
): ListMultimap<K, V> {
    val map = LinkedHashMap<K, MutableList<V>>()
    val copy = DelegatingMutableListMultimap(map)
    copy.putAll(this)
    mutation(copy)
    return DelegatingListMultimap(map)
}

private inline fun <K, V> SetMultimap<K, V>.copyThen(mutation: (MutableSetMultimap<K, V>) -> Unit): SetMultimap<K, V> {
    val map = LinkedHashMap<K, MutableSet<V>>()
    val copy = DelegatingMutableSetMultimap(map)
    copy.putAll(this)
    mutation(copy)
    return DelegatingSetMultimap(map)
}
