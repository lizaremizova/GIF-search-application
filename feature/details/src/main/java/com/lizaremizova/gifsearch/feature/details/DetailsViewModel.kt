package com.lizaremizova.gifsearch.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.lizaremizova.gifsearch.core.ui.components.toUserMessage
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.repository.GifRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(val gif: Gif) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GifRepository,
) : ViewModel() {

    private val gifId: String = savedStateHandle.toRoute<DetailsDestination>().gifId

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = DetailsUiState.Loading
        viewModelScope.launch {
            _uiState.value = repository.getGif(gifId).fold(
                onSuccess = { DetailsUiState.Success(it) },
                onFailure = { DetailsUiState.Error(it.toUserMessage()) },
            )
        }
    }
}
