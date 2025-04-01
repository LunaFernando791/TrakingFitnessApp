package com.example.trackingfitness.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.LockOrientationInThisScreen
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.conection.Exercise
import com.example.trackingfitness.conection.RetrofitInstance.BASE_URL
import com.example.trackingfitness.conection.Sets
import com.example.trackingfitness.ui.theme.BlueGreen
import com.example.trackingfitness.ui.theme.BorderColor
import com.example.trackingfitness.ui.theme.PositionColor
import com.example.trackingfitness.viewModel.UserSessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.JsonArray
import com.google.gson.JsonObject


@Composable
fun ExerciseListScreen(
    userSession: UserSessionManager,
    darkTheme: Boolean?,
    navController: NavController
) {
    LockOrientationInThisScreen()
    val userExercise = userSession.exercises.collectAsState()
    val availableExercises = remember {
        mutableStateOf(userExercise.value?.exercises ?: emptyList())
    }
    val myExercises = remember {
        mutableStateOf(userExercise.value?.exercisesList ?: emptyList())
    }
    val sets = userExercise.value?.routineSets
    val selectedSets = remember { mutableStateMapOf<Int, Int>() }
    val selectedReps = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(Unit) {
        userSession.getExercises()
    }

    // Actualizar los estados locales cuando la respuesta cambia
    LaunchedEffect(userExercise.value) {
        userExercise.value?.let { response ->
            availableExercises.value = response.exercises ?: emptyList()
            myExercises.value = response.exercisesList ?: emptyList()
            response.exercisesList?.forEach { exercise ->
                selectedSets[exercise.id] = sets?.sets?.firstOrNull() ?: 3 // Valor por defecto
                selectedReps[exercise.id] = sets?.reps?.firstOrNull() ?: 10
            }
        }
    }

    val rutineCreated = userSession.rutineCreated.collectAsState()
    val rutineCompleted = userSession.rutineCompleted.collectAsState()
    if (!rutineCreated.value) {
        LazyColumn(
            modifier = Modifier
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        )
        {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                BackButton(
                    navController = navController,
                    ruta = "homeScreen",
                    modifier = Modifier.padding(end = 250.dp)
                )
                if (rutineCompleted.value){
                    Text(
                        text = "Routine completed",
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }else {
                    userExercise.value?.let {
                        Text(
                            text = "Routine: ${it.routineType}",
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "List of Exercises: ",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            // Mostrar la lista de ejercicios actuales (tu rutina)
            if (myExercises.value.isNotEmpty()) {
                items(myExercises.value) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        sets = sets,
                        availableExercises = availableExercises.value,
                        selectedSets = selectedSets[exercise.id] ?: 0,
                        selectedReps = selectedReps[exercise.id] ?: 0,
                        onSetsSelected = { newSets -> selectedSets[exercise.id] = newSets },
                        onRepsSelected = { newReps -> selectedReps[exercise.id] = newReps },
                        darkTheme = darkTheme,
                        onExerciseSelected = { selected, current ->
                            swapExercises(current, selected, availableExercises, myExercises, selectedSets, selectedReps)
                        }
                    )
                }
                item{
                    var isSubmitting by remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                isSubmitting = true
                                coroutineScope.launch {
                                    submitRoutine(
                                        userSession,
                                        myExercises.value,
                                        selectedSets,
                                        selectedReps
                                    )
                                    isSubmitting = false
                                    navController.navigate("myExercisesScreen")
                                }
                            },
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .width(150.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (darkTheme==true) Color.White else BlueGreen
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Text(text = "Submit",
                                    color = if (darkTheme==true) Color.Black else Color.White)
                            }
                        }
                    }
                }
            } else {
                item {
                    if(rutineCreated.value) {
                        Text(
                            text = "No exercises added yet",
                        )
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    if(rutineCompleted.value) {
                        Text(
                            text = "Congratulations, you have completed your routine",
                        )
                    }
                }
            }
        }
    }
    else{
        navController.navigate("myExercisesScreen")
    }

}

