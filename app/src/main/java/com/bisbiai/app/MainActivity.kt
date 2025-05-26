package com.bisbiai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bisbiai.app.ui.navigation.NavGraph
import com.bisbiai.app.ui.theme.BISBIAITheme
import dagger.hilt.android.AndroidEntryPoint
import top.yukonga.miuix.kmp.basic.Surface

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashCondition
            }
        }
        enableEdgeToEdge()
        setContent {
            BISBIAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val startDestination by viewModel.startDestination.collectAsState()
                    NavGraph(
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}