package dev.ajthom.kollections.multimap

interface MutableMultimap<KeyType, ValueType>: Multimap<KeyType, ValueType> {
	override val flatValues: MutableList<ValueType>
	override val entries: MutableSet<MutableEntry<KeyType, ValueType>>
	override val keys: MutableSet<KeyType>
	override val values: MutableList<MutableList<ValueType>>

	override operator fun get(key: KeyType): MutableList<ValueType>
	override fun asMap(): MutableMap<KeyType, MutableList<ValueType>>

	fun clear()
	fun putAll(from: Map<out KeyType, List<ValueType>>)
	fun remove(key: KeyType): MutableList<ValueType>
	fun put(key: KeyType, value: ValueType)
	fun putAll(key: KeyType, values: Iterable<ValueType>)

	interface MutableEntry<KeyType, ValueType>: Multimap.Entry<KeyType, ValueType> {
		override var value: MutableList<ValueType>
		override var key: KeyType
	}
}