// -------------------
// TARJETA DE EJERCICIO
// -------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(
    exercise: Exercise,
    darkTheme: Boolean?,
    sets: Sets?,
    availableExercises: List<Exercise>,
    selectedSets: Int,
    selectedReps: Int,
    onSetsSelected: (Int) -> Unit,
    onRepsSelected: (Int) -> Unit,
    onExerciseSelected: (selectedExercise: Exercise, currentExercise: Exercise) -> Unit
) {
    val isVisible = remember { mutableStateOf(false) }
    var setsExpanded by remember { mutableStateOf(false) }
    var repsExpanded by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val setsOptions = sets?.sets ?: emptyList()
    val repsOptions = sets?.reps ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(
                10.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.secondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val url = "${BASE_URL}storage/${exercise.image_path}"
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(20.dp),
                    ),
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (exercise.name.length > 10) exercise.name.substring(
                            0,
                            10
                        ) + "..." else exercise.name,
                        modifier = Modifier.padding(start = 20.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (isVisible.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        tint = if (darkTheme==true) Color.White else Color.Black,
                        contentDescription = "toggle details",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 10.dp)
                            .clickable { isVisible.value = !isVisible.value }
                    )
                }
                Text(
                    text = if(exercise.warning.isNullOrEmpty()) "" else exercise.warning.toString(),
                    modifier = Modifier.padding(end = 20.dp),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                ) {
                    ExposedDropdownMenuBox(
                        expanded = setsExpanded,
                        onExpandedChange = { setsExpanded = it }, // Corrección aquí
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (darkTheme==true) Color.White else BorderColor,
                                shape = RoundedCornerShape(50.dp)
                            )
                    ) {
                        TextField(
                            value = selectedSets.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sets",
                                fontSize = 10.sp,
                                color = if (darkTheme==true) Color.Black else Color.White)},
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = setsExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .height(45.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = if (darkTheme==true) Color.Black else Color.White,
                                unfocusedTextColor = if (darkTheme==true) Color.Black else Color.White,
                                focusedContainerColor = if (darkTheme==true) PositionColor else BorderColor,
                                unfocusedContainerColor = if (darkTheme==true) Color.White else PositionColor,
                                disabledContainerColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                disabledIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                focusedLabelColor = if (darkTheme==true) Color.Black else Color.White,
                                unfocusedLabelColor = if (darkTheme==true) Color.Black else Color.White,
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                        )
                        ExposedDropdownMenu(
                            expanded = setsExpanded,
                            onDismissRequest = { setsExpanded = false },
                            modifier = Modifier
                                .menuAnchor()
                                .background(if (darkTheme==true) Color.White else BorderColor),
                        ) {
                            setsOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.toString(),
                                        color = if (darkTheme==true) Color.Black else Color.White) },
                                    onClick = {
                                        onSetsSelected(option)
                                        setsExpanded = false
                                    },
                                    modifier = Modifier
                                        .background(if (darkTheme==true) Color.White else BorderColor, shape = RoundedCornerShape(50.dp))
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    // Dropdown para Reps
                    ExposedDropdownMenuBox(
                        expanded = repsExpanded,
                        onExpandedChange = { repsExpanded = it } // Corrección aquí
                    ) {
                        TextField(
                            value = selectedReps.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reps",
                                fontSize = 10.sp,
                                color = if (darkTheme==true) Color.Black else Color.White) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = repsExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .height(45.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = if (darkTheme==true) Color.Black else Color.White,
                                unfocusedTextColor = if (darkTheme==true) Color.Black else Color.White,
                                focusedContainerColor = if (darkTheme==true) PositionColor else BorderColor,
                                unfocusedContainerColor = if (darkTheme==true) Color.White else PositionColor,
                                disabledContainerColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                disabledIndicatorColor = MaterialTheme.colorScheme.tertiary,
                                focusedLabelColor = if (darkTheme==true) Color.Black else Color.White,
                                unfocusedLabelColor = if (darkTheme==true) Color.Black else Color.White,
                            ),
                            textStyle = TextStyle(fontSize = 10.sp),
                        )
                        ExposedDropdownMenu(
                            expanded = repsExpanded,
                            onDismissRequest = { repsExpanded = false },
                            modifier = Modifier
                                .background(if (darkTheme==true) Color.White else BorderColor)
                        ) {
                            repsOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.toString(),
                                        color = if (darkTheme==true) Color.Black else Color.White) },
                                    onClick = {
                                        onRepsSelected(option)
                                        repsExpanded = false
                                    },
                                    modifier = Modifier
                                        .background(if (darkTheme==true) Color.White else BorderColor, shape = RoundedCornerShape(50.dp))
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (isVisible.value) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Description: ") }
                    append(exercise.description ?: "")
                }
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Watch the video example ") }
                },
                modifier = Modifier
                    .clickable { uriHandler.openUri(exercise.video_url ?: "") }
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(5.dp),
                textAlign = TextAlign.Center
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Change your exercise: ") }
                }
            )
            // Mostrar los ejercicios disponibles para cambiar (lista de availableExercises)
            availableExercises.forEach { ex ->
                ChangeExerciseCards(
                    exercise = ex,
                    onExerciseSelected = { selectedExercise ->
                        onExerciseSelected(selectedExercise, exercise)
                    }
                )
            }
        }
    }
}
// ----------------------------
// COMPONENTE PARA CAMBIAR EJERCICIOS
// ----------------------------
@Composable
fun ChangeExerciseCards(
    exercise: Exercise,
    onExerciseSelected: (Exercise) -> Unit
) {
    val url = "${BASE_URL}storage/${exercise.image_path}"
    Row(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
            .clickable { onExerciseSelected(exercise) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp)),
            painter = rememberAsyncImagePainter(url),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Text(
            text = exercise.name,
            modifier = Modifier.padding(start = 20.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ----------------------------
// FUNCIÓN QUE REALIZA EL INTERCAMBIO (SWAP) DE EJERCICIOS
// ----------------------------
fun swapExercises(
    currentExercise: Exercise,
    selectedExercise: Exercise,
    availableExercises: MutableState<List<Exercise>>,
    myExercises: MutableState<List<Exercise>>,
    selectedSets: SnapshotStateMap<Int, Int>,
    selectedReps: SnapshotStateMap<Int, Int>
) {
    val availList = availableExercises.value.toMutableList()
    val myList = myExercises.value.toMutableList()

    if (availList.contains(selectedExercise) && myList.contains(currentExercise)) {
        val index = myList.indexOf(currentExercise)

        // Guardar sets y reps del ejercicio actual
        val currentSets = selectedSets[currentExercise.id] ?: 0
        val currentReps = selectedReps[currentExercise.id] ?: 0

        // Remover el ejercicio seleccionado de la lista de disponibles
        availList.remove(selectedExercise)
        // Remover el ejercicio actual de tu rutina
        myList.remove(currentExercise)

        // Intercambiar: el seleccionado se agrega a tu rutina y el actual se vuelve a agregar a disponibles
        myList.add(index, selectedExercise)
        availList.add(currentExercise)

        // Mantener los sets y reps para el nuevo ejercicio
        val newSets = selectedSets.toMutableMap().apply {
            this[selectedExercise.id] = currentSets
            remove(currentExercise.id) // Eliminar el antiguo
        }
        val newReps = selectedReps.toMutableMap().apply {
            this[selectedExercise.id] = currentReps
            remove(currentExercise.id)
        }

        // Actualizar listas y mapas
        availableExercises.value = availList
        myExercises.value = myList
        selectedSets.putAll(newSets)
        selectedReps.putAll(newReps)
    }
}


fun submitRoutine(
    userSession: UserSessionManager,
    myExercises: List<Exercise>,
    selectedSets: Map<Int, Int>,
    selectedReps: Map<Int, Int>
) {
    val jsonArray = JsonArray()

    myExercises.forEachIndexed { index, exercise ->
        val order = index + 1
        val exerciseDetails = JsonObject().apply {
            addProperty("id", exercise.id)
            addProperty("sets", selectedSets[exercise.id] ?: 0) // Manejo de nulos
            addProperty("reps", selectedReps[exercise.id] ?: 0)
        }

        val exerciseEntry = JsonArray().apply {
            add(order)
            add(exerciseDetails)
        }
        jsonArray.add(exerciseEntry)
    }

    val jsonBody = JsonObject().apply {
        add("selected_exercises", jsonArray)
    }.toString()
    val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
    Log.d("JSON BODY", "JSON BODY: $jsonBody")
    userSession.sendRoutine(requestBody, userSession.getUserSession().token)
}