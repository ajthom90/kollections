package dev.ajthom.kollections.multimap

import kotlin.test.Test
import kotlin.test.assertTrue

class DelegatingMultimapTests {
	@Test
	fun testThatMultimapWorks() {
		val map = mapOf("a" to listOf("b", "c"), "d" to listOf("e", "f"))
		val multimap = DelegatingMultimap(map)
		assertTrue {
			multimap["a"].containsAll(setOf("b", "c"))
			multimap["d"].containsAll(setOf("e", "f"))
			multimap["z"].isEmpty()
			map === multimap.asMap()
		}
	}

	@Test
	fun testThatBuildMultimapFunctionWorks() {
		val multimap = buildMultimap<String, String> {
			put("a", "b")
			put("a", "c")
			put("d", "e")
			put("d", "f")
		}
		assertTrue {
			multimap["a"].containsAll(setOf("b", "c"))
			multimap["d"].containsAll(setOf("e", "f"))
			multimap["z"].isEmpty()
		}
	}
}
