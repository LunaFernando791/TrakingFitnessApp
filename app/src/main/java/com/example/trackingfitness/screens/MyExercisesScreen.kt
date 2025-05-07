package com.example.trackingfitness.screens

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.LockOrientationInThisScreen
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.conection.MyExercise
import com.example.trackingfitness.conection.RetrofitInstance.BASE_URL
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.ui.theme.PositionColor
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun MyExercisesScreen(
    userSessionManager: UserSessionManager,
    darkTheme: Boolean?,
    navController: NavController,
    context: Context
) {
    LockOrientationInThisScreen()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Contenedor para el botón de regreso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    navController = navController,
                    ruta = "selectExerciseModeScreen"
                )
            }
            // Cuerpo del contenido con la lista de ejercicios
            ExercisesBodyContent(userSessionManager,
                darkTheme,navController,
                context)
        }
    }
}


@Composable
fun ExercisesBodyContent(
    userSessionManager: UserSessionManager,
    darkTheme: Boolean?,
    navController: NavController,
    context: Context
) {
    val myExercisesState by userSessionManager.myExercises.collectAsState()
    val routineCompleted by userSessionManager.routineCompleted.collectAsState()

    LaunchedEffect(Unit) {
        userSessionManager.getMyExercises()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally

    )

    {
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Text("My Exercises", style = MaterialTheme.typography.headlineMedium)
        }
        if(!routineCompleted) {
            when {
                // Estado de carga inicial
                myExercisesState == null -> {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Lista vacía
                myExercisesState?.selectedExercises.isNullOrEmpty() -> {
                    item {
                        Text("No exercises added yet")
                    }
                }
                // Datos cargados
                else -> {
                    items(myExercisesState!!.selectedExercises) { exercise ->
                        if (exercise.status != "completado")
                            ExerciseItem(myExercise = exercise,
                                darkTheme = darkTheme,
                                navController = navController,
                                context = context)
                    }
                }
            }
        }else{
            item {
                Text("Congratulations! You have completed your routine")
            }
        }
    }
}

@Composable
fun ExerciseItem(
    myExercise: MyExercise,
    darkTheme: Boolean?,
    navController: NavController,
    context: Context
){

    val url = "${BASE_URL}storage/${myExercise.image_path}"
    Column(
        modifier = Modifier

            .fillMaxWidth()

            .height(250.dp)
            .padding(15.dp)
            .shadow(
                10.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .width(150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop // Añade esto
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    Text(
                        text = if (myExercise.exercise_name.length > 15) myExercise.exercise_name.substring(
                            0,
                            15
                        ) + "..." else myExercise.exercise_name,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                        color = MaterialTheme.colorScheme.inverseSurface,
                    )
                    Text(
                        text = if (myExercise.description.length > 80) myExercise.description.substring(
                            0,
                            80
                        ) + "..." else
                            "Sets: ${myExercise.description}",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                        color = MaterialTheme.colorScheme.inverseSurface,
                    )
                }
            }
        }
        Button(
            onClick = {
//                navController.navigate("cameraScreen/${myExercise.exercise_id}")
                if (myExercise.status == "actual")
//                    navController.navigate(AppScreens.CameraScreenV2.route.replace("{id}", myExercise.exercise_id.toString()))
                    navController.navigate(AppScreens.CameraScreenV2.route.replace("{id}", "-1"))
                else
                    Toast.makeText(
                        context,
                        "This exercise haven't been unlocked yet.",
                        Toast.LENGTH_SHORT
                    ).show()
            },
            modifier = Modifier
                .width(120.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (myExercise.status == "actual") PositionColor else MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                text = if (myExercise.status == "actual") "Start" else "Block",
                color = if (darkTheme==true) Color.White else Color.Black,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
            )
        }
    }
}