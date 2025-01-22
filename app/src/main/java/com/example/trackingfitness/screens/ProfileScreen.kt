package com.example.trackingfitness.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.R
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.activity.ExperienceBar
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun ProfileScreen(
    navController: NavController,
    userSession: UserSessionManager
){
    userSession.getUserInformation()
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
        BackButton(
            navController = navController,
            ruta = "homeScreen",
            modifier = Modifier
            .padding(end = 275.dp))
        Spacer(modifier = Modifier.height(20.dp))
        LaunchedEffect(true) {
            userSessionManager.fetchImageProfile()
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                    painter = painterResource(R.drawable.edit_button),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .shadow(
                            10.dp,
                            shape = RoundedCornerShape(200.dp),
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        )
                        .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(200.dp))
                        .size(40.dp)
                        .clickable {
                            navController.navigate("editProfilePicture")
                        },
                    contentScale = ContentScale.Crop
                )
            Box(
                modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 60.dp
                    )
                    .shadow(
                        10.dp,
                        shape = RoundedCornerShape(200.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(200.dp))
                    .width(200.dp)
                    .height(200.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(200.dp)
                    )
            ) {
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
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            modifier = Modifier
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(20.dp))
                .width(200.dp)
                .height(50.dp)
                .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
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
                .padding(horizontal = 5.dp)
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(20.dp))
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
        ){
            item {
                Spacer(modifier = Modifier.height(30.dp))
                ExperienceBar(userSessionManager.getUserSession().userLevel, userSessionManager.getUserSession().progressLevel, modifier = Modifier.padding(horizontal = 15.dp))
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
                DynamicInfoRow(
                    items = listOf(
                        "Objetivo" to userSessionManager.getUserSession().routineType
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Lesiones activas" to userSessionManager.getUserSession().injuries
                    )
                )
                Row (
                    modifier = Modifier
                        .padding(horizontal = 50.dp)
                        .fillMaxWidth(),
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
                                .background(MaterialTheme.colorScheme.background)
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
                                .background(MaterialTheme.colorScheme.background)
                                .clickable {
                                    navController.navigate("editPasswordScreen")
                                }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(10.dp),
                        onClick = {
                            navController.navigate("editProfileScreen")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
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
                            contentColor = Color.White
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
                            contentColor = Color.White
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
    items: List<Pair<String, Any>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
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
                    "Objetivo" -> {
                        valueModifier = when (value) {
                            "1" -> "Improve cardiovascular health"
                            "2" -> "Strengthen muscles"
                            "3" -> "Improve flexibility"
                            "4" -> "Reduce stress"
                            "5" -> "Weight control"
                            "6" -> "Increase energy"
                            "7" -> "Prevent diseases"
                            "8" -> "Improve posture"
                            else -> ""
                        }
                    }
                    "Lesiones activas" -> {
                        val injuryDescription = mapOf(
                            1 to "Cuello",
                            2 to "Hombro",
                            3 to "Cadera",
                            4 to "Rodilla",
                            5 to "Cintura",
                            6 to "Pierna",
                            7 to "Muñeca"
                        )
                        // Asegurarse de que value es una lista de enteros
                        valueModifier = if (value is List<*>) {
                            (value as List<*>).joinToString(", ") {
                                injuryDescription[it] ?: "Unknown injury"
                            }
                        } else {
                            "No injuries"
                        }
                    }
                    else ->{
                        valueModifier = value.toString()
                    }
                }
                Text(
                    text = label,
                    fontSize = 20.sp,
                    modifier = Modifier
                )
                Text(
                    text = valueModifier,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(if (label == "Lesiones activas" || label == "Objetivo") 5.dp else 0.dp)
                )
            }
        }
    }
}






