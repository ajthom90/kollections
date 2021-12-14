package dev.ajthom.kollections.multimap

fun <K, V> emptyMultimap(): Multimap<K, V> {
	return multimapOf(emptyMap())
}

fun <K, V> multimapOf(map: Map<K, List<V>>): Multimap<K, V> {
	return DelegatingMultimap(map)
}

fun <K, V> mutableMultimapOf(): MutableMultimap<K, V> {
	return MutableLinkedHashMultimap()
}

fun <K, V1, V2> Multimap<K, V1>.transformValues(transform: (V1) -> V2): Multimap<K, V2> {
	return multimapOf(this.asMap().mapValues { it.value.map(transform) })
}

fun <K, V, C: Collection<V>> C.multimapWith(keySelector: (V) -> K): Multimap<K, V> {
	return multimapOf(this.groupBy(keySelector))
}

fun <K, V, C: Collection<V>, M: MutableMultimap<K, V>> C.multimapWith(multimap: M, keySelector: (V) -> K): M {
	for (value in this) {
		multimap.put(keySelector(value), value)
	}
	return multimap
}

fun <K, V> buildMultimap(block: MutableMultimap<K, V>.() -> Unit): Multimap<K, V> {
	val mutableMultimap = mutableMultimapOf<K, V>()
	mutableMultimap.block()
	return DelegatingMultimap(mutableMultimap.asMap().toImmutableMap())
}

private fun <K, V> MutableMap<K, MutableList<V>>.toImmutableMap(): Map<K, List<V>> {
	return this.mapValues { it.value.toList() }.toMap()
}
