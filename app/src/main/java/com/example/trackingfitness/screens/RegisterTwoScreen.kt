package com.example.trackingfitness.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import com.example.trackingfitness.activity.CustomTextFieldMenu
import com.example.trackingfitness.activity.RegisterForm
import com.example.trackingfitness.activity.handleBackPress
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.RegisterViewModel

@Composable
fun RegisterTwoScreen(navController: NavController, viewModel: RegisterViewModel){
    BackHandler {
        handleBackPress(navController, viewModel)
    }
    RegisterForm(
        viewModel = viewModel, content = {
            CustomTextFieldMenu(
                value = viewModel.height,
                onValueChange = { viewModel.updateHeight(it) },
                label = "Height",
                options = (120..250).map { it.toString() },
                placeholder = "Select an option",
                isError = viewModel.heightError != null,
                errorMessage = viewModel.heightError,
                typeOfOption = "Cm"
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextFieldMenu(
                value = viewModel.weight,
                onValueChange = { viewModel.updateWeight(it) },
                label = "Weight",
                options = (40..250).map { it.toString() },
                placeholder = "Select an option",
                isError = viewModel.weightError != null,
                errorMessage = viewModel.weightError,
                typeOfOption = "Kg"
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextFieldMenu(
                value = viewModel.gender,
                onValueChange = { viewModel.updateGender(it) },
                label = "Gender",
                options = listOf("Male", "Female"),
                placeholder = "Select an option",
                isError = viewModel.genderError != null,
                errorMessage = viewModel.genderError
            )
        },
        onButtonClick = {
            navController.navigate(AppScreens.RegisterThreeScreen.route)
            { launchSingleTop = true }
        }
    )
}
