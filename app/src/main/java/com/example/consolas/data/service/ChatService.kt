package com.example.consolas.data.service

import android.util.Log
import com.example.consolas.data.local.SessionManager
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatService @Inject constructor(
    private val client: OkHttpClient,
    private val sessionManager: SessionManager
) {
    private var webSocket: WebSocket? = null
    private val URL = "wss://https://prothalloid-unsceptically-spencer.ngrok-free.dev//chat"

    fun connect(onMessageReceived: (String) -> Unit) {
        val email = sessionManager.userEmail()
        val token = sessionManager.getUserToken() ?: ""

        val request = Request.Builder()
            .url("$URL?email=$email")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("CHAT", " Conectado")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                onMessageReceived(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("CHAT", " Error: ${t.message}")
            }
        })
    }

    fun sendMessage(json: String) {
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, null)
        webSocket = null
    }
}