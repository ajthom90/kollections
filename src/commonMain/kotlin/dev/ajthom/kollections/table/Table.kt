package dev.ajthom.kollections.table

interface Table<RowType, ColumnType, ValueType> {
	fun cellSet(): Set<TableCell<RowType, ColumnType, ValueType>>
	fun column(columnKey: ColumnType): Map<RowType, ValueType>
	fun columnKeySet(): Set<ColumnType>
	fun columnMap(): Map<ColumnType, Map<RowType, ValueType>>
	fun contains(rowKey: RowType, columnKey: ColumnType): Boolean
	fun containsColumn(columnKey: ColumnType): Boolean
	fun containsRow(rowKey: RowType): Boolean
	fun containsValue(value: ValueType): Boolean
	fun get(rowKey: RowType, columnKey: ColumnType): ValueType?
	fun isEmpty(): Boolean
	fun row(rowKey: RowType): Map<ColumnType, ValueType>
	fun rowKeySet(): Set<RowType>
	fun rowMap(): Map<RowType, Map<ColumnType, ValueType>>
	fun size(): Int
	fun values(): Collection<ValueType>
	fun transpose(): Table<ColumnType, RowType, ValueType>
}

data class TableCell<R, C, V>(val rowKey: R, val columnKey: C, val value: V)
