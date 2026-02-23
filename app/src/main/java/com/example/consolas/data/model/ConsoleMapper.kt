package com.example.consolas.data.model

import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game

fun ResponseConsole.toDomain(): Console {
    return Console(
        name = this.nombre ?: "",
        releasedate = this.releasedate ?: "",
        company = this.company ?: "",
        description = this.description ?: "",
        image = this.image ?: "",
        nativeGames = this.nativeGames?.map { it.toDomain() } ?: emptyList(),
        adaptedGames = this.adaptedGames?.map { it.toDomain() } ?: emptyList()
    )
}

fun ResponseGame.toDomain() = Game(
    title = this.title ?: "",
    releaseDate = this.releaseDate ?: "",
    description = this.description ?: "",
    image = this.image ?: ""
)