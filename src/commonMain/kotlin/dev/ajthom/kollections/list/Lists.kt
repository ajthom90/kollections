package dev.ajthom.kollections.list

import kotlin.jvm.JvmStatic

/**
 * This is a helper class for those familiar with Guava on the JVM
 */
object ImmutableList {
	/**
	 * @return an empty unmodifiable list
	 */
	@JvmStatic
	fun <T> of(): List<T> {
		return emptyList()
	}

	/**
	 * @return an unmodifiable list containing the items given
	 */
	@JvmStatic
	fun <T> of(vararg item: T): List<T> {
		return listOf(*item)
	}

	/**
	 * @return an unmodifiable list containing only the given item
	 */
	@JvmStatic
	fun <T> of(item: T): List<T> {
		return listOf(item)
	}
}
