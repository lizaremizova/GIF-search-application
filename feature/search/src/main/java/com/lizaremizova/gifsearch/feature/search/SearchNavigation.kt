package com.lizaremizova.gifsearch.feature.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lizaremizova.gifsearch.core.ui.theme.ThemeMode
import kotlinx.serialization.Serializable

@Serializable
data object SearchDestination

fun NavGraphBuilder.searchScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onGifClick: (gifId: String) -> Unit,
) {
    composable<SearchDestination> {
        SearchRoute(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
            onGifClick = onGifClick,
        )
    }
}
