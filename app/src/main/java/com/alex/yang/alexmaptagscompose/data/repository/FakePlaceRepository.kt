package com.alex.yang.alexmaptagscompose.data.repository

import com.alex.yang.alexmaptagscompose.domain.model.Place
import com.alex.yang.alexmaptagscompose.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
class FakePlaceRepository : PlaceRepository {
    private val placesFlow = MutableStateFlow(
        listOf(
            Place(
                id = "1",
                name = "台北車站",
                latitude = 25.0478,
                longitude = 121.5170,
                description = "交通樞紐"
            ),
            Place(
                id = "2",
                name = "台北 101",
                latitude = 25.033968,
                longitude = 121.564468,
                description = "地標建築"
            ),
            Place(
                id = "3",
                name = "中正紀念堂",
                latitude = 25.0340,
                longitude = 121.5210,
                description = "文化景點"
            )
        )
    )
    override fun observePlaces(): Flow<List<Place>> {
        return placesFlow.asStateFlow()
    }
}