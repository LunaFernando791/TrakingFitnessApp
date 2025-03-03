package com.example.trackingfitness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.trackingfitness.viewModel.UserSessionManager


// CLASE PARA LA PANTALLA DE LA CÁMARA
@Composable
fun CameraScreen(
    idExercise: Int,
    navController: NavController,
    userSessionManager: UserSessionManager
){
    Surface(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        CameraScreenBodyContent(
            idExercise = idExercise,
            navController = navController,
            userSessionManager = userSessionManager
        )
    }
}

// PANTALLA DE LA CÁMARA -- Aquí es donde pondras tu código para la cámara
@Composable
fun CameraScreenBodyContent(
    idExercise: Int,
    navController: NavController,
    userSessionManager: UserSessionManager
){
    val currentExercise = userSessionManager.currentExercise // ESTA VARIABLE RECUPERA TODA LA INFO DEL EJERCICIO ACTUAL.
    LaunchedEffect(Unit){
        userSessionManager.showExercise(userSessionManager.getUserSession().token, idExercise)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Name: ${currentExercise.value.exercise.name}") // AQUÍ TE PUSE UN EJEMPLO.
    }
}

// ASEGURATE DE QUE CUANDO SE CONCLUYA EL EJERCICIO, TE REDIRIJA A ESTA DIRECCIÓN.
// ----> navController.navigate("myExercisesScreen")
// EN ESA PANTALLA SE SUPONE QUE SE REPRODUCE LA FUNCIÓN QUE SOLICITA EL ARCHIVO DE LA RUTINA
// QUE REALIZA EL USUARIO, EN TEORÍA, DEBERÍA DE ELIMINAR LOS EJERCICIOS YA COMPLETADOS Y DESBLOQUEAR
// EL BOTON DEL SIGUIENTE EJERCICIO.

/* NOTA: Elimina todos los archivos de lo que ya integraste de la cámara o acomodalos de manera
que se integren con esta pantalla en específico. No las borre por cualquier cosa, pero que sepas
que ahí andan.

Creo que ya no tengo más que decir, besitos en el pilin guapa.
 */