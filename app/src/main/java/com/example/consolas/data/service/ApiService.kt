package com.example.consolas.data.service

import com.example.consolas.data.model.*
import com.example.consolas.domain.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- RUTAS PÚBLICAS ---

    @POST("register")
    suspend fun register(@Body user: UserRequest): Response<ResponseBody>

    @POST("login")
    suspend fun login(@Body user: UserRequest): Response<TokenResponse>

    // --- RUTAS PROTEGIDAS (JWT) ---

    @GET("console")
    suspend fun getConsoles(): Response<List<ResponseConsole>>

    @GET("console/{name}")
    suspend fun getConsole(@Path("name") name: String): Response<ResponseConsole>

    @POST("console")
    suspend fun addConsole(@Body console: ConsoleRequest): Response<ResponseBody>

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

    // --- RUTAS DE CHAT E HISTORIAL ---

    /**
     * Obtiene todos los usuarios registrados para poder iniciar una conversación.
     */
    @GET("users")
    suspend fun getUsers(): Response<List<UserResponse>>

    /**
     * Obtiene el historial de mensajes entre el usuario actual y el contacto especificado.
     * @param otherEmail El email del usuario con el que se mantiene la conversación.
     */
    @GET("messages")
    suspend fun getChatHistory(
        @Query("with") otherEmail: String
    ): Response<List<ChatMessage>>
}