package com.grupo06.doggoapp.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.grupo06.doggoapp.presentation.navigation.BotonNavItem
import com.grupo06.doggoapp.presentation.navigation.NavRutas

@Composable
fun AppBottomBar(navHostController: NavHostController){
    val opciones = listOf(BotonNavItem.Inicio, BotonNavItem.Agenda, BotonNavItem.Mensajes, BotonNavItem.Perfil)
    val rutaSel = navHostController.currentBackStackEntryAsState().value?.destination?.route

    if (rutaSel == NavRutas.BIENVENIDA) return

    NavigationBar(
        containerColor = Color.White
    ){
        opciones.forEach { item ->
            NavigationBarItem(
                selected = rutaSel == item.ruta,
                onClick = { navHostController.navigate(item.ruta) },
                icon = { Icon(item.icono,"") },
                label = { Text(item.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color(0xFF10B981),
                    selectedTextColor = Color(0xFF10B981),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}