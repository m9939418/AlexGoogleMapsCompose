package com.alex.yang.alexmaptagscompose.presentation.di

import android.content.Context
import com.alex.yang.alexmaptagscompose.data.repository.LocationPermissionCheckerImpl
import com.alex.yang.alexmaptagscompose.data.repository.LocationRepositoryImpl
import com.alex.yang.alexmaptagscompose.domain.repository.LocationPermissionChecker
import com.alex.yang.alexmaptagscompose.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): LocationPermissionChecker {
        return LocationPermissionCheckerImpl(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        locationProviderClient: FusedLocationProviderClient,
        permissionChecker: LocationPermissionChecker
    ): LocationRepository {
        return LocationRepositoryImpl(locationProviderClient, permissionChecker)
    }
}