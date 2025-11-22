package com.example.consolas.controller

import android.content.Context
import android.widget.Toast
import com.example.consolas.MainActivity
import com.example.consolas.adapter.AdapterConsolas
import com.example.consolas.dao.DaoConsolas
import com.example.consolas.models.Console

class Controller (val context: Context) {
    lateinit var listConsoles : MutableList<Console>

    init {
        initData()
    }

    fun initData(){
        listConsoles = DaoConsolas.myDao.getDataConsoles().toMutableList()
    }

    fun loggOut(){
        Toast.makeText(context, "He mostrado los datos en pantalla", Toast.LENGTH_SHORT)
            .show()
        listConsoles.forEach {
            println(it)
        }
    }

    fun setAdapter(){
        val myActivity = context as MainActivity
        myActivity.binding.myRecyclerView.adapter = AdapterConsolas(listConsoles)
    }
}