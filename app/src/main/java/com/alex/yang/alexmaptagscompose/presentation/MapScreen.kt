@file:Suppress("SpellCheckingInspection")

package com.alex.yang.alexmaptagscompose.presentation

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alex.yang.alexmaptagscompose.R
import com.alex.yang.alexmaptagscompose.core.hasLocationPermission
import com.alex.yang.alexmaptagscompose.core.isLocationEnabled
import com.alex.yang.alexmaptagscompose.domain.model.Place
import com.alex.yang.alexmaptagscompose.domain.model.toLatLng
import com.alex.yang.alexmaptagscompose.presentation.component.AddMarkerDialog
import com.alex.yang.alexmaptagscompose.presentation.component.PlaceInfoWindow
import com.alex.yang.alexmaptagscompose.ui.theme.AlexMapTagsComposeTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    state: MapViewModel.UiState,
    onPlaceSelected: (String) -> Unit = {},
    getCurrentLocation: suspend () -> Place? = { null },
    onAddUserMarker: (Place, String, String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val showAddMarkerDialog = remember { mutableStateOf(false) }
    val pendingLocation = remember { mutableStateOf<Place?>(null) }

    val defaultPlace = state.places.firstOrNull()
        ?.toLatLng()
        ?: LatLng(25.0478, 121.5170) // 台北車站

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPlace, 12f)
    }

    // 設定地圖圖標樣式
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_clear)
    }
    // 設定地圖類型與圖標樣式
    val properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                mapStyleOptions = mapStyleOptions
            )
        )
    }

    // 請求定位權限
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        val granted = resultMap[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                resultMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!granted) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("尚未授予定位權限，無法使用此功能")
            }
        }
    }
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }

    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    // 監聽地圖移動，當移動時關閉 overlay
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (isMoving) selectedPlace = null
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 地圖元件
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            onMapClick = { selectedPlace = null }
        ) {
            // 標記地點
            state.places.forEach { place ->
                Marker(
                    state = MarkerState(position = place.toLatLng()),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker),
                    title = place.name,
                    snippet = place.description,
                    onClick = {

                        // 把鏡頭移回預設中心
                        // 在 Marker 點擊並 zoom 完成後顯示
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(place.toLatLng(), 16f),
                                durationMs = 500
                            )

                            delay(100)
                            selectedPlace = place
                        }

                        onPlaceSelected(place.id)
                        true  // 回傳 true 阻止預設行為（不顯示 InfoWindow）
                    }
                )
            }

            // 使用者新增的地點
            state.userMarkers.forEach { place ->
                Marker(
                    state = MarkerState(position = place.toLatLng()),
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker),
                    title = place.name,
                    snippet = place.description,
                    onClick = {
                        // 把鏡頭移回預設中心
                        // 在 Marker 點擊並 zoom 完成後顯示
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(place.toLatLng(), 16f),
                                durationMs = 500
                            )

                            delay(100)
                            selectedPlace = place
                        }

                        onPlaceSelected(place.id)
                        true
                    }
                )
            }
        }

        // 客製化 InfoWindow
        selectedPlace?.let { place ->
            Box(modifier = Modifier.fillMaxSize()) {
                PlaceInfoWindow(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-110).dp),
                    place = place,
                    onDismiss = { selectedPlace = null }
                )
            }
        }

        HorizontalFloatingToolbar(
            modifier = Modifier
                .padding(bottom = 60.dp)
                .align(Alignment.BottomCenter),
            expanded = true,
            colors = FloatingToolbarDefaults.standardFloatingToolbarColors()
        ) {
            listOf(
                Icons.Default.MyLocation,
                Icons.Default.LocationOn
            ).forEach { iconRes ->
                IconButton(
                    onClick = {
                        when (iconRes) {
                            Icons.Default.MyLocation -> {
                                // 把鏡頭移回預設中心
                                coroutineScope.launch {
                                    cameraPositionState.move(
                                        CameraUpdateFactory.newLatLngZoom(defaultPlace, 12f)
                                    )
                                }
                            }

                            Icons.Default.LocationOn -> {
                                // 1. 先檢查權限
                                if (!hasLocationPermission) {
                                    locationLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                    return@IconButton
                                }

                                // 2. 檢查是否啟用系統定位
                                if (!context.isLocationEnabled()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("請先在系統設定中開啟「定位服務」")
                                    }
                                    return@IconButton
                                }

                                // 3. 取得目前定位
                                coroutineScope.launch {
                                    val place = getCurrentLocation()

                                    if (place != null) {
                                        // 讓地圖鏡頭移到這個定位點
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(place.toLatLng(), 16f)
                                        )

                                        pendingLocation.value = place
                                        showAddMarkerDialog.value = true
                                    } else {
                                        snackbarHostState.showSnackbar("無法取得目前位置")
                                    }
                                }
                            }

                            else -> Unit
                        }
                    }
                ) {
                    Icon(
                        imageVector = iconRes,
                        contentDescription = null,
                    )
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        state.selectedPlace?.let { place ->
            Text(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = "選取：${place.name}"
            )
        }

        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            hostState = snackbarHostState
        )

        if (showAddMarkerDialog.value && pendingLocation.value != null) {
            AddMarkerDialog(
                visible = showAddMarkerDialog.value && pendingLocation.value != null,
                onConfirm = { name, description ->
                    val base = pendingLocation.value
                    if (base != null && name.isNotBlank()) {
                        val newPlace = base.copy(name = name, description = description)
                        onAddUserMarker(base, name, description)
                        selectedPlace = newPlace    // 顯示 InfoWindow
                    }

                    showAddMarkerDialog.value = false
                    pendingLocation.value = null
                },
                onDismiss = {
                    showAddMarkerDialog.value = false
                    pendingLocation.value = null
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun MapScreenPreview() {
    AlexMapTagsComposeTheme {
        MapScreen(
            state = MapViewModel.UiState()
        )
    }
}