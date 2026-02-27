package com.example.consolas.data.local

import androidx.room.Entity
import com.example.consolas.domain.model.Game

// Definimos una clave primaria compuesta para que cada usuario tenga su propia copia de la consola
@Entity(tableName = "consoles", primaryKeys = ["name", "userEmail"])
data class ConsoleEntity(
    val name: String,
    val userEmail: String,
    val releasedate: String,
    val company: String,
    val description: String,
    val image: String,
    val price: Double,
    val favorite: Boolean,
    val nativeGames: List<Game>,
    val adaptedGames: List<Game>
)