package com.example.consolas.domain.model

data class Console (
    var name: String,
    var releasedate: String,
    var company: String,
    var description: String,
    var image: String,
    val userEmail: String,
    val nativeGames: List<Game> = emptyList(),
    val adaptedGames: List<Game> = emptyList(),
    var price: Double,
    var favorite: Boolean
) {
    override fun toString(): String {
        return  "Console(name= '${name}, " +
                "releaseDate='${releasedate}, " +
                "company='${company}, " +
                "description='${description}', " +
                "image='${image}'"
                "price='{$price}'"+
                "favorite='{$favorite}'"
    }
}