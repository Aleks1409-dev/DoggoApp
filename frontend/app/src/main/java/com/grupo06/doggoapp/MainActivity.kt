package com.grupo06.doggoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.grupo06.doggoapp.di.AppContainer
import com.grupo06.doggoapp.presentation.components.AppScaffold
import com.grupo06.doggoapp.presentation.navigation.AppNavigation
import com.grupo06.doggoapp.ui.theme.DoggoAppTheme

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer by lazy {
        (application as DoggoApplication).container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DoggoAppTheme {
                val navController = rememberNavController()

                AppScaffold(navHostController = navController) { paddingValues ->
                    AppNavigation(
                        navHostController = navController,
                        paddingValues = paddingValues,
                        appContainer = appContainer
                    )
                }
            }
        }
    }
}