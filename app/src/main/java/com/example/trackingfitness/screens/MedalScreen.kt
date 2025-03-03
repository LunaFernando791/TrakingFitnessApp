package com.example.trackingfitness.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.viewModel.MedalViewModel
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun MedalScreen(
    navController: NavController,
    userSessionManager: UserSessionManager
) {
    Surface {
        BodyMedalContainer(userSessionManager, navController)
    }
}
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BodyMedalContainer(
    userSessionManager: UserSessionManager
    ,navController: NavController,
    medalViewModel: MedalViewModel = viewModel()
){
    LaunchedEffect(Unit) {
        medalViewModel.getAllMedals()
        userSessionManager.getUserMedals()
    }
    val medalList = medalViewModel.allMedals
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                20.dp
            )
    ) {
        medalList.value.forEach(
            fun(medal){
                userSessionManager.user.value.userMedals.forEach(
                    fun(userMedal) {
                        if (medal.id == userMedal) {
                            medal.unLockMedal()
                        }
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        BackButton(
            navController = navController,
            ruta = "homeScreen",
            modifier = Modifier
                .padding(end = 240.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "YOUR MEDALS",
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            modifier = Modifier.padding(top = 15.dp, start = 15.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Fixed(3), // Definir 3 columnas
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(medalList.value) { medal ->
                MedalCard(
                    medalUrl = medal.showMedal(),
                    onClick = {
                    }
                )
            }
        }
    }
}

@Composable
fun MedalCard(modifier: Modifier = Modifier,medalUrl: String, onClick: () -> Unit){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(110.dp)
            .width(120.dp)
            .clickable { onClick() },
    ) {
        Log.d("URL", medalUrl)
        Image(
            modifier = Modifier.height(120.dp).width(120.dp),
            contentScale = ContentScale.Crop,
            painter = rememberAsyncImagePainter(medalUrl),
            contentDescription = null,
        )
    }
}
