package com.alex.yang.alexmaptagscompose.domain.model

import com.google.android.gms.maps.model.LatLng

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
data class Place(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String
)

fun Place.toLatLng(): LatLng = LatLng(latitude, longitude)

