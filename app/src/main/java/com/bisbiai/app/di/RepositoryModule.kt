package com.bisbiai.app.di

import com.bisbiai.app.data.auth.AuthRepositoryImpl
import com.bisbiai.app.data.local.ObjectDetectionDbRepositoryImpl
import com.bisbiai.app.data.local.UserProgressRepositoryImpl
import com.bisbiai.app.data.remote.AzureRepositoryImpl
import com.bisbiai.app.domain.repository.AuthRepository
import com.bisbiai.app.domain.repository.AzureRepository
import com.bisbiai.app.domain.repository.ObjectDetectionDbRepository
import com.bisbiai.app.domain.repository.UserProgressRepository
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
    abstract fun bindAzureRepository(
        azureRepositoryImpl: AzureRepositoryImpl
    ): AzureRepository

    @Binds
    @Singleton
    abstract fun bindObjectDetectionDbRepository(
        objectDetectionDbRepositoryImpl: ObjectDetectionDbRepositoryImpl
    ): ObjectDetectionDbRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserProgressRepository(
        userProgressRepositoryImpl: UserProgressRepositoryImpl
    ): UserProgressRepository
}