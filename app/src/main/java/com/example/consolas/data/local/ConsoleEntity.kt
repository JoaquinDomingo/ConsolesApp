package com.example.consolas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consoles")
data class ConsoleEntity(
    @PrimaryKey val name: String,
    val releasedate: String,
    val company: String,
    val description: String,
    val image: String,
    val price: Double,
    val favorite: Boolean,
    val userEmail: String
)
