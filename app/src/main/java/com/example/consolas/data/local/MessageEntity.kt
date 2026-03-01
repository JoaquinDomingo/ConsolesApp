package com.example.consolas.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index(value = ["senderEmail", "receiverEmail", "text", "timestamp"], unique = true)]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderEmail: String,
    val receiverEmail: String,
    val text: String,
    val timestamp: Long,
    val userEmail: String // Email del dueño de la sesión actual
)
