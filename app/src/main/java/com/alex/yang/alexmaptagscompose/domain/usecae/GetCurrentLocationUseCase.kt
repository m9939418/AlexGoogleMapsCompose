package com.alex.yang.alexmaptagscompose.domain.usecae

import com.alex.yang.alexmaptagscompose.domain.model.Place
import com.alex.yang.alexmaptagscompose.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Result<Place> = repository.getCurrentLocation()
}