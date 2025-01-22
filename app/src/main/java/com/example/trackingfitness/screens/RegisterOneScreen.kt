package com.example.trackingfitness.screens

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
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.RegisterViewModel

@Composable
fun RegisterOneScreen(navController: NavController, viewModel: RegisterViewModel){
    RegisterForm(
        viewModel = viewModel, content = {
            CustomTextField(
                value = viewModel.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Name",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                isError = viewModel.nameError != null,
                errorMessage = viewModel.nameError
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = viewModel.lastname,
                onValueChange = { viewModel.updateLastName(it) },
                label = "Last Name",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
                isError = viewModel.lastnameError != null,
                errorMessage = viewModel.lastnameError
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextFieldMenu(
                value = viewModel.age,
                onValueChange = { viewModel.updateAge(it) },
                label = "Age",
                options = (18..99).map { it.toString() },
                placeholder = "Select an option",
                isError = viewModel.ageError != null,
                errorMessage = viewModel.ageError,
                typeOfOption = "Years"
            )
        },
        onButtonClick = {
            navController.navigate(AppScreens.RegisterTwoScreen.route
            ){
            launchSingleTop = true
            }
        }
    )
}