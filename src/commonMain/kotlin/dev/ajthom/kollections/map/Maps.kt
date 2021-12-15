package dev.ajthom.kollections.map

import kotlin.jvm.JvmStatic

object Maps {
	internal fun <KeyType> safeContainsKey(map: Map<KeyType, *>, key: KeyType): Boolean {
		return try {
			map.containsKey(key)
		} catch (e: Exception) {
			false
		}
	}

	internal fun <RowType, ValueType> safeGet(map: Map<RowType, ValueType>, key: RowType?): ValueType? {
		return try {
			map[key]
		} catch (e: Exception) {
			null
		}
	}

	internal fun <K, V: Any?> safeRemove(map: MutableMap<K, V>, key: K): V? {
		return try {
			map.remove(key)
		} catch (e: Exception) {
			null
		}
	}
}

fun <K, V> Map<K, V>?.orEmpty() = this ?: emptyMap()

object ImmutableMap {
	@JvmStatic
	fun <K, V> of(): Map<K, V> {
		return emptyMap()
	}

	@JvmStatic
	fun <K, V> of(key1: K, value1: V): Map<K, V> {
		return mapOf(key1 to value1)
	}

	@JvmStatic
	fun <K, V> of(key1: K, value1: V, key2: K, value2: V): Map<K, V> {
		return mapOf(key1 to value1, key2 to value2)
	}

	@JvmStatic
	fun <K, V> of(key1: K, value1: V, key2: K, value2: V, key3: K, value3: V): Map<K, V> {
		return mapOf(key1 to value1, key2 to value2, key3 to value3)
	}
}
