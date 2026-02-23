package com.example.consolas.data.model

import com.google.gson.annotations.SerializedName

class ResponseGame (){
    @SerializedName("title")
    var title: String? = null

    @SerializedName("releaseDate")
    var releaseDate: String? = null


    @SerializedName("description")
    var description: String? = null

    @SerializedName("image")
    var image: String? = null
}