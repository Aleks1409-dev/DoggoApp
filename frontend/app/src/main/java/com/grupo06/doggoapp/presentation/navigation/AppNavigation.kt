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
import com.grupo06.doggoapp.presentation.screens.bienvenida.BienvenidaScreen
import com.grupo06.doggoapp.presentation.screens.cuidadorDetalle.CuidadorDetalleScreen
import com.grupo06.doggoapp.presentation.screens.cuidadorDetalle.CuidadorDetalleViewModel
import com.grupo06.doggoapp.presentation.screens.cuidadorDetalle.CuidadorDetalleViewModelFactory
import com.grupo06.doggoapp.presentation.screens.inicio.InicioScreen
import com.grupo06.doggoapp.presentation.screens.agenda.AgendaScreen
import com.grupo06.doggoapp.presentation.screens.inicio.InicioViewModel
import com.grupo06.doggoapp.presentation.screens.inicio.InicioViewModelFactory
import com.grupo06.doggoapp.presentation.screens.mensajes.MensajesScreen
import com.grupo06.doggoapp.presentation.screens.perfil.PerfilScreen
import com.grupo06.doggoapp.presentation.screens.programarCita.ProgramarCitaScreen
import com.grupo06.doggoapp.presentation.screens.programarCita.ProgramarCitaViewModel
import com.grupo06.doggoapp.presentation.screens.programarCita.ProgramarCitaViewModelFactory

@Composable
fun AppNavigation(
    navHostController: NavHostController,
    paddingValues: PaddingValues,
    appContainer: com.grupo06.doggoapp.di.AppContainer
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
            InicioScreen(
                viewModel = viewModel,
                onCuidadorClick = { sitterId ->
                    navHostController.navigate(NavRutas.cuidadorDetalle(sitterId))
                }
            )
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
        composable(
            route = NavRutas.CUIDADOR_DETALLE,
            arguments = listOf(navArgument("sitterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sitterId = backStackEntry.arguments?.getString("sitterId")
            if (sitterId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                onReservar = { sitterId ->
                    navHostController.navigate(NavRutas.programarCita(sitterId))
                }
            )
        }
        composable(
            route = NavRutas.PROGRAMAR_CITA,
            arguments = listOf(navArgument("sitterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sitterId = backStackEntry.arguments?.getString("sitterId")
            if (sitterId == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
    }
}