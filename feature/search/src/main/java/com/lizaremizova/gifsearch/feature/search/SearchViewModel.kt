package com.lizaremizova.gifsearch.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.lizaremizova.gifsearch.core.network.ConnectivityObserver
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.repository.GifRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: GifRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    /** Kept in SavedStateHandle so the query survives rotation and process death. */
    val query: StateFlow<String> = savedStateHandle.getStateFlow(KEY_QUERY, "")

    // TODO: build from `query`:
    //  .debounce(400) -> .map { it.trim() } -> .distinctUntilChanged()
    //  -> .flatMapLatest { if blank: PagingData.empty() else repository.search(it) }
    //  -> .cachedIn(viewModelScope)
    val gifs: Flow<PagingData<Gif>> = flowOf(PagingData.empty())

    val isOnline: StateFlow<Boolean> = connectivityObserver.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = true)

    fun onQueryChange(newQuery: String) {
        savedStateHandle[KEY_QUERY] = newQuery
    }

    private companion object {
        const val KEY_QUERY = "query"
    }
}
