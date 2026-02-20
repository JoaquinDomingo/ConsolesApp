package com.example.consolas.data.service

import android.util.Log
import com.example.consolas.data.model.ResponseConsole
import com.example.consolas.data.network.RetrofitInstance
import retrofit2.Response

class ConsoleService {

    suspend fun getConsole(): ResponseConsole?{

        val nameConsole : String = "POR IMPLEMENTAR"
        val response: Response<ResponseConsole> = RetrofitInstance.retrofitService.getConsole(nameConsole)
        if (!response.isSuccessful)
            showError()
        return  response.body()
    }

    private fun showError(){
        Log.i("TAG-ERROR", "Error al obtener la consola")
    }
}