package com.example.trackingfitness.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.conection.RankingResponse
import com.example.trackingfitness.conection.RetrofitInstance.BASE_URL
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun RankingScreen(
    navController: NavController,
    userSessionManager: UserSessionManager,
){
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ){
        RankingBodyContent(
            navController,
            userSessionManager
        )
    }
}

@Composable
fun RankingBodyContent(
    navController: NavController,
    userSessionManager: UserSessionManager,
){
    LaunchedEffect(Unit) {
        userSessionManager.showRanking()
    }
    val ranking = userSessionManager.ranking.collectAsState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .padding(10.dp)
        )
        BackButton(
            navController = navController,
            ruta = "homeScreen",
            modifier = Modifier
                .padding(end = 275.dp)
        )
        Text(
            text = "GLOBAL RANKING",
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            modifier = Modifier.padding(top = 15.dp, start = 15.dp)
        )
        Spacer(
            modifier = Modifier
                .padding(10.dp)
        )
        if (ranking.value != null) {
            RankingList(ranking.value!!)
        }else{
            Text("Loading...")
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RankingList(ranking: RankingResponse) {
    // Definir cuántos usuarios mostrar por página
    val usuariosPorPagina = 7
    // Estado para la página actual
    var paginaActual by remember { mutableIntStateOf(0) }
    // Calcular el índice de inicio y fin de los usuarios a mostrar
    val inicio = paginaActual * usuariosPorPagina
    val fin = minOf(inicio + usuariosPorPagina, ranking.topUsers.size)
    // Obtener la sublista de usuarios para la página actual
    val usuariosMostrados = ranking.topUsers.subList(inicio, fin)

    Column(
        modifier = Modifier
            .shadow(
                10.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .fillMaxWidth()
            .height(700.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary),
    ) {
        // Iterar sobre los usuarios de la página actual
        usuariosMostrados.forEachIndexed { index, user ->
            // Calcular la posición real en el ranking
            val posicion = inicio + index + 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (posicion == ranking.userPosition) MaterialTheme.colorScheme.onSecondaryContainer
                        else if (posicion % 2 == 0) MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.secondary
                    )
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (posicion == ranking.userPosition) {
                    Text(text = "You: ")
                }
                Text(text = posicion.toString())
                val url = BASE_URL + user.icon_url
                Log.d("URL", url)
                Image(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = null
                )
                Text(
                    text = if (user.username.length > 10) user.username.substring(0, 8) + "..."
                    else user.username
                )
                Column {
                    Text(text = "Points: ")
                    Text(text = user.score.toString())
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        // Botón de "Siguiente"
        if (fin < ranking.topUsers.size) {
            Button(
                onClick = { paginaActual += 1 },
                modifier = Modifier
                    .align(Alignment.End) // Alinear el botón a la derecha
                    .padding(horizontal = 8.dp)
                    .height(30.dp),
                colors =
                    androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
            ) {
                Text(
                    text = "Next",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(4f, 4f),
                            blurRadius = 8f
                        )
                    )
                )
            }
        }
        if (inicio > 0) {
            Button(
                onClick = { paginaActual -= 1 },
                modifier = Modifier
                    .align(Alignment.End) // Alinear el botón a la derecha
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .height(30.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ){
                Text(
                    text = "Previous",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(4f, 4f),
                            blurRadius = 8f
                        )
                    ))
            }
        }
    }
}