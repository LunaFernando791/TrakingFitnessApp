package com.example.trackingfitness.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.R
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun ProfileScreen(
    navController: NavController,
    userSession: UserSessionManager
){
    Surface (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .padding(end = 275.dp),
            onClick = {
                navController.navigate("homeScreen")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Volver")
        }
        Spacer(modifier = Modifier.height(20.dp))
        LaunchedEffect(Unit) {
            userSessionManager.fetchImageProfile()
        }
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
            // Observa la imagen del LiveData
            val profileImage by userSessionManager.profileImage.observeAsState()
            if (profileImage != null) {
                Image(
                    bitmap = profileImage!!.asImageBitmap(),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "Cargando imagen...",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
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
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    3.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(20.dp)
                )
                .background(MaterialTheme.colorScheme.background)

        ){
            item {
                Spacer(modifier = Modifier.height(30.dp))
                DynamicInfoRow(
                    items = listOf(
                        "Nombre" to userSessionManager.getUserSession().name,
                        "Apellido" to userSessionManager.getUserSession().lastname,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Genero" to userSessionManager.getUserSession().gender,
                        "Experiencia" to userSessionManager.getUserSession().experienceLevel,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Altura" to userSessionManager.getUserSession().height,
                        "Peso" to userSessionManager.getUserSession().weight,
                        "Edad" to userSessionManager.getUserSession().age
                    )
                )
                Row (
                    modifier = Modifier
                        .padding(horizontal = 50.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Correo",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = userSessionManager.getUserSession().email,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .width(50.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable {
                                    navController.navigate("editEmailScreen")
                                }
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Password",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .width(50.dp)
                                .height(30.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable {
                                    navController.navigate("editPasswordScreen")
                                }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .background(MaterialTheme.colorScheme.background),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            //userSessionManager.logoutUser()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = if (darkTheme.value) Color.White else Color.Black
                        )
                    ) {
                        Text(text = "Editar Perfil")
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            userSessionManager.logoutUser()
                            navController.navigate("startScreen")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = if (darkTheme.value) Color.White else Color.Black
                        )
                    ) {
                        Text(text = "Cerrar sesión")
                    }
                    Button(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            userSessionManager.deleteUserAccount()
                            navController.navigate("startScreen")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = if (darkTheme.value) Color.White else Color.Black
                        )
                    ) {
                        Text(text = "Eliminar cuenta")
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicInfoRow(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val valueModifier: String
                when (label) {
                    "Altura" -> {
                        valueModifier = "$value m"
                    }

                    "Peso" -> {
                        valueModifier = "$value kg"
                    }

                    "Edad" -> {
                        valueModifier = "$value años"
                    }

                    "Genero" -> {
                        valueModifier = when (value) {
                            "1" -> "Masculino"
                            "2" -> "Femenino"
                            "3" -> "Otro"
                            else -> ""
                        }
                    }
                    "Experiencia" -> {
                        valueModifier = when (value) {
                            "1" -> "Principiante"
                            "2" -> "Intermedio"
                            "3" -> "Avanzado"
                            else -> ""
                        }
                    }
                    else ->{
                        valueModifier = value
                    }
                }
                Text(text = label, fontSize = 20.sp)
                Text(
                    text = valueModifier,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}






