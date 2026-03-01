package com.example.consolas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderEmail: String,    // <--- AÑADIR ESTO
    val receiverEmail: String,  // <--- AÑADIR ESTO
    val text: String,
    val timestamp: Long,
    val userEmail: String       // Email del dueño de la sesión
)
