package com.example.pockettasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

// Development-only exercise: use HTTPS and a deployed host for real releases.
private suspend fun fetchTip(): String = withContext(Dispatchers.IO) {
    val connection = URL("http://10.0.2.2:8000/tip").openConnection() as HttpURLConnection
    try {
        connection.requestMethod = "GET"
        connection.connectTimeout = 5_000
        connection.readTimeout = 5_000
        val code = connection.responseCode
        if (code !in 200..299) throw IOException("HTTP $code")
        val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        JSONObject(body).getString("text")
    } finally {
        connection.disconnect()
    }
}

@Composable
fun NetworkLabScreen() {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("点击按钮，从你的电脑读取一句话") }
    var loading by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("联网练习")
        Text(text)
        Button(enabled = !loading, onClick = {
            loading = true
            scope.launch {
                try {
                    text = fetchTip()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    text = "请求失败：${error.javaClass.simpleName}。检查服务、地址、权限和开发环境 HTTP 配置。"
                } finally {
                    loading = false
                }
            }
        }) { Text(if (loading) "请求中…" else "获取提示") }
    }
}
