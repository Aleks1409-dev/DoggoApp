package com.piero.doggoapp.presentation.screens.inicio

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piero.doggoapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen() {
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
                    Icon(Icons.Default.LocationOn, contentDescription = "Ubicación", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Miraflores, Lima", color = Color.Gray, fontSize = 14.sp)
                }
                Text(
                    text = "Cuidadores cercanos",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colorVerde.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = colorVerde, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = colorVerde,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = colorVerde
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val categorias = listOf("Todos", "Paseos", "Hospedaje", "Cuidado diurno")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categorias) { cat ->
                val isSelected = cat == "Todos"
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color.Black else Color.White,
                    border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                CuidadorCardMock(
                    nombre = "Lionel Messi",
                    ubicacion = "La Molina • a 6.4 km",
                    rating = "5.0",
                    reviews = "(841)",
                    precio = "S/ 150",
                    tipo = "Paseos",
                    badgeText = "GOAT",
                    badgeColor = Color(0xFFD4E6F1),
                    imagenResId = R.drawable.messi
                )
            }
            item {
                CuidadorCardMock(
                    nombre = "Cristiano Ronaldo",
                    ubicacion = "Miraflores • a 1.2 km",
                    rating = "4.9",
                    reviews = "(777)",
                    precio = "S/ 140",
                    tipo = "Hospedaje",
                    badgeText = "Premium",
                    badgeColor = Color(0xFFEAECEE),
                    imagenResId = R.drawable.cr7
                )
            }
            item {
                CuidadorCardMock(
                    nombre = "Lamine Yamal",
                    ubicacion = "San Isidro • a 3.0 km",
                    rating = "4.7",
                    reviews = "(120)",
                    precio = "S/ 80",
                    tipo = "Cuidado diurno",
                    badgeText = "Nuevo",
                    badgeColor = Color(0xFFFFF3CD),
                    imagenResId = R.drawable.yamal
                )
            }
            item {
                CuidadorCardMock(
                    nombre = "Erling Haaland",
                    ubicacion = "Surco • a 5.5 km",
                    rating = "4.8",
                    reviews = "(350)",
                    precio = "S/ 100",
                    tipo = "Paseos intensos",
                    badgeText = "Máquina",
                    badgeColor = Color(0xFFD1F2EB),
                    imagenResId = R.drawable.haaland
                )
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
                    contentDescription = "Foto de $nombre",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF10B981)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, "", tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(ubicacion, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "", tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
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