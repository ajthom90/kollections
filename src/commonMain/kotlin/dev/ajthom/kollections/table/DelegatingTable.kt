package dev.ajthom.kollections.table

import dev.ajthom.kollections.map.orEmpty

open class DelegatingTable<R, C, V>(private val map: Map<R, Map<C, V>>): Table<R, C, V> {
	override fun cellSet(): Set<TableCell<R, C, V>> {
		return buildSet {
			for (r in map.keys) {
				val columnMap = map[r].orEmpty()
				for (entry in columnMap) {
					val (c, v) = entry
					add(TableCell(r, c, v))
				}
			}
		}
	}

	override fun columnKeySet(): Set<C> {
		val out = linkedSetOf<C>()
		for ((_, cm) in map) {
			out.addAll(cm.keys)
		}
		return out
	}

	override fun columnMap(): Map<C, Map<R, V>> {
		return buildMap {
			for ((r, c, v) in cellSet()) {
				val rowMap = this[c].orEmpty().toMutableMap()
				this[c] = rowMap
				rowMap[r] = v
			}
		}
	}

	override fun rowKeySet(): Set<R> {
		return map.keys
	}

	override fun rowMap(): Map<R, Map<C, V>> {
		return map
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
			val cm = map[rowKey] ?: emptyMap()
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
			val cm = map[rowKey] ?: emptyMap()
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

	override fun transpose(): Table<C, R, V> {
		return DelegatingTable(columnMap())
	}
}
