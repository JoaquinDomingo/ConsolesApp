package com.example.consolas.data.local

import androidx.room.TypeConverter
import com.example.consolas.domain.model.Game
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGameList(value: List<Game>?): String {
        return gson.toJson(value ?: emptyList<Game>())
    }

    @TypeConverter
    fun toGameList(value: String): List<Game> {
        val listType = object : TypeToken<List<Game>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}