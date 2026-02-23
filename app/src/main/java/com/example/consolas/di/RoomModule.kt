package com.example.consolas.di

import android.content.Context
import androidx.room.Room
import com.example.consolas.data.local.AppDatabase
import com.example.consolas.data.local.ConsoleDao
import com.example.consolas.data.local.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "consoles_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideConsoleDao(db: AppDatabase): ConsoleDao = db.consoleDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()
}
