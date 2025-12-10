package com.alex.yang.alexmaptagscompose.domain.usecae

import com.alex.yang.alexmaptagscompose.domain.repository.PlaceRepository
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
class ObservePlacesUseCase @Inject constructor(
    private val repository: PlaceRepository
) {
    operator fun invoke() = repository.observePlaces()
}