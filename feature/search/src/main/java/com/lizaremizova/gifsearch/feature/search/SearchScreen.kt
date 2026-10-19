package com.lizaremizova.gifsearch.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.lizaremizova.gifsearch.core.ui.components.ErrorView
import com.lizaremizova.gifsearch.core.ui.components.FullScreenLoading
import com.lizaremizova.gifsearch.core.ui.components.toUserMessage
import com.lizaremizova.gifsearch.core.ui.theme.GifSearchTheme
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.model.GifImage
import kotlinx.coroutines.flow.flowOf

/** Stateful entry point: connects the ViewModel to the stateless [SearchScreen]. */
@Composable
internal fun SearchRoute(
    onGifClick: (gifId: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val gifs = viewModel.gifs.collectAsLazyPagingItems()

    SearchScreen(
        query = query,
        onQueryChange = viewModel::onQueryChange,
        gifs = gifs,
        isOnline = isOnline,
        onGifClick = onGifClick,
    )
}

@Composable
internal fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    gifs: LazyPagingItems<Gif>,
    isOnline: Boolean,
    onGifClick: (gifId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (!isOnline) {
                Text(
                    text = "You're offline",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )
            }
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search GIPHY") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
            )

            val refresh = gifs.loadState.refresh
            // Retry failed loads once the connection comes back; retry() is a no-op when nothing failed.
            LaunchedEffect(isOnline) {
                if (isOnline) gifs.retry()
            }
            if (refresh is LoadState.Loading && gifs.itemCount > 0) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            when {
                gifs.itemCount > 0 -> GifGrid(gifs, onGifClick)
                refresh is LoadState.Loading -> FullScreenLoading()
                refresh is LoadState.Error -> ErrorView(refresh.error.toUserMessage(), onRetry = gifs::retry)
                query.isBlank() -> ErrorView("Type something to search GIFs", onRetry = null)
                // shortcut: also flashes for ~400 ms while the very first query is debounced; fine for now.
                else -> ErrorView("Nothing found for \"$query\"", onRetry = null)
            }
        }
    }
}

@Composable
private fun GifGrid(gifs: LazyPagingItems<Gif>, onGifClick: (gifId: String) -> Unit) {
    LazyVerticalGrid(
        // Adaptive columns give more items per row in landscape automatically.
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // No item keys: Giphy can return the same GIF on two pages, and duplicate keys crash the grid.
        items(count = gifs.itemCount) { index ->
            val gif = gifs[index] ?: return@items
            GifGridItem(gif = gif, onClick = { onGifClick(gif.id) })
        }

        when (val append = gifs.loadState.append) {
            is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorView(append.error.toUserMessage(), onRetry = gifs::retry)
            }
            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun GifGridItem(gif: Gif, onClick: () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        AsyncImage(
            model = gif.preview.url,
            contentDescription = gif.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
        )
    }
}

private val previewGifs = List(6) { i ->
    val image = GifImage(url = "", width = 200, height = 200)
    Gif(id = "$i", title = "Cat $i", username = null, pageUrl = "", preview = image, original = image)
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenPreview() {
    GifSearchTheme(dynamicColor = false) {
        SearchScreen(
            query = "cats",
            onQueryChange = {},
            gifs = flowOf(PagingData.from(previewGifs)).collectAsLazyPagingItems(),
            isOnline = true,
            onGifClick = {},
        )
    }
}
