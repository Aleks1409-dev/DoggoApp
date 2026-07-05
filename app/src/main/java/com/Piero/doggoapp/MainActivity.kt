package com.piero.doggoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.piero.doggoapp.presentation.components.AppScaffold
import com.piero.doggoapp.presentation.navigation.AppNavigation
import com.piero.doggoapp.ui.theme.DoggoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoggoAppTheme {
                val navController = rememberNavController()

                AppScaffold(navHostController = navController) { paddingValues ->
                    AppNavigation(
                        navHostController = navController,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}