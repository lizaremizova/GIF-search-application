package com.lizaremizova.gifsearch.feature.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.lizaremizova.gifsearch.core.ui.components.ErrorView
import com.lizaremizova.gifsearch.core.ui.components.FullScreenLoading
import com.lizaremizova.gifsearch.core.ui.theme.GifSearchTheme
import com.lizaremizova.gifsearch.domain.model.Gif
import com.lizaremizova.gifsearch.domain.model.GifImage

@Composable
internal fun DetailsRoute(
    onBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailsScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::load)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailsScreen(
    uiState: DetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text((uiState as? DetailsUiState.Success)?.gif?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(padding)
        when (uiState) {
            DetailsUiState.Loading -> FullScreenLoading(contentModifier)
            is DetailsUiState.Error -> ErrorView(uiState.message, onRetry, contentModifier)
            is DetailsUiState.Success -> GifDetails(uiState.gif, contentModifier)
        }
    }
}

@Composable
private fun GifDetails(gif: Gif, modifier: Modifier = Modifier) {
    // TODO: in landscape, consider placing the GIF and the info side by side.
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = gif.original.url,
            contentDescription = gif.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(gif.original.aspectRatio),
        )
        Text(gif.title, style = MaterialTheme.typography.titleLarge)
        gif.username?.let { Text("@$it", style = MaterialTheme.typography.bodyMedium) }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DetailsScreenPreview() {
    val image = GifImage(url = "", width = 480, height = 270)
    GifSearchTheme(dynamicColor = false) {
        DetailsScreen(
            uiState = DetailsUiState.Success(
                Gif(id = "1", title = "Dancing cat", username = "giphy", pageUrl = "", preview = image, original = image),
            ),
            onBack = {},
            onRetry = {},
        )
    }
}
