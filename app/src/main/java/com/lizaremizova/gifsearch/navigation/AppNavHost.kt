package com.lizaremizova.gifsearch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lizaremizova.gifsearch.core.ui.theme.ThemeMode
import com.lizaremizova.gifsearch.feature.details.detailsScreen
import com.lizaremizova.gifsearch.feature.details.navigateToDetails
import com.lizaremizova.gifsearch.feature.search.SearchDestination
import com.lizaremizova.gifsearch.feature.search.searchScreen

@Composable
fun AppNavHost(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SearchDestination) {
        searchScreen(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
            onGifClick = navController::navigateToDetails,
        )
        detailsScreen(onBack = { navController.popBackStack() })
    }
}
