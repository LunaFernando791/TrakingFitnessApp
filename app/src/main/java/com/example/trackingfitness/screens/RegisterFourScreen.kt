package com.example.trackingfitness.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
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
        },
        onButtonClick = {navController.navigate(AppScreens.StartScreen.route)}
    )
}