package com.example.consolas.di

import com.example.consolas.data.repository.ConsoleRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindLocalRepository(
        repositoryImpl: LocalRepositoryImpl
    ): LocalRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        repositoryImpl: MessageRepositoryImpl
    ): MessageRepository

}