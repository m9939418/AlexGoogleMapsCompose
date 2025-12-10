package com.alex.yang.alexmaptagscompose.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.alex.yang.alexmaptagscompose.domain.repository.LocationPermissionChecker
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
class LocationPermissionCheckerImpl @Inject constructor(
    private val context: Context
) : LocationPermissionChecker {
    override fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }
}