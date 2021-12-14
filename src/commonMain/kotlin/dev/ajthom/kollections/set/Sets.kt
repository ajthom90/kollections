package dev.ajthom.kollections.set

import kotlin.jvm.JvmStatic

object Sets {
	@JvmStatic
	fun <T> intersection(left: Set<T>, right: Set<T>) = left.intersect(right)
}
