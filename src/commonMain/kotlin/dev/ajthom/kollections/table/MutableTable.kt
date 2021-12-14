package dev.ajthom.kollections.table

interface MutableTable<RowType, ColumnType, ValueType>: Table<RowType, ColumnType, ValueType> {
	override fun cellSet(): MutableSet<TableCell<RowType, ColumnType, ValueType>>
	fun clear()
	override fun columnKeySet(): MutableSet<ColumnType>
	override fun columnMap(): MutableMap<ColumnType, MutableMap<RowType, ValueType>>
	fun put(rowKey: RowType, columnKey: ColumnType, value: ValueType): ValueType?
	fun remove(rowKey: RowType, columnKey: ColumnType): ValueType?
	override fun rowKeySet(): MutableSet<RowType>
	override fun rowMap(): MutableMap<RowType, MutableMap<ColumnType, ValueType>>
	override fun values(): MutableCollection<ValueType>
}
