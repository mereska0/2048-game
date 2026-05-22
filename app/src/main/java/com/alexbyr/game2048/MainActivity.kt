package com.alexbyr.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alexbyr.game2048.game.presentation.GameRoute
import com.alexbyr.game2048.ui.design.Game2048Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Game2048Theme {
                GameRoute()
            }
        }
    }
}
