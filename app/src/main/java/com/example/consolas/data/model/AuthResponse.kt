package com.example.consolas.data.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class TokenResponse(
    @SerializedName("token") val token: String
)