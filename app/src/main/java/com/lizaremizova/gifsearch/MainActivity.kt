package com.lizaremizova.gifsearch

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.lizaremizova.gifsearch.core.ui.theme.GifSearchTheme
import com.lizaremizova.gifsearch.core.ui.theme.ThemeMode
import com.lizaremizova.gifsearch.core.ui.theme.isDark
import com.lizaremizova.gifsearch.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val savedMode = prefs.getString(KEY_THEME, null)
            .let { saved -> ThemeMode.entries.find { it.name == saved } ?: ThemeMode.System }

        setContent {
            var themeMode by remember { mutableStateOf(savedMode) }
            val dark = themeMode.isDark()

            // Status bar icons must follow the app theme, not the system one.
            DisposableEffect(dark) {
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose {}
            }

            GifSearchTheme(darkTheme = dark) {
                AppNavHost(
                    themeMode = themeMode,
                    onThemeModeChange = {
                        themeMode = it
                        prefs.edit { putString(KEY_THEME, it.name) }
                    },
                )
            }
        }
    }

    private companion object {
        const val KEY_THEME = "theme_mode"
    }
}
