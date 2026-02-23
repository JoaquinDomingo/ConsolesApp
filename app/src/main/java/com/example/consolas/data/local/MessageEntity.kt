package com.example.consolas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val text: String,
    val timestamp: Long,
    val fromUser: Boolean
)
