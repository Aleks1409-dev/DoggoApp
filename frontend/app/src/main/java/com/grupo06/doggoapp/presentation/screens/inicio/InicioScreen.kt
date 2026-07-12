package com.grupo06.doggoapp.presentation.screens.inicio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.grupo06.doggoapp.R
import com.grupo06.doggoapp.di.AppContainer
import com.grupo06.doggoapp.presentation.components.EmptyScreen
import com.grupo06.doggoapp.presentation.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    navHostController: NavHostController,
    appContainer: AppContainer
) {
    val viewModel = appContainer.inicioViewModel
    val uiState by viewModel.uiState.collectAsState()
    val cuidadores = uiState.cuidadores
    val colorFondo = Color(0xFFFCFBF8)
    val colorVerde = Color(0xFF10B981)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Miraflores, Lima", color = Color.Gray, fontSize = 14.sp)
                }
                Text("Cuidadores cercanos", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(colorVerde.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = colorVerde, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "", onValueChange = {}, placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = colorVerde, unfocusedIndicatorColor = Color.LightGray),
                modifier = Modifier.weight(1f).height(52.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = {}, modifier = Modifier.size(52.dp).background(Color.White, RoundedCornerShape(24.dp))) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when {
            uiState.isLoading -> LoadingScreen(mensaje = "Buscando cuidadores...")
            cuidadores.isEmpty() -> EmptyScreen(mensaje = "No hay cuidadores disponibles")
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(cuidadores) { cuidador ->
                        Surface(
                            onClick = {
                                // Usamos un id quemado "123" porque el modelo actual no tiene id
                                navHostController.navigate("detalle_screen/123")
                            },
                            color = Color.Transparent
                        ) {
                            CuidadorCardReal(cuidador = cuidador)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CuidadorCardMock(
    nombre: String, ubicacion: String, rating: String, reviews: String,
    precio: String, tipo: String, badgeText: String, badgeColor: Color,
    imagenResId: Int
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = imagenResId),
                    contentDescription = "Foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Icono de favorito
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(16.dp), tint = Color(0xFF10B981))
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(ubicacion, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Text(" $rating ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(reviews, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Surface(shape = RoundedCornerShape(8.dp), color = badgeColor) {
                    Text(badgeText, color = Color.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(tipo, color = Color.Gray, fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(precio, color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(" / noche", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CuidadorCardReal(cuidador: com.grupo06.doggoapp.domain.model.Cuidador) {
    CuidadorCardMock(
        nombre = cuidador.nombre,
        ubicacion = cuidador.ubicacion,
        rating = cuidador.rating.toString(),
        reviews = "(Verificado)",
        precio = "S/ ${cuidador.precio}",
        tipo = cuidador.tipo,
        badgeText = "En línea",
        badgeColor = Color(0xFFD4E6F1),
        imagenResId = R.drawable.messi
    )
}