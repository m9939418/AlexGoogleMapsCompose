package com.alex.yang.alexmaptagscompose.presentation.di

import com.alex.yang.alexmaptagscompose.data.repository.FakePlaceRepository
import com.alex.yang.alexmaptagscompose.domain.repository.PlaceRepository
import com.alex.yang.alexmaptagscompose.domain.usecae.ObservePlacesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object FakeModule {
    @Provides
    @Singleton
    fun providePlaceRepository(): PlaceRepository = FakePlaceRepository()

    @Provides
    @Singleton
    fun provideObservePlacesUseCase(repository: PlaceRepository) =
        ObservePlacesUseCase(repository)
}