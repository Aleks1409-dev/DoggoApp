package com.grupo06.doggoapp.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.grupo06.doggoapp.presentation.event.EventBus
import com.grupo06.doggoapp.presentation.event.UiEvent

@Composable
fun AppScaffold(navHostController: NavHostController,
                content: @Composable (PaddingValues) -> Unit){

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        EventBus.events.collect { event ->
            when(event){
                is UiEvent.Success -> { snackbarHostState.showSnackbar(SnackbarCustom(
                    message = event.mensaje, status = SnackbarStatus.SUCCESS
                )) }
                is UiEvent.Error -> { snackbarHostState.showSnackbar(SnackbarCustom(
                    message = event.mensaje, status = SnackbarStatus.ERROR
                )) }
                is UiEvent.Warning -> { snackbarHostState.showSnackbar(SnackbarCustom(
                    message = event.mensaje, status = SnackbarStatus.WARNING
                )) }
            }
        }
    }

    Scaffold(
        bottomBar = { AppBottomBar(navHostController) },
        snackbarHost = { SnackbarHost(snackbarHostState){ data ->
            val custom = data.visuals as? SnackbarCustom
            if(custom != null){
                val status = custom.status
                Snackbar(
                    containerColor = status.colorFondo,
                    contentColor = status.colorTexto,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = status.icono,"", tint = status.colorTexto)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(custom.message)
                    }
                }
            }else{
                Snackbar(snackbarData = data)
            }
        } }
    ) {paddingValues ->
        content(paddingValues)
    }
}