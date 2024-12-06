package com.example.trackingfitness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackingfitness.MyCalendar
import com.example.trackingfitness.NavBarMenu
import com.example.trackingfitness.TopMenu

@Composable
fun PrincipalScreen(
    navController: NavController,
){
    BodyContent(navController = navController)
}

@Composable
fun BodyContent(navController: NavController) { //
    Column(
        modifier = Modifier.padding(top = 80.dp)
    ) {
        Text(text = "Bienvenido")
        Row(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            TopMenu(navController)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyCalendar()
        }
        Row(
            modifier = Modifier
                .padding(top = 40.dp)
                .background(Color.LightGray)
                .fillMaxSize()
        ) {
            NavBarMenu(navController)
        }
    }
}