package com.example.pockettasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) { TaskScreen() }
            }
        }
    }
}

@Composable
private fun TaskScreen(vm: TodoViewModel = viewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    val adding by vm.adding.collectAsStateWithLifecycle()
    var input by rememberSaveable { mutableStateOf("") }
    var filterName by rememberSaveable { mutableStateOf(TaskFilter.ALL.name) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val filter = TaskFilter.valueOf(filterName)
    val canEdit = state.loaded && state.readError == null

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(contentPadding).imePadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("口袋待办", style = MaterialTheme.typography.headlineMedium)
            Text("只保存在本机 · 无需登录")
            Text("${state.tasks.count { !it.done }} 项待完成 / 共 ${state.tasks.size} 项")
            OutlinedTextField(
                value = input,
                onValueChange = { if (it.length <= TodoRules.MAX_TITLE_LENGTH) input = it },
                label = { Text("输入任务，最多 80 字符") },
                singleLine = true,
                enabled = canEdit && !adding,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                enabled = canEdit && input.isNotBlank() && !adding,
                onClick = {
                    // Disabling prevents accidental duplicate taps during a disk write.
                    vm.add(input) { input = "" }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (adding) "保存中…" else "添加任务") }
            if (message != null) {
                Text(message.orEmpty(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = vm::dismissMessage) { Text("知道了") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskFilter.entries.forEach { item ->
                    FilterChip(
                        selected = item == filter,
                        onClick = { filterName = item.name },
                        label = { Text(item.label) }
                    )
                }
            }
            if (!state.loaded) Text(state.readError ?: "正在读取…")
            val visible = TodoRules.visible(state.tasks, filter)
            if (state.loaded && visible.isEmpty()) Text("这里还没有任务")
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visible, key = { it.id }) { task ->
                    Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.medium) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.done,
                                enabled = canEdit,
                                onCheckedChange = { vm.toggle(task.id) },
                                modifier = Modifier.semantics { contentDescription = "完成状态：${task.title}" }
                            )
                            Text(
                                task.title,
                                modifier = Modifier.weight(1f),
                                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                            )
                            TextButton(enabled = canEdit, onClick = { pendingDeleteId = task.id }) { Text("删除") }
                        }
                    }
                }
            }
        }
    }
    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("删除这项任务？") },
            text = { Text("删除后无法恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    pendingDeleteId?.let(vm::delete)
                    pendingDeleteId = null
                }) { Text("删除") }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text("取消") } }
        )
    }
}
