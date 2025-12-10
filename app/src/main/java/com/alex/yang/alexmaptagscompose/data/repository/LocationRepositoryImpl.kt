package com.alex.yang.alexmaptagscompose.data.repository

import android.util.Log
import com.alex.yang.alexmaptagscompose.domain.model.Place
import com.alex.yang.alexmaptagscompose.domain.repository.LocationPermissionChecker
import com.alex.yang.alexmaptagscompose.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by AlexYang on 2025/12/10.
 *
 *
 */
class LocationRepositoryImpl @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient,
    private val permissionChecker: LocationPermissionChecker
) : LocationRepository {
    override suspend fun getCurrentLocation(): Result<Place> {
        if (!permissionChecker.hasLocationPermission()) {
            Log.d("DEBUG", "[DEBUG]權限檢查失敗")

            return Result.failure(SecurityException("缺少定位權限"))
        }

        return runCatching {
            suspendCancellableCoroutine { continuation ->
                Log.d("DEBUG", "[DEBUG] 開始請求位置...")

                val cancellationTokenSource = CancellationTokenSource()
                locationProviderClient
                    .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token
                    )
                    .addOnSuccessListener { location ->
                        Log.d("DEBUG", "[DEBUG]位置請求成功: $location")
                        if (!continuation.isActive) return@addOnSuccessListener

                        val place = Place(
                            id = location.time.toString(),
                            name = "",
                            description = "",
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracyMeters = location.accuracy,
                            timeMillis = location.time
                        )

                        continuation.resume(place)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("DEBUG", "[DEBUG]位置請求失敗: ${exception.message}", exception)
                        if (!continuation.isActive) return@addOnFailureListener

                        continuation.resumeWithException(exception)
                    }

                // coroutine 被取消時，取消 Location 請求
                continuation.invokeOnCancellation {
                    Log.d("DEBUG", "[DEBUG] Coroutine cancel，取消定位請求")
                    cancellationTokenSource.cancel()
                }
            }
        }
    }
}