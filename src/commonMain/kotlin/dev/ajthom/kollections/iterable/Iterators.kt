package dev.ajthom.kollections.iterable

import kotlin.jvm.JvmStatic

object Iterators {
	/**
	 * @return the first item out of an Iterator, or throws an exception if it is empty
	 * @param iter the iterator
	 */
	@JvmStatic
	fun <T> getFirst(iter: Iterator<T>): T {
		if (!iter.hasNext())
			throw NoSuchElementException("Collection is empty.")
		return iter.next()
	}

	/**
	 * @return the first item out of an iterator, or the default value if it is empty.
	 * @param iter the iterator
	 * @param default the value to be returned if there is not a next item in the iterator.
	 */
	@JvmStatic
	fun <T> getFirst(iter: Iterator<T>, default: T): T {
		return try {
			getFirst(iter)
		} catch (e: NoSuchElementException) {
			default
		}
	}

	@JvmStatic
	fun <T> getLast(iter: Iterator<T>): T {
		var next = iter.next()
		while (iter.hasNext()) {
			next = iter.next()
		}
		return next
	}

	@JvmStatic
	fun <T> only(iter: Iterator<T>): T {
		val value = iter.next()
		if (iter.hasNext()) {
			throw IllegalStateException("Iterable has more than one element.")
		}
		return value
	}

	@JvmStatic
	fun <T> only(iterator: Iterator<T>, default: T): T {
		if (iterator.hasNext()) {
			return only(iterator)
		}
		return default
	}
}
