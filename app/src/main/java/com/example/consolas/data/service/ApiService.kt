package com.example.consolas.data.service

import com.example.consolas.data.model.ResponseConsole
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("console")
    suspend fun getConsoles(): Response<List<ResponseConsole>> // Cambiado de List<String> a List<ResponseConsole>

    @GET("console/{name}")
    suspend fun getConsole(@Path("name") name: String): Response<ResponseConsole>
}