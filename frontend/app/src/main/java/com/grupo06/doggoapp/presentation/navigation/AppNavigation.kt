package com.grupo06.doggoapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grupo06.doggoapp.presentation.screens.bienvenida.BienvenidaScreen
import com.grupo06.doggoapp.presentation.screens.inicio.InicioScreen
import com.grupo06.doggoapp.presentation.screens.agenda.AgendaScreen
import com.grupo06.doggoapp.presentation.screens.login.LoginScreen
import com.grupo06.doggoapp.presentation.screens.mensajes.MensajesScreen
import com.grupo06.doggoapp.presentation.screens.perfil.PerfilScreen
import com.grupo06.doggoapp.presentation.screens.registro.RegistroScreen

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    appContainer: com.grupo06.doggoapp.di.AppContainer
){
    NavHost(
        navController = navHostController,
        startDestination = NavRutas.LOGIN,
        modifier = Modifier.padding(paddingValues)
    ){
        composable(NavRutas.LOGIN){
            LoginScreen(
                appContainer = appContainer,
                onLoginSuccess = {
                    navHostController.navigate(NavRutas.INICIO) {
                        popUpTo(NavRutas.LOGIN) { inclusive = true }
                    }
                },
                onCrearCuentaClick = {
                    navHostController.navigate(NavRutas.REGISTRO)
                }
            )
        }
        composable(NavRutas.REGISTRO){
            RegistroScreen(
                appContainer = appContainer,
                onRegistroSuccess = {
                    navHostController.popBackStack()
                },
                onBackToLoginClick = {
                    navHostController.popBackStack()
                }
            )
        }
        composable(NavRutas.BIENVENIDA){
            BienvenidaScreen()
        }
        composable(NavRutas.INICIO) {
            InicioScreen(appContainer = appContainer)
        }
        composable(NavRutas.AGENDA){
            AgendaScreen()
        }
        composable(NavRutas.MENSAJES){
            MensajesScreen()
        }
        composable(NavRutas.PERFIL){
            PerfilScreen(
                appContainer = appContainer,
                onLogoutSuccess = {
                    navHostController.navigate(NavRutas.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
