package com.alex.yang.alexmaptagscompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.yang.alexmaptagscompose.domain.model.Place
import com.alex.yang.alexmaptagscompose.domain.usecae.GetCurrentLocationUseCase
import com.alex.yang.alexmaptagscompose.domain.usecae.ObservePlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/9.
 *
 *
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val useCase: ObservePlacesUseCase,
    private val locationUseCase: GetCurrentLocationUseCase

) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        observePlaces()
    }

    fun observePlaces() {
        viewModelScope.launch {
            useCase()
                .onStart {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                    )
                }
                .collect { places ->
                    _uiState.value = _uiState.value.copy(
                        places = places,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    fun onPlaceSelected(placeId: String) {
        _uiState.update { it.copy(selectedPlaceId = placeId) }
    }

    suspend fun getCurrentLocation(): Place? {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        return try {
            val result = locationUseCase()

            result.fold(
                onSuccess = { place ->
                    place
                },
                onFailure = { e ->
                    val message = when (e) {
                        is SecurityException -> "尚未取得定位權限"
                        else -> "無法取得目前位置：${e.message ?: "未知錯誤"}"
                    }
                    _uiState.update { state -> state.copy(errorMessage = message) }
                    null
                }
            )
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun addUserMarker(base: Place, name: String, desc: String) {
        val marker = base.copy(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            description = desc
        )

        _uiState.update { current ->
            current.copy(userMarkers = current.userMarkers + marker)
        }
    }

    data class UiState(
        val places: List<Place> = emptyList(),
        val selectedPlaceId: String? = null,
        val userMarkers: List<Place> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) {
        val selectedPlace: Place? get() = places.firstOrNull { it.id == selectedPlaceId }
    }
}