package dev.ajthom.kollections.set

import kotlin.coroutines.CoroutineContext

interface Multiset<E>: MutableCollection<E> {
	override val size: Int
	fun count(element: Any): Int
	fun add(element: E, occurrences: Int): Int
	override fun add(element: E): Boolean
	fun remove(e: Any, occurrences: Int): Int
	override fun remove(element: E): Boolean
	fun setCount(element: E, count: Int): Int
	fun setCount(element: E, oldCount: Int, newCount: Int)
	fun elementSet(): MutableSet<E>
	fun entrySet(): Set<Entry<E>>
	override fun iterator(): MutableIterator<E>

	interface Entry<E> {
		val element: E
		val count: Int
	}
}
