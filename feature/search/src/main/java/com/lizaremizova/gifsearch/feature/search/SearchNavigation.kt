package com.lizaremizova.gifsearch.feature.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object SearchDestination

fun NavGraphBuilder.searchScreen(onGifClick: (gifId: String) -> Unit) {
    composable<SearchDestination> {
        SearchRoute(onGifClick = onGifClick)
    }
}
