package com.example.pockettasks

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// One DataStore instance per file in this process.
private val Context.todoStore by preferencesDataStore(name = "pocket_tasks_v1")

class TodoRepository(context: Context) {
    private val store = context.applicationContext.todoStore
    private val tasksKey = stringPreferencesKey("tasks_json")

    val tasks: Flow<List<Todo>> = store.data.map { decode(it[tasksKey] ?: "[]") }

    suspend fun add(raw: String) {
        val title = TodoRules.normalizedTitle(raw)
        update { current ->
            check(current.size < TodoRules.MAX_TASKS) { "最多保存 200 项，请先清理旧任务" }
            listOf(Todo(UUID.randomUUID().toString(), title)) + current
        }
    }

    suspend fun toggle(id: String) = update { tasks ->
        tasks.map { if (it.id == id) it.copy(done = !it.done) else it }
    }

    suspend fun delete(id: String) = update { tasks -> tasks.filterNot { it.id == id } }

    // edit serializes concurrent writes; transform reads the latest stored value.
    private suspend fun update(transform: (List<Todo>) -> List<Todo>) {
        store.edit { preferences ->
            val current = decode(preferences[tasksKey] ?: "[]")
            preferences[tasksKey] = encode(transform(current))
        }
    }

    private fun decode(raw: String): List<Todo> {
        val array = JSONArray(raw)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            Todo(item.getString("id"), item.getString("title"), item.getBoolean("done"))
        }
    }

    private fun encode(tasks: List<Todo>): String = JSONArray().apply {
        tasks.forEach { task ->
            put(JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("done", task.done)
            })
        }
    }.toString()
}
