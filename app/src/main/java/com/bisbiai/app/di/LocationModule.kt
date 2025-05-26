package com.bisbiai.app.di

import android.app.Application
import android.content.Context
import com.bisbiai.app.data.location.ILocationService
import com.bisbiai.app.data.location.LocationService
import com.bisbiai.app.data.location.current_location.DefaultLocationTracker
import com.bisbiai.app.data.location.current_location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context,
    ): ILocationService = LocationService(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(
        application: Application,
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun providesLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application,
    ): LocationTracker = DefaultLocationTracker(
        fusedLocationProviderClient = fusedLocationProviderClient,
        application = application
    )
}