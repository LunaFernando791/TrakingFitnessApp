package com.example.trackingfitness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.activity.CustomTextFieldMenu
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun EditProfileScreen(
    navController: NavController,
    userSession: UserSessionManager
    ) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 50.dp, 10.dp, 10.dp),
        color = MaterialTheme.colorScheme.background
    ){
        BodyProfile(
            navController = navController,
            userSession = userSession
        )
    }
}

@Composable
fun BodyProfile(
    navController: NavController,
    userSession: UserSessionManager
){
    val user = remember { mutableStateOf(userSession.getUserSession()) }
    val name = remember { mutableStateOf(user.value.name) }
    val lastname = remember { mutableStateOf(user.value.lastname) }
    val age = remember { mutableStateOf(user.value.age) }
    val height = remember { mutableStateOf(user.value.height) }
    val weight = remember { mutableStateOf(user.value.weight) }
    val gender = remember { mutableStateOf(user.value.gender) }
    val username = remember { mutableStateOf(user.value.username) }
    val injuries = remember { mutableStateOf(user.value.injuries) }
    val experienceLevel = remember { mutableStateOf(user.value.experienceLevel) }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(20.dp),
    ) {
        item{
            BackButton(
                navController = navController,
                ruta = "profileScreen",
                modifier = Modifier
                .padding(end = 275.dp))
            Text(
                modifier = Modifier
                    .padding(horizontal = 15.dp),
                text = "Actualiza tu información",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 15.dp),
                fontSize = 15.sp,
                text = "Actualiza tu perfil, mantenlo al día, para poder ver tu progreso",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = "Nombre",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = userSession.obtenerNameError() != null,
                errorMessage = userSession.obtenerNameError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = lastname.value,
                onValueChange = { lastname.value = it },
                label = "Apellido",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = userSession.obtenerLastnameError() != null,
                errorMessage = userSession.obtenerLastnameError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = age.value,
                onValueChange = { age.value = it },
                label = "Edad",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = userSession.obtenerAgeError() != null,
                errorMessage = userSession.obtenerAgeError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = height.value,
                onValueChange = { height.value = it },
                label = "Altura",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = userSession.obtenerHeightError() != null,
                errorMessage = userSession.obtenerHeightError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = weight.value,
                onValueChange = { weight.value = it },
                label = "Peso",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = userSession.obtenerWeightError() != null,
                errorMessage = userSession.obtenerWeightError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextFieldMenu(
                value = when (gender.value) {
                    "1" -> "Masculino"
                    "2" -> "Femenino"
                    else -> "Other"
                },
                onValueChange = { gender.value = if (it == "Masculino") "1" else "2" },
                label = "Género",
                options = listOf("Masculino", "Femenino"),
                isError = userSession.obtenerGenderError() != null,
                errorMessage = userSession.obtenerGenderError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = "Nombre de usuario",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = userSession.obtenerUsernameError() != null,
                errorMessage = userSession.obtenerUsernameError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextFieldMenu(
                value = when (experienceLevel.value) {
                    "1" -> "Principiante"
                    "2" -> "Intermedio"
                    "3" -> "Avanzado"
                    else -> "Other"
                },
                onValueChange = { experienceLevel.value = if (it == "Principiante") "1" else if (it == "Intermedio") "2" else if (it == "Avanzado") "3" else "0"},
                label = "Nivel de experiencia",
                options = listOf("Principiante", "Intermedio", "Avanzado"),
                isError = userSession.obtenerExperienceLevelError() != null,
                errorMessage = userSession.obtenerExperienceLevelError()
            )
            Spacer(modifier = Modifier.height(15.dp))
            InjuriesOptions(injuries)
            Button(
                onClick = {
                    userSession.saveUserSession(
                        token = user.value.token,
                        name = name.value,
                        lastname = lastname.value,
                        age = age.value,
                        height = height.value,
                        weight = weight.value,
                        gender = gender.value,
                        email = user.value.email,
                        username = username.value,
                        iconNumber = user.value.iconNumber,
                        injuries = injuries.value.toString(),
                        experienceLevel = user.value.experienceLevel,
                        routineType = user.value.routineType
                    )
                    if (userSession.validateUserSettings()) {
                        userSession.updateAccount()
                        navController.navigate("profileScreen")
                    }
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                ),
                modifier = Modifier.fillMaxSize()
            ){
                Text(
                    text = "Guardar cambios",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun InjuriesOptions(
    injuries: MutableState<List<Int?>>
){
    val selectedInjuries = remember { mutableStateListOf<Int>().apply { addAll(injuries.value.filterNotNull()) } }
    val injuriesList = listOf(
        1 to "Neck",
        2 to "Shoulder",
        3 to "Hip",
        4 to "Knee",
        5 to "Waist",
        6 to "Leg",
        7 to "Wrist"
    )
    Column(
        modifier = Modifier.padding(10.dp)
    ){
        injuriesList.forEach { (id, name) ->
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ){
                Checkbox(
                    checked = selectedInjuries.contains(id),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedInjuries.add(id) // Agregar el ID a la lista
                        } else {
                            selectedInjuries.remove(id) // Remover el ID de la lista
                        }
                        injuries.value = selectedInjuries.toList()
                    }
                )
                Text(
                    text = name,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}