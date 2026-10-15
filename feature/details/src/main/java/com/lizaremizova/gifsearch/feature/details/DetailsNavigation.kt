package com.lizaremizova.gifsearch.feature.details

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DetailsDestination(val gifId: String)

fun NavController.navigateToDetails(gifId: String) {
    navigate(DetailsDestination(gifId))
}

fun NavGraphBuilder.detailsScreen(onBack: () -> Unit) {
    composable<DetailsDestination> {
        DetailsRoute(onBack = onBack)
    }
}
