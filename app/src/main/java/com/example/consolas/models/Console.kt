package com.example.consolas.models

class Console (
    var name: String,
    var releasedate: String,
    var company: String,
    var description: String,
    var image: String
) {
    override fun toString(): String {
        return  "Console(name= '${name}, " +
                "releaseDate='${releasedate}, " +
                "company='${company}, " +
                "description='${description}', " +
                "image='${image}'"
    }
}