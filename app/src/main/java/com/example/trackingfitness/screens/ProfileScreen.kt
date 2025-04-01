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
import androidx.compose.runtime.collectAsState
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
import com.example.trackingfitness.LockOrientationInThisScreen
import com.example.trackingfitness.R
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.activity.ExperienceBar
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun ProfileScreen(
    navController: NavController,
    darkTheme: Boolean?,
    userSession: UserSessionManager
){
    LockOrientationInThisScreen()
    userSession.getUserInformation()
    Surface (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ){
        Image(
            painter = painterResource(if (darkTheme==true) R.drawable.fondodark else R.drawable.fondoblanco2),
            contentDescription = "Logo FitnessTracking",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        BodyContentProfile(
            userSession,
            darkTheme,
            navController
        )
    }
}

@Composable
fun BodyContentProfile(
    userSessionManager: UserSessionManager,
    darkTheme: Boolean?,
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
            .padding(end = 250.dp))
        Spacer(modifier = Modifier.height(20.dp))
        val user by userSessionManager.user.collectAsState()
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
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "Loading...",
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
                ExperienceBar(darkTheme,user.userLevel, user.progressLevel, modifier = Modifier.padding(horizontal = 15.dp))
                DynamicInfoRow(
                    items = listOf(
                        "Name" to userSessionManager.getUserSession().name,
                        "Lastname" to userSessionManager.getUserSession().lastname,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Gender" to userSessionManager.getUserSession().gender,
                        "Experience" to userSessionManager.getUserSession().experienceLevel,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Height" to userSessionManager.getUserSession().height,
                        "Weight" to userSessionManager.getUserSession().weight,
                        "Age" to userSessionManager.getUserSession().age
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Goal" to userSessionManager.getUserSession().routineType
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Active Injuries" to userSessionManager.getUserSession().injuries
                    )
                )
                Row (
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Email",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = userSessionManager.getUserSession().email,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
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
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
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
                            contentColor = if (darkTheme==true) Color.White else Color.Black
                        )
                    ) {
                        Text(text = "Edit profile",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize)
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
                        Text(text = "Log out",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize)
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
                        Text(text = "Delete",
                            fontSize = MaterialTheme.typography.labelSmall.fontSize)
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
            .padding(vertical = 10.dp),
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
                    "Height" -> {
                        valueModifier = "$value m"
                    }

                    "Weight" -> {
                        valueModifier = "$value kg"
                    }

                    "Age" -> {
                        valueModifier = "$value"
                    }

                    "Gender" -> {
                        valueModifier = when (value) {
                            "1" -> "Male"
                            "2" -> "Female"
                            "3" -> "Other"
                            else -> ""
                        }
                    }
                    "Experience" -> {
                        valueModifier = when (value) {
                            "1" -> "Beginner"
                            "2" -> "Intermediate"
                            "3" -> "Advanced"
                            else -> ""
                        }
                    }
                    "Goal" -> {
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
                    "Active Injuries" -> {
                        val injuryDescription = mapOf(
                            1 to "Neck",
                            2 to "Shoulder",
                            3 to "Hip",
                            4 to "Knee",
                            5 to "Waist",
                            6 to "Leg",
                            7 to "Wrist"
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
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    modifier = Modifier
                )
                Text(
                    text = valueModifier,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(if (label == "Active injuries" || label == "Goal") 5.dp else 0.dp)
                )
            }
        }
    }
}






