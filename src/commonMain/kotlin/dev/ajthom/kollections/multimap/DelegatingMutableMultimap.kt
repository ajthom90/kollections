package dev.ajthom.kollections.multimap

open class DelegatingMutableMultimap<KeyType, ValueType>(private val map: MutableMap<KeyType, MutableList<ValueType>>): MutableMultimap<KeyType, ValueType> {
	override val size: Int
		get() = map.size

	override val flatValues: MutableList<ValueType>
		get() = map.values.flatten().toMutableList()

	override val entries: MutableSet<MutableMultimap.MutableEntry<KeyType, ValueType>>
		get() = map.entries.mapTo(mutableSetOf()) { MutableEntry(it) }

	override val keys: MutableSet<KeyType>
		get() = map.keys

	override val values: MutableList<MutableList<ValueType>>
		get() = map.values.toMutableList()

	override fun isEmpty() = map.isEmpty()
	override fun containsKey(key: KeyType) = map.containsKey(key)
	override fun containsValue(value: ValueType) = flatValues.contains(value)
	override fun get(key: KeyType) = map[key] ?: mutableListOf()
	override fun asMap() = map
	override fun clear() = map.clear()
	override fun remove(key: KeyType) = (map.remove(key) ?: mutableListOf())

	override fun putAll(from: Map<out KeyType, List<ValueType>>) {
		for ((key, list) in from) {
			for (item in list) {
				put(key, item)
			}
		}
	}

	override fun put(key: KeyType, value: ValueType) {
		val existing = map[key]
		val list = if (existing == null) {
			val newList = mutableListOf<ValueType>()
			map[key] = newList
			newList
		} else {
			existing
		}
		list.add(value)
	}

	override fun putAll(key: KeyType, values: Iterable<ValueType>) {
		for (value in values) {
			put(key, value)
		}
	}

	inner class MutableEntry(override var key: KeyType, override var value: MutableList<ValueType>): MutableMultimap.MutableEntry<KeyType, ValueType> {
		constructor(entry: MutableMap.MutableEntry<KeyType, MutableList<ValueType>>): this(entry.key, entry.value)
	}
}
