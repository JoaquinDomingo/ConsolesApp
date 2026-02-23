package com.example.consolas.data.model

import com.example.consolas.domain.model.Game
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseConsole (){
    @SerializedName("name")

    var nombre: String? = null


    @SerializedName("releasedate")

    var releasedate: String? = null

    @SerializedName("company")

    var company: String? = null

    @SerializedName("description")

    var description: String? = null

    @SerializedName("image")

    var image: String? = null

    @SerializedName("nativeGames")
    var nativeGames: List<ResponseGame>? = null

    @SerializedName("adaptedGames")
    var adaptedGames: List<ResponseGame>? = null
}