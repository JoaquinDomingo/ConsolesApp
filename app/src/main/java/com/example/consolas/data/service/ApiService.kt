package com.example.consolas.data.service

import com.example.consolas.data.model.ConsoleRequest
import com.example.consolas.data.model.ResponseConsole
import com.example.consolas.domain.model.UpdateConsole
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("console")
    suspend fun getConsoles(): Response<List<ResponseConsole>> // Cambiado de List<String> a List<ResponseConsole>

    @GET("console/{name}")
    suspend fun getConsole(@Path("name") name: String): Response<ResponseConsole>

    @POST("console")
    suspend fun addConsole(@Body console: ConsoleRequest): Response<ResponseConsole>

    @PUT("console/{name}")
    suspend fun updateConsole(
        @Path("name") name: String,
        @Body update: UpdateConsole
    ): Response<ResponseConsole>

    @DELETE("console/{name}")
    suspend fun deleteConsole(@Path("name") name: String): Response<Unit>


}