package com.lizaremizova.gifsearch.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.lizaremizova.gifsearch.core.network.ConnectivityObserver
import com.lizaremizova.gifsearch.domain.repository.GifRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val repository = mockk<GifRepository> {
        every { search(any()) } returns flowOf(PagingData.empty())
        every { trending() } returns flowOf(PagingData.empty())
    }
    private val connectivity = mockk<ConnectivityObserver> {
        every { isOnline } returns flowOf(true)
    }

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `searches only the last query after typing stops, trimmed`() = runTest {
        val viewModel = SearchViewModel(SavedStateHandle(), repository, connectivity)
        backgroundScope.launch { viewModel.gifs.collect {} }

        viewModel.onQueryChange("c")
        advanceTimeBy(100)
        viewModel.onQueryChange("ca")
        advanceTimeBy(100)
        viewModel.onQueryChange(" cats ")
        advanceTimeBy(SearchViewModel.DEBOUNCE_MS + 1)
        runCurrent()

        verify(exactly = 1) { repository.search("cats") }
        verify(exactly = 0) { repository.search("c") }
        verify(exactly = 0) { repository.search("ca") }
    }

    @Test
    fun `blank query shows trending instead of searching`() = runTest {
        val viewModel = SearchViewModel(SavedStateHandle(), repository, connectivity)
        backgroundScope.launch { viewModel.gifs.collect {} }

        viewModel.onQueryChange("   ")
        advanceTimeBy(SearchViewModel.DEBOUNCE_MS + 1)
        runCurrent()

        verify(exactly = 1) { repository.trending() }
        verify(exactly = 0) { repository.search(any()) }
    }
}
