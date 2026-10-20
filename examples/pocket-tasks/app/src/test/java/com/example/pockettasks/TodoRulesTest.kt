package com.example.pockettasks

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TodoRulesTest {
    @Test fun trimsWhitespace() {
        assertEquals("买牛奶", TodoRules.normalizedTitle("  买牛奶  "))
    }
    @Test fun rejectsBlankAndOverlongTasks() {
        assertThrows(IllegalArgumentException::class.java) { TodoRules.normalizedTitle("  ") }
        assertThrows(IllegalArgumentException::class.java) { TodoRules.normalizedTitle("字".repeat(81)) }
        assertEquals(80, TodoRules.normalizedTitle("字".repeat(80)).length)
    }
    @Test fun filtersWithoutChangingOriginalList() {
        val tasks = listOf(Todo("1", "学习"), Todo("2", "散步", true))
        assertEquals(listOf(tasks[0]), TodoRules.visible(tasks, TaskFilter.OPEN))
        assertEquals(listOf(tasks[1]), TodoRules.visible(tasks, TaskFilter.DONE))
        assertEquals(tasks, TodoRules.visible(tasks, TaskFilter.ALL))
        assertEquals(2, tasks.size)
    }
}
