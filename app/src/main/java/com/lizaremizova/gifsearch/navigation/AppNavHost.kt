package com.lizaremizova.gifsearch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lizaremizova.gifsearch.feature.details.detailsScreen
import com.lizaremizova.gifsearch.feature.details.navigateToDetails
import com.lizaremizova.gifsearch.feature.search.SearchDestination
import com.lizaremizova.gifsearch.feature.search.searchScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SearchDestination) {
        searchScreen(onGifClick = navController::navigateToDetails)
        detailsScreen(onBack = { navController.popBackStack() })
    }
}
