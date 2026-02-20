package com.example.consolas.data.network

import com.example.consolas.data.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val URL_BASE = "http://localhost:8081/console"

    val retrofitService : ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }


    private fun getRetrofit(): Retrofit = Retrofit
        .Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

