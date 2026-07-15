package com.grupo06.doggoapp.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grupo06.doggoapp.di.AppContainer
import com.grupo06.doggoapp.presentation.screens.bienvenida.BienvenidaScreen
import com.grupo06.doggoapp.presentation.screens.cuidadorDetalle.CuidadorDetalleScreen
import com.grupo06.doggoapp.presentation.screens.cuidadorDetalle.CuidadorDetalleViewModel
import com.grupo06.doggoapp.presentation.screens.cuidadorDetalle.CuidadorDetalleViewModelFactory
import com.grupo06.doggoapp.presentation.screens.chat.ChatScreen
import com.grupo06.doggoapp.presentation.screens.inicio.InicioScreen
import com.grupo06.doggoapp.presentation.screens.login.LoginScreen
import com.grupo06.doggoapp.presentation.screens.mensajes.MensajesScreen
import com.grupo06.doggoapp.presentation.screens.perfil.PerfilScreen
import com.grupo06.doggoapp.presentation.screens.programarCita.ProgramarCitaScreen
import com.grupo06.doggoapp.presentation.screens.programarCita.ProgramarCitaViewModel
import com.grupo06.doggoapp.presentation.screens.programarCita.ProgramarCitaViewModelFactory
import com.grupo06.doggoapp.presentation.screens.registro.RegistroScreen
import com.grupo06.doggoapp.presentation.screens.reserva.ReservarScreen

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    appContainer: AppContainer
) {
    NavHost(
        navController = navHostController,
        startDestination = NavRutas.LOGIN,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(NavRutas.LOGIN) {
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
        composable(NavRutas.REGISTRO) {
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
        composable(NavRutas.BIENVENIDA) {
            BienvenidaScreen()
        }
        composable(NavRutas.INICIO) {
            InicioScreen(
                navHostController = navHostController,
                appContainer = appContainer
            )
        }
        composable(NavRutas.AGENDA) {
            ReservarScreen(
                onBackClick = { navHostController.popBackStack() },
                onConfirmarClick = {
                    navHostController.navigate(NavRutas.INICIO)
                }
            )
        }
        composable(NavRutas.MENSAJES) {
            MensajesScreen(navHostController = navHostController)
        }
        composable(NavRutas.PERFIL) {
            PerfilScreen(
                appContainer = appContainer,
                onLogoutSuccess = {
                    navHostController.navigate(NavRutas.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(
            route = NavRutas.CUIDADOR_DETALLE,
            arguments = listOf(navArgument("sitterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sitterId = backStackEntry.arguments?.getString("sitterId")
            if (sitterId == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: cuidador no encontrado")
                }
                return@composable
            }
            val viewModel: CuidadorDetalleViewModel = viewModel(
                factory = CuidadorDetalleViewModelFactory(sitterId, appContainer.cuidadorRepository)
            )
            CuidadorDetalleScreen(
                viewModel = viewModel,
                onVolver = { navHostController.popBackStack() },
                onReservar = { id ->
                    navHostController.navigate(NavRutas.programarCita(id))
                },
                onMensaje = { id ->
                    navHostController.navigate(NavRutas.chat(id))
                }
            )
        }
        composable(
            route = NavRutas.PROGRAMAR_CITA,
            arguments = listOf(navArgument("sitterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sitterId = backStackEntry.arguments?.getString("sitterId")
            if (sitterId == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: cuidador no encontrado")
                }
                return@composable
            }
            val viewModel: ProgramarCitaViewModel = viewModel(
                factory = ProgramarCitaViewModelFactory(
                    sitterId,
                    appContainer.agendaRepository,
                    appContainer.cuidadorRepository
                )
            )
            ProgramarCitaScreen(
                viewModel = viewModel,
                onVolver = { navHostController.popBackStack() }
            )
        }
        composable(
            route = NavRutas.CHAT,
            arguments = listOf(navArgument("sitterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sitterId = backStackEntry.arguments?.getString("sitterId") ?: ""
            ChatScreen(
                sitterId = sitterId,
                onVolver = { navHostController.popBackStack() }
            )
        }
    }
}
