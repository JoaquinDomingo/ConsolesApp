package com.example.consolas.data.local

import com.example.consolas.domain.model.Console

fun ConsoleEntity.toDomain(ownerEmail: String? = null): Console = Console(
    name = this.name,
    releasedate = this.releasedate,
    company = this.company,
    description = this.description,
    image = this.image,
    price = this.price,
    favorite = this.favorite,
    nativeGames = this.nativeGames,
    adaptedGames = this.adaptedGames,
    userEmail = ownerEmail ?: this.userEmail
)


fun Console.toEntity(userEmail: String): ConsoleEntity = ConsoleEntity(
    name = this.name,
    userEmail = userEmail,
    releasedate = this.releasedate,
    company = this.company,
    description = this.description,
    image = this.image,
    price = this.price,
    favorite = this.favorite,
    nativeGames = this.nativeGames,
    adaptedGames = this.adaptedGames
)