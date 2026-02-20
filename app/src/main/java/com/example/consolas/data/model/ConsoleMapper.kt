package com.example.consolas.data.model

import com.example.consolas.domain.model.Console

fun ResponseConsole.toDomain(): Console {
    return Console(
        name = this.nombre ?: "",
        releasedate = this.releasedate ?: "",
        company = this.company ?: "",
        description = this.description ?: "",
        image = this.image ?: ""
    )
}