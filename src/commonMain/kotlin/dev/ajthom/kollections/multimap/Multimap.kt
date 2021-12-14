package dev.ajthom.kollections.multimap

/**
 * An interface defining the methods available to all Multimaps
 *
 * @param KeyType The type of the keys to be used in the Multimap
 * @param ValueType The type of the values that will be stored in the Multimap
 */
interface Multimap<KeyType, ValueType> {
	/**
	 * All values presented in one list
	 */
	val flatValues: List<ValueType>

	/**
	 * The entries in the multimap
	 */
	val entries: Set<Entry<KeyType, ValueType>>

	/**
	 * The keys in the multimap
	 */
	val keys: Set<KeyType>

	/**
	 * The values, returned as a list of lists
	 */
	val values: List<List<ValueType>>

	/**
	 * The size, which is equal to the number of keys
	 */
	val size: Int

	/**
	 * @return all the values for the given key in the Multimap, or an empty list if the key is nto present
	 *
	 * @param key the key for which you want to retrieve the values
	 */
	operator fun get(key: KeyType): List<ValueType>

	/**
	 * @return this multimap as a Map
	 */
	fun asMap(): Map<KeyType, List<ValueType>>

	/**
	 * @return true if the Multimap is empty, false if there is anything in the multimap
	 */
	fun isEmpty(): Boolean

	/**
	 * @return true if the key is present in the multimap
	 * @param key The key to check
	 */
	fun containsKey(key: KeyType): Boolean

	/**
	 * @return true if the value is present at least once in the Multimap
	 * @param value The value to check
	 */
	fun containsValue(value: ValueType): Boolean

	/**
	 * An entry storing the key and values
	 *
	 * @param KeyType the type of the keys
	 * @param ValueType the type of the
	 */
	interface Entry<out KeyType, out ValueType> {
		/**
		 * The key for this given type
		 */
		val key: KeyType

		/**
		 * The values contained in this given type
		 */
		val value: List<ValueType>
	}
}
