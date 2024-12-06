package com.example.trackingfitness.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.CustomTextField
import com.example.trackingfitness.activity.RegisterForm
import com.example.trackingfitness.activity.handleBackPress
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.RegisterViewModel

@Composable
fun RegisterThreeScreen(navController: NavController, viewModel: RegisterViewModel) {
    BackHandler {
        handleBackPress(navController, viewModel)
    }
    RegisterForm(
        viewModel = viewModel, content = {
            CustomTextField(
                value = viewModel.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
                ),
                isError = viewModel.emailError != null,
                errorMessage = viewModel.emailError
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = viewModel.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Password",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError != null,
                errorMessage = viewModel.passwordError
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = "Confirm your password",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError != null,
                errorMessage = viewModel.passwordError
            )
        },
        onButtonClick = { navController.navigate(AppScreens.RegisterFourScreen.route)
        }
    )
}