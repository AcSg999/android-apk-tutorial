package com.example.pockettasks

data class Todo(val id: String, val title: String, val done: Boolean = false)

enum class TaskFilter(val label: String) {
    ALL("全部"), OPEN("未完成"), DONE("已完成")
}

object TodoRules {
    const val MAX_TITLE_LENGTH = 80
    const val MAX_TASKS = 200

    fun normalizedTitle(raw: String): String {
        val title = raw.trim()
        require(title.isNotEmpty()) { "请先输入任务" }
        require(title.length <= MAX_TITLE_LENGTH) { "任务最多 80 个字符" }
        return title
    }

    fun visible(tasks: List<Todo>, filter: TaskFilter): List<Todo> =
        tasks.filter {
            when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.OPEN -> !it.done
                TaskFilter.DONE -> it.done
            }
        }
}
