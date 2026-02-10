package com.example.consolas.domain.interfaces

import com.example.consolas.domain.model.Console

interface InterfaceDao {

    fun getDataConsoles(): List<Console>
}
