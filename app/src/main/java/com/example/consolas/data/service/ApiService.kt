package com.example.consolas.data.service

import com.example.consolas.data.model.ConsoleRequest
import com.example.consolas.data.model.GameRequest
import com.example.consolas.data.model.ResponseConsole
import com.example.consolas.data.model.ResponseGame
import com.example.consolas.domain.model.UpdateConsole
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("console")
    suspend fun getConsoles(): Response<List<ResponseConsole>> // Cambiado de List<String> a List<ResponseConsole>

    @GET("console/{name}")
    suspend fun getConsole(@Path("name") name: String): Response<ResponseConsole>

    @POST("console")
    suspend fun addConsole(@Body console: ConsoleRequest): Response<ResponseConsole>

    @POST("console/{name}/game")
    suspend fun addGameToConsole(
        @Path("name") consoleName: String,
        @Query("isNative") isNative: Boolean,
        @Body game: GameRequest
    ): Response<ResponseBody>
    @PATCH("console/{name}")
    suspend fun updateConsole(
        @Path("name") name: String,
        @Body update: UpdateConsole
    ): Response<ResponseConsole>

    @DELETE("console/{name}")
    suspend fun deleteConsole(@Path("name") name: String): Response<Unit>


}