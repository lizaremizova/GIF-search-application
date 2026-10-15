package com.lizaremizova.gifsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lizaremizova.gifsearch.core.ui.theme.GifSearchTheme
import com.lizaremizova.gifsearch.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GifSearchTheme {
                AppNavHost()
            }
        }
    }
}
