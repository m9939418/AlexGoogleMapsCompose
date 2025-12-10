package com.alex.yang.alexmaptagscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.yang.alexmaptagscompose.presentation.MapScreen
import com.alex.yang.alexmaptagscompose.presentation.MapViewModel
import com.alex.yang.alexmaptagscompose.ui.theme.AlexMapTagsComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlexMapTagsComposeTheme {
                val viewModel = hiltViewModel<MapViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                MapScreen(
                    state = state,
                    onPlaceSelected = viewModel::onPlaceSelected
                )
            }
        }
    }
}