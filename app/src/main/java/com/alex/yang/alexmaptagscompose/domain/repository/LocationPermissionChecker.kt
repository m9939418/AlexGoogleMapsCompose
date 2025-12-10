package com.alex.yang.alexmaptagscompose.domain.repository

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
interface LocationPermissionChecker {
    fun hasLocationPermission(): Boolean
}