package com.piero.doggoapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.piero.doggoapp.presentation.screens.bienvenida.BienvenidaScreen
import com.piero.doggoapp.presentation.screens.inicio.InicioScreen
import com.piero.doggoapp.presentation.screens.agenda.AgendaScreen
import com.piero.doggoapp.presentation.screens.inicio.InicioViewModel
import com.piero.doggoapp.presentation.screens.inicio.InicioViewModelFactory
import com.piero.doggoapp.presentation.screens.mensajes.MensajesScreen
import com.piero.doggoapp.presentation.screens.perfil.PerfilScreen

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    appContainer: com.piero.doggoapp.di.AppContainer
){
    NavHost(
        navController = navHostController,
        startDestination = NavRutas.INICIO,
        modifier = Modifier.padding(paddingValues)
    ){
        composable(NavRutas.BIENVENIDA){
            BienvenidaScreen()
        }
        composable(NavRutas.INICIO) {
            val viewModel: InicioViewModel = viewModel(
                factory = InicioViewModelFactory(appContainer.cuidadorRepository)
            )
            InicioScreen(viewModel = viewModel)
        }
        composable(NavRutas.AGENDA){
            AgendaScreen()
        }
        composable(NavRutas.MENSAJES){
            MensajesScreen()
        }
        composable(NavRutas.PERFIL){
            PerfilScreen()
        }
    }
}