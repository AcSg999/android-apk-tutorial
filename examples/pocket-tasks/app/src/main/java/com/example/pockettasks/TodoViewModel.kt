package com.example.pockettasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodoUiState(
    val tasks: List<Todo> = emptyList(),
    val loaded: Boolean = false,
    val readError: String? = null
)

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TodoRepository(application)
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    private val _adding = MutableStateFlow(false)
    val adding = _adding.asStateFlow()

    val uiState = repository.tasks
        .map { TodoUiState(tasks = it, loaded = true) }
        .catch { error ->
            if (error is CancellationException) throw error
            // Never replace unreadable data with an empty list and overwrite it.
            emit(TodoUiState(readError = "读取失败，已停止修改。请保留应用数据并重启；仍失败时联系开发者。"))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodoUiState())

    fun add(raw: String, onSuccess: () -> Unit) {
        if (_adding.value) return
        _adding.value = true
        mutate {
            try {
                repository.add(raw)
                onSuccess()
            } finally {
                _adding.value = false
            }
        }
    }
    fun toggle(id: String) = mutate { repository.toggle(id) }
    fun delete(id: String) = mutate { repository.delete(id) }
    fun dismissMessage() { _message.value = null }

    private fun mutate(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _message.value = when (error) {
                    is IllegalArgumentException, is IllegalStateException -> error.message
                    else -> "保存失败，请检查可用空间后重试"
                }
            }
        }
    }
}
