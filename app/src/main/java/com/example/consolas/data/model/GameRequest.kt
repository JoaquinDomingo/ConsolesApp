package com.example.consolas.data.model

import com.example.consolas.domain.model.Game
import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para enviar un nuevo juego a la API
 */
data class GameRequest(
    @SerializedName("title") val title: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String
)

/**
 * Mapper para convertir de Dominio a Request de API
 */
fun Game.toRequest(): GameRequest = GameRequest(
    title = this.title,
    releaseDate = this.releaseDate,
    description = this.description,
    image = this.image
)