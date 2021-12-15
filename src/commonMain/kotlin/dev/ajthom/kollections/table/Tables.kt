package dev.ajthom.kollections.table

fun <R, C, V> buildTable(block: MutableTable<R, C, V>.() -> Unit): Table<R, C, V> {
	val table = SimpleTable<R, C, V>()
	table.block()
	return table.asTable()
}
