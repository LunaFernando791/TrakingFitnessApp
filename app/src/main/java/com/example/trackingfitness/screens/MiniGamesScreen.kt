package com.example.trackingfitness.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.viewModel.MiniGamesViewModel
import com.example.trackingfitness.viewModel.UserSessionManager


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MiniGamesScreen(
    userSessionManager: UserSessionManager,
    navController: NavController,
    miniGamesViewModel: MiniGamesViewModel
){

    LaunchedEffect(Unit) {
        miniGamesViewModel.getGlobalRanking(userSessionManager.getUserSession().token)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
        BackButton(
            navController = navController,
            ruta = "selectExerciseModeScreen",
            modifier = Modifier.padding(end = 250.dp)
        )
        Text(
            text = "Mini Games",
            style = TextStyle(
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(10f, 10f),
                    blurRadius = 8f
                ),
            )

        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Lasts Scores")
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Position")
                    Text(text = "Name")
                    Text(text = "Telephone")
                    Text(text = "Score")
                }
                if (miniGamesViewModel.globalRanking.value.isNotEmpty()) {
                    for (i in miniGamesViewModel.globalRanking.value.take(5)) {
                        PositionCard(
                            position = miniGamesViewModel.globalRanking.value.indexOf(i) + 1,
                            name = i.alias,
                            telephone = i.telephone,
                            score = i.global_score
                        )
                    }
                }else{
                    Text(text = "No hay datos")
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Mini Games")
                MiniGameCard(1,"image", "Push-Ups","Make many push-ups how you can.", navController)
                MiniGameCard(2, "image", "Wall Sit ","Make the most time on wall site.", navController)
                MiniGameCard(3, "image", "Plank","Make the most time in plank position.", navController)
            }
        }
    }
}
@Composable
fun PositionCard(
    position: Int,
    name: String,
    telephone: String,
    score: Int
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .height(40.dp)
            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Text(text = position.toString(),
            modifier = Modifier
                .padding(start = 10.dp)
                .width(20.dp)
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .background(if (position == 1) Color.Yellow else if (position == 2) Color.Gray else if (position == 3) Color.Red else Color.Transparent),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(text = name)
        Text(text = telephone)
        Text(text = score.toString(), modifier = Modifier.padding(end = 10.dp))
    }
}

@Composable
fun MiniGameCard(
    id: Int,
    image: String,
    title: String,
    description: String,
    navController: NavController
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { // DEPENDIENDO DEL ID SE ESCOGE 1 DE LOS EJERCICIOS. PUEDES MODIFICARLO CÓMO QUIERAS ARMANDO
                if (id == 1) {
                    navController.navigate("pushUpsScreen")
                } else if (id == 2) {
                    navController.navigate("wallSitScreen")
                } else if (id == 3) {
                    navController.navigate("plankScreen")
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title)
    }
    Text(text = description)
}