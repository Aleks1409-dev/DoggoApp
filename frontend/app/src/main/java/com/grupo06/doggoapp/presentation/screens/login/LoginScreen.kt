package com.grupo06.doggoapp.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo06.doggoapp.di.AppContainer
import com.grupo06.doggoapp.presentation.components.LoadingScreen

private val ColorFondo = Color(0xFFFCFBF8)
private val ColorVerde = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(appContainer: AppContainer, onLoginSuccess: () -> Unit, onCrearCuentaClick: () -> Unit) {
    val viewModel = appContainer.loginViewModel
    val uiState by viewModel.uiState.collectAsState()
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorFondo)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido de vuelta.",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inicia sesión para continuar con doGgo.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Correo electrónico", fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("tu@correo.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = ColorVerde,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = ColorVerde
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Contraseña", fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
//            placeholder = { Text("••••••••") },
                placeholder = { Text("") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                        Icon(
                            imageVector = if (mostrarContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color.Gray
                        )
                    }
                },
                singleLine = true,
                enabled = !uiState.isLoading,
                visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = ColorVerde,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = ColorVerde
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error ?: "",
                    color = Color(0xFFDC2626),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.login(correo, contrasena) },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorVerde),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = "Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿No tienes cuenta? ", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "Crear cuenta",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorVerde,
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) { onCrearCuentaClick() }
                )
            }
        }

        if (uiState.isLoading) {
            LoadingScreen(mensaje = "Iniciando sesión...")
        }
    }
}
