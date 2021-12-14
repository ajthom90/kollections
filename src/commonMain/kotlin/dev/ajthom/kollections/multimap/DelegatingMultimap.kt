package dev.ajthom.kollections.multimap

class DelegatingMultimap<KeyType, ValueType>(private val map: Map<KeyType, List<ValueType>>): Multimap<KeyType, ValueType> {
	override val entries: Set<Multimap.Entry<KeyType, ValueType>>
		get() = map.entries.mapTo(mutableSetOf()) { Entry(it) }
	override val keys: Set<KeyType>
		get() = map.keys
	override val size: Int
		get() = map.size
	override val values: List<List<ValueType>>
		get() = map.values.toList()
	override val flatValues: List<ValueType>
		get() = map.values.flatten()

	override fun containsKey(key: KeyType) = map.containsKey(key)
	override fun containsValue(value: ValueType) = flatValues.contains(value)
	override fun get(key: KeyType) = map[key] ?: emptyList()
	override fun isEmpty() = map.isEmpty()
	override fun asMap() = map

	inner class Entry(override val key: KeyType, override val value: List<ValueType>): Multimap.Entry<KeyType, ValueType> {
		constructor(entry: Map.Entry<KeyType, List<ValueType>>): this(entry.key, entry.value)
	}
}
