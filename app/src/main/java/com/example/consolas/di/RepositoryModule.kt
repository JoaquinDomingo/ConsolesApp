package com.example.consolas.di

import com.example.consolas.data.repository.ConsoleRepositoryImpl
import com.example.consolas.data.repository.Repository
import com.example.consolas.domain.repository.ConsoleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConsoleRepository(
        repositoryImpl: ConsoleRepositoryImpl
    ): ConsoleRepository
}