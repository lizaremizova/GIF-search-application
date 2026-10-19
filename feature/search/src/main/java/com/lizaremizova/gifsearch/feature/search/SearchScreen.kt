package com.lizaremizova.gifsearch.feature.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.lizaremizova.gifsearch.core.ui.theme.ThemeMode
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.model.GifImage
import kotlinx.coroutines.flow.flowOf

/** Stateful entry point: connects the ViewModel to the stateless [SearchScreen]. */
@Composable
internal fun SearchRoute(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
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
        themeMode = themeMode,
        onThemeModeChange = onThemeModeChange,
        onGifClick = onGifClick,
    )
}

@Composable
internal fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    gifs: LazyPagingItems<Gif>,
    isOnline: Boolean,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onGifClick: (gifId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "GIFs",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                ThemeMenu(themeMode, onThemeModeChange)
            }

            SearchField(query, onQueryChange)

            if (!isOnline) {
                Text(
                    text = "You're offline",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }

            val refresh = gifs.loadState.refresh
            // Retry failed loads once the connection comes back; retry() is a no-op when nothing failed.
            LaunchedEffect(isOnline) {
                if (isOnline) gifs.retry()
            }
            if (refresh is LoadState.Loading && gifs.itemCount > 0) {
                LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }

            when {
                gifs.itemCount > 0 -> GifGrid(gifs, showTrendingLabel = query.isBlank(), onGifClick)
                refresh is LoadState.Loading -> FullScreenLoading()
                refresh is LoadState.Error -> ErrorView(refresh.error.toUserMessage(), onRetry = gifs::retry)
                query.isBlank() -> ErrorView("Nothing here yet", onRetry = null)
                else -> ErrorView("Nothing found for \"$query\"", onRetry = null)
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = { Text("Search GIPHY") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    )
}

@Composable
private fun ThemeMenu(themeMode: ThemeMode, onThemeModeChange: (ThemeMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(themeMode.icon, contentDescription = "Theme: ${themeMode.name}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = mode.name,
                            fontWeight = if (mode == themeMode) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    leadingIcon = { Icon(mode.icon, contentDescription = null) },
                    onClick = {
                        onThemeModeChange(mode)
                        expanded = false
                    },
                )
            }
        }
    }
}

private val ThemeMode.icon: ImageVector
    get() = when (this) {
        ThemeMode.System -> Icons.Outlined.BrightnessAuto
        ThemeMode.Light -> Icons.Outlined.LightMode
        ThemeMode.Dark -> Icons.Outlined.DarkMode
    }

@Composable
private fun GifGrid(
    gifs: LazyPagingItems<Gif>,
    showTrendingLabel: Boolean,
    onGifClick: (gifId: String) -> Unit,
) {
    val state = rememberLazyStaggeredGridState()
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(state.isScrollInProgress) {
        if (state.isScrollInProgress) keyboard?.hide()
    }

    LazyVerticalStaggeredGrid(
        state = state,
        // Adaptive columns give more items per row in landscape automatically.
        columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        if (showTrendingLabel) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text = "Trending",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }

        // No item keys: Giphy can return the same GIF on two pages, and duplicate keys crash the grid.
        items(count = gifs.itemCount) { index ->
            val gif = gifs[index] ?: return@items
            GifGridItem(gif = gif, onClick = { onGifClick(gif.id) })
        }

        when (val append = gifs.loadState.append) {
            is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
                ErrorView(append.error.toUserMessage(), onRetry = gifs::retry)
            }
            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun GifGridItem(gif: Gif, onClick: () -> Unit) {
    AsyncImage(
        model = gif.preview.url,
        contentDescription = gif.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            // Keep the real shape, but clamp extremes so one GIF can't take over the column.
            .aspectRatio(gif.preview.aspectRatio.coerceIn(0.6f, 1.8f))
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
    )
}

private val previewGifs = List(8) { i ->
    val image = GifImage(url = "", width = 200, height = if (i % 3 == 0) 280 else 160)
    Gif(id = "$i", title = "Cat $i", username = null, pageUrl = "", preview = image, original = image)
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SearchScreenPreview() {
    GifSearchTheme {
        SearchScreen(
            query = "",
            onQueryChange = {},
            gifs = flowOf(PagingData.from(previewGifs)).collectAsLazyPagingItems(),
            isOnline = false,
            themeMode = ThemeMode.System,
            onThemeModeChange = {},
            onGifClick = {},
        )
    }
}
