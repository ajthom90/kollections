package dev.ajthom.kollections.iterable

import kotlin.jvm.JvmStatic

/**
 * An object, based somewhat on Guava, that provides some helpers for Iterables
 */
object Iterables {
	@JvmStatic
	fun <T> getFirst(iter: Iterable<T>): T {
		return iter.first()
	}

	@JvmStatic
	fun <T> getFirst(iter: Iterable<T>, default: T): T {
		return iter.firstOrNull() ?: default
	}

	@JvmStatic
	fun <T> getLast(iter: Iterable<T>): T {
		return iter.last()
	}

	@JvmStatic
	fun <T> only(iter: Iterable<T>): T {
		val iterator = iter.iterator()
		val value = iterator.next()
		if (iterator.hasNext()) {
			throw IllegalStateException("Iterable has more than one element.")
		}
		return value
	}

	@JvmStatic
	fun <T> only(iter: Iterable<T>, default: T): T {
		val iterator = iter.iterator()
		if (iterator.hasNext()) {
			return only(iter)
		}
		return default
	}
}
