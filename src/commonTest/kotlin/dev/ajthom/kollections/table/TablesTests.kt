package dev.ajthom.kollections.table

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TablesTests {
	@Test
	fun testThatBuildTableWorks() {
		val table = buildTable<String, String, String> {
			put("a", "b", "c")
			put("d", "e", "f")
		}
		assertTrue("table contains entry for a/b with value c") {
			val value = table.get("a", "b")
			value == "c"
		}
		assertTrue("table contains entry for a/b with value c") {
			val value = table.get("d", "e")
			value == "f"
		}
		assertFalse("table does not contain entry for x/y") {
			table.contains("x", "y")
		}
		assertFalse("table does not contain entry for a/c") {
			table.contains("a", "c")
		}
	}
}
