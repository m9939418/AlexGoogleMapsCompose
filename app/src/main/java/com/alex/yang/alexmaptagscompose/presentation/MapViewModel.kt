package com.alex.yang.alexmaptagscompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.yang.alexmaptagscompose.domain.usecae.ObservePlacesUseCase
import com.alex.yang.alexmaptagscompose.domain.model.Place
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
    private val useCase: ObservePlacesUseCase
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
                    _uiState.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    data class UiState(
        val places: List<Place> = emptyList(),
        val selectedPlaceId: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) {
        val selectedPlace: Place? get() = places.firstOrNull { it.id == selectedPlaceId }
    }
}