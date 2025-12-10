package com.alex.yang.alexmaptagscompose.domain.repository

import com.alex.yang.alexmaptagscompose.domain.model.Place

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): Result<Place>
}