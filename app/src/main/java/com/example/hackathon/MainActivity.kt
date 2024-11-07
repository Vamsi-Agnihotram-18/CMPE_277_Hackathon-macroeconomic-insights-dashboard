package com.example.hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hackathon.ui.theme.HackathonTheme
import com.github.mikephil.charting.utils.Utils

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val dataViewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.init(this)

        setContent {
            HackathonTheme {

                window.statusBarColor = Color.White.toArgb()
                window.navigationBarColor = Color.Black.toArgb()
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true

                MainScreen(mainViewModel = mainViewModel,dataViewModel = dataViewModel)
            }
        }
    }
}


@Composable
fun MainScreen(mainViewModel: MainViewModel, dataViewModel: DataViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "role_selection") {
        composable("role_selection") {
            AppLaunchView(navController, mainViewModel)
        }
        composable("data_analysis") {
            DataAnalysisView(navController, mainViewModel, dataViewModel)
        }
        composable("chat") {
            ChatScreen()
        }
    }
}