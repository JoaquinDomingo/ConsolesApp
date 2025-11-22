package com.example.consolas.dao

import com.example.consolas.interfaces.InterfaceDao
import com.example.consolas.models.Console
import com.example.consolas.objects_models.Repository

class DaoConsolas private constructor(): InterfaceDao{
    companion object {
        val myDao : DaoConsolas by lazy {
            DaoConsolas()
        }
    }

    override
    fun getDataConsoles(): List<Console> = Repository.listConsoles


}