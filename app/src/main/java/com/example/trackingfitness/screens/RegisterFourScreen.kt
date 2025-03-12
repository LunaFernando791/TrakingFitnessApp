package com.example.trackingfitness.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.CustomTextField
import com.example.trackingfitness.activity.CustomTextFieldMenu
import com.example.trackingfitness.activity.RegisterForm
import com.example.trackingfitness.activity.handleBackPress
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.RegisterViewModel

@Composable
fun RegisterFourScreen(navController: NavController, viewModel: RegisterViewModel) {
    BackHandler {
        handleBackPress(navController, viewModel)
    }
    RegisterForm(
        viewModel = viewModel, content = {
            CustomTextField(
                value = viewModel.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = "Username",

                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                isError = viewModel.usernameError != null,
                errorMessage = viewModel.usernameError
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextFieldMenu(
                value = viewModel.experienceLevel,
                onValueChange = { viewModel.updateExperienceLevel(it) },
                label = "Experience Level",
                options = listOf("Principiante", "Intermedio", "Avanzado"),
                placeholder = "Select an option",
                isError = viewModel.experienceLevelError != null,
                errorMessage = viewModel.experienceLevelError
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text= "Select your experience.",
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                modifier = Modifier
                    .padding(10.dp)
            )
            CustomTextFieldMenu(
                value = viewModel.routineType,
                onValueChange = { viewModel.updateRoutineType(it) },
                label = "Routine Type",
                options = listOf(
                    "Improve cardiovascular health", "Strengthen muscles", "Improve flexibility",
                    "Reduce stress", "Weight control", "Increase energy", "Prevent diseases",
                    "Improve posture"
                ),
                placeholder = "Select an option",
                isError = viewModel.routineTypeError != null,
                errorMessage = viewModel.routineTypeError
            )
            Text(
                text= "¿Do you have injuries?.",
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                modifier = Modifier
            )
            InjurySelectionScreen(viewModel)
        },
        onButtonClick = {navController.navigate(AppScreens.StartScreen.route)}
    )
}

@Composable
fun InjurySelectionScreen(viewModel: RegisterViewModel) {
    // Recordar los IDs seleccionados
    val selectedInjuries = remember { mutableStateListOf<Int>() }

    // Lista de lesiones con sus IDs y nombres
    val injuries = listOf(
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
    ) {
        Row {
            // Primera columna
            Column(
                modifier = Modifier.weight(1f)
            ) {
                injuries.take((injuries.size + 1) / 2).forEach { (id, name) ->
                    InjuryCheckbox(id, name, selectedInjuries, viewModel)
                }
            }

            // Segunda columna
            Column(
                modifier = Modifier.weight(1f)
            ) {
                injuries.drop((injuries.size + 1) / 2).forEach { (id, name) ->
                    InjuryCheckbox(id, name, selectedInjuries, viewModel)
                }
            }
        }
    }
}

@Composable
fun InjuryCheckbox(id: Int, name: String, selectedInjuries: MutableList<Int>, viewModel: RegisterViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = selectedInjuries.contains(id),
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    selectedInjuries.add(id) // Agregar el ID a la lista
                } else {
                    selectedInjuries.remove(id) // Remover el ID de la lista
                }
                viewModel.updateInjuries(selectedInjuries as SnapshotStateList<Int>)
            }
        )
        Text(
            text = name,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
