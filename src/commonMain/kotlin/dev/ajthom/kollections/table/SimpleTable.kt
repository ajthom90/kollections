package dev.ajthom.kollections.table

class SimpleTable<R, C, V>: MutableTable<R, C, V> {
	private val map = linkedMapOf<R, LinkedHashMap<C, V>>()

	override fun cellSet(): MutableSet<TableCell<R, C, V>> {
		val out = linkedSetOf<TableCell<R, C, V>>()
		for (r in map.keys) {
			for ((c, v) in (map[r] ?: emptyMap())) {
				out.add(TableCell(r, c, v))
			}
		}
		return out
	}

	override fun clear() {
		map.clear()
	}

	override fun columnKeySet(): MutableSet<C> {
		val out = linkedSetOf<C>()
		for ((_, cm) in map) {
			out.addAll(cm.keys)
		}
		return out
	}

	override fun columnMap(): MutableMap<C, MutableMap<R, V>> {
		val out = linkedMapOf<C, LinkedHashMap<R, V>>()
		for ((r, c, v) in cellSet()) {
			val rowMap = out[c] ?: linkedMapOf()
			out[c] = rowMap
			rowMap[r] = v
		}
		return out.toMutableMap()
	}

	override fun put(rowKey: R, columnKey: C, value: V): V? {
		val cm = getColumnMap(rowKey)
		return cm.put(columnKey, value)
	}

	override fun remove(rowKey: R, columnKey: C): V? {
		val cm = getColumnMap(rowKey)
		val value = cm.remove(columnKey)
		if (cm.isEmpty()) {
			map.remove(rowKey)
		}
		return value
	}

	override fun rowKeySet(): MutableSet<R> {
		return map.keys
	}

	override fun rowMap(): MutableMap<R, MutableMap<C, V>> {
		return map.toMutableMap()
	}

	override fun values(): MutableCollection<V> {
		return map.flatMapTo(mutableListOf()) { (_, cm) ->
			cm.map { it.value }
		}
	}

	override fun column(columnKey: C): Map<R, V> {
		return columnMap()[columnKey] ?: emptyMap()
	}

	override fun contains(rowKey: R, columnKey: C): Boolean {
		if (map.containsKey(rowKey)) {
			val cm = getColumnMap(rowKey)
			if (cm.containsKey(columnKey)) {
				return true
			}
		}
		return false
	}

	override fun containsColumn(columnKey: C): Boolean {
		for (cm in map.values) {
			if (cm.containsKey(columnKey)) {
				return true
			}
		}
		return false
	}

	override fun containsRow(rowKey: R): Boolean {
		return map.containsKey(rowKey)
	}

	override fun containsValue(value: V): Boolean {
		return values().contains(value)
	}

	override fun get(rowKey: R, columnKey: C): V? {
		if (containsRow(rowKey) && containsColumn(columnKey)) {
			val cm = getColumnMap(rowKey)
			return cm[columnKey]
		}
		return null
	}

	override fun isEmpty(): Boolean {
		return map.isEmpty()
	}

	override fun row(rowKey: R): Map<C, V> {
		return map[rowKey] ?: emptyMap()
	}

	override fun size(): Int {
		return map.size
	}

	private fun getColumnMap(rowKey: R): LinkedHashMap<C, V> {
		val cm = map[rowKey]
		val out = if (cm == null) {
			val newMap = linkedMapOf<C, V>()
			map[rowKey] = newMap
			newMap
		} else {
			cm
		}
		return out
	}
}
