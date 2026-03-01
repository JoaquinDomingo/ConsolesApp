package com.example.consolas.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("sender") val sender: String,
    @SerializedName("receiver") val receiver: String,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

data class UserResponse(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String
)