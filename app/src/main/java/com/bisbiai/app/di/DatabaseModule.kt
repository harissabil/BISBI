package com.bisbiai.app.di

import android.content.Context
import androidx.room.Room
import com.bisbiai.app.data.local.room.ObjectDetectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ObjectDetectionDatabase =
        Room.databaseBuilder(
            context = context,
            klass = ObjectDetectionDatabase::class.java,
            name = "object_detection.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideObjectDetectionDao(database: ObjectDetectionDatabase) =
        database.objectDetectionDao()
}