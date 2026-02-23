package com.example.consolas.data.local

import com.example.consolas.domain.model.Console

fun ConsoleEntity.toDomain(): Console = Console(
    name = name,
    releasedate = releasedate,
    company = company,
    description = description,
    image = image,
    price = price,
    favorite = favorite,
    nativeGames = emptyList(),
    adaptedGames = emptyList()
)

fun Console.toEntity(userEmail: String): ConsoleEntity = ConsoleEntity(
    name = name,
    releasedate = releasedate,
    company = company,
    description = description,
    image = image,
    price = price,
    favorite = favorite,
    userEmail = userEmail
)
