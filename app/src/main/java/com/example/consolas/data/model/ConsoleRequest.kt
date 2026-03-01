package com.example.consolas.data.model

import com.example.consolas.domain.model.Console
import com.google.gson.annotations.SerializedName

data class ConsoleRequest(
    @SerializedName("name") val name: String,
    @SerializedName("releasedate") val releasedate: String,
    @SerializedName("company") val company: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("price") val price: Double,
    @SerializedName("favorite") val favorite: Boolean,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("nativeGames") val nativeGames: List<ResponseGame> = emptyList(),
    @SerializedName("adaptedGames") val adaptedGames: List<ResponseGame> = emptyList()
)

fun Console.toRequest(): ConsoleRequest = ConsoleRequest(
    name = name,
    releasedate = releasedate,
    company = company,
    description = description,
    image = image,
    price = price,
    favorite = favorite,
    userEmail = userEmail,
    nativeGames = nativeGames.map { it.toResponse() },
    adaptedGames = adaptedGames.map { it.toResponse() }
)

fun com.example.consolas.domain.model.Game.toResponse(): ResponseGame = ResponseGame().apply {
    title = this@toResponse.title
    releaseDate = this@toResponse.releaseDate
    description = this@toResponse.description
    image = this@toResponse.image
}