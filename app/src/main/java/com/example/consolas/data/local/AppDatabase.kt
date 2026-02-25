package com.example.consolas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ConsoleEntity::class, MessageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun consoleDao(): ConsoleDao
    abstract fun messageDao(): MessageDao
}
