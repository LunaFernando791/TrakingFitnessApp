package com.example.trackingfitness.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.trackingfitness.R
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.navigation.AppScreens


@Composable//VISTA DE INICIO PARA INICIAR SESIÓN O REGISTRARTE
fun StartScreen(navController: NavHostController) {
    BodyContent(navController = navController)
}

@Composable
fun BodyContent(navController: NavHostController){
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(if (darkTheme) R.drawable.fondodark else R.drawable.fondoblanco2),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ftlogo),
                    contentDescription = "Logo FitnessTracking",
                    modifier = Modifier
                        .size(width = 400.dp, height = 400.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                    .fillMaxSize()
                    .background(if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                    .border(
                        2.dp,
                        if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                        RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "TRACKING FITNESS",
                        fontSize = 25.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary)
                            .clickable { navController.navigate(AppScreens.RegisterOneScreen.route) },

                        ) {
                        Text(
                            text = "REGISTRARSE",
                            fontSize = 20.sp,
                            color = if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "O",
                        fontSize = 25.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary)
                            .clickable { navController.navigate("loginScreen") }
                    ) {
                        Text(
                            text = "INICIAR SESIÓN",
                            fontSize = 20.sp,
                            color = if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(20.dp)
                        )
                    }
                }
            }
        }
    }
}