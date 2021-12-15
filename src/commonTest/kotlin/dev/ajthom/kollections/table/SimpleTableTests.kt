package dev.ajthom.kollections.table

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleTableTests {
	@Test
	fun testSimpleTableAddAndCheck() {
		val table = newTable()
		table.put("a", "b", "c")
		assertTrue {
			table.contains("a", "b")
		}
		assertTrue {
			val value = table.get("a", "b")
			value == "c"
		}
		assertFalse {
			table.contains("b", "a")
		}
	}

	@Test
	fun testTransposed() {
		val table = newTable()
		table.put("a", "b", "c")
		val transposed = table.transpose()
		assertTrue {
			transposed.contains("b", "a")
		}
		assertTrue {
			val value = transposed.get("b", "a")
			value == "c"
		}
		assertFalse {
			transposed.contains("a", "b")
		}
	}

	@Test
	fun testColumnMap() {
		val table = newTable()
		table.put("a", "b", "c")
		val columnMap = table.columnMap()
		assertContains(columnMap, "b")
		assertContains(columnMap["b"].orEmpty(), "a")
	}

	private fun newTable(): SimpleTable<String, String, String> = SimpleTable()
}
