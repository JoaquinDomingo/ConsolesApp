package com.example.consolas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ConsoleEntity::class, MessageEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun consoleDao(): ConsoleDao
    abstract fun messageDao(): MessageDao
}
