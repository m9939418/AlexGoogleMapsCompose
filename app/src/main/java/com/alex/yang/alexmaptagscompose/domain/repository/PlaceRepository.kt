package com.alex.yang.alexmaptagscompose.domain.repository

import com.alex.yang.alexmaptagscompose.domain.model.Place
import kotlinx.coroutines.flow.Flow

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
interface PlaceRepository {
    fun observePlaces(): Flow<List<Place>>
}