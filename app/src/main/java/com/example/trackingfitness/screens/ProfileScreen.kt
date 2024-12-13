package com.example.trackingfitness.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.R
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.UserSessionManager

// PANTALLA SIN TERMINAR
@Composable
fun ProfileScreen(
    navController: NavController,
    userSession: UserSessionManager
){
    Surface (
        modifier = Modifier.fillMaxSize(),
    ){
        Image(
            painter = painterResource(if (darkTheme.value) R.drawable.fondodark else R.drawable.fondoblanco2),
            contentDescription = "Logo FitnessTracking",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        BodyContentProfile(
            userSession,
            navController
        )
    }
}

@Composable
fun BodyContentProfile(
    userSessionManager: UserSessionManager,
    navController: NavController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(50.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(200.dp))
                .border(
                    2.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(200.dp)
                )
                .width(200.dp)
                .height(200.dp)
                .background(MaterialTheme.colorScheme.background)

        ){
            Text(text = "Imagen de perfil.")
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    2.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(20.dp)
                )
                .background(MaterialTheme.colorScheme.background)
                .wrapContentSize(Alignment.Center),
            text = userSessionManager.getUserSession().username,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    3.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(20.dp)
                )
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)

        ){
            Row (
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(text = "Nombre: ")
                Text(text = "Apellido: ")
            }
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Edad: ")
                Text(text = "Altura: ")
                Text(text = "Peso: ")
            }
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Genero: ")
                Text(text = "Experiencia: ")
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(text = "Correo electronico: ")
                Text(text = "Contraseña: ")
            }
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = {
                        //userSessionManager.logoutUser()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )) {
                    Text(text = "Editar Perfil")
                }
                Button(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = {
                        navController.navigate("homeScreen")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )) {
                    Text(text = "Volver")
                }
            }
            Button(
                modifier = Modifier
                    .padding(10.dp),
                onClick = {
                    userSessionManager.logoutUser()
                    navController.navigate("startScreen")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = MaterialTheme.colorScheme.primary
                )) {
                Text(text = "Cerrar sesión")
            }
        }
    }
}