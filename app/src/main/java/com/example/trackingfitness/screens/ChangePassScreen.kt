package com.example.trackingfitness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.viewModel.RecoverPasswordViewModel

@Composable
fun ChangePassScreen(
    navController: NavController,
    darkTheme: Boolean?,
    recoverPasswordViewModel: RecoverPasswordViewModel
){
    Surface {
        BodyContent(navController,
            darkTheme = darkTheme,recoverPasswordViewModel)
    }
}

@Composable
fun BodyContent(
    navController: NavController,
    darkTheme: Boolean?,
    viewModel: RecoverPasswordViewModel
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            textAlign = TextAlign.Center,
            text = "Please enter your new password",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily,
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(30.dp))
        CustomTextField(
            value = viewModel.newPassword,
            onValueChange = {
                viewModel.updateNewPassword(it)
            },
            label = "New Password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.newPasswordError != null,
            errorMessage = viewModel.newPasswordError
        )
        Spacer(modifier = Modifier.height(20.dp))
        CustomTextField(
            value = viewModel.confirmPassword,
            onValueChange = {
                viewModel.updateConfirmPassword(it)
            },
            label = "Confirm Password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = viewModel.confirmPasswordError != null,
            errorMessage = viewModel.confirmPasswordError
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (
                    viewModel.validateAndUpdateNewPassword()
                    && viewModel.validateAndUpdateConfirmPassword()
                ) {
                    viewModel.changePassword()
                    navController.navigate("loginScreen")
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ), modifier = Modifier
                .width(200.dp)
        ) {
            Text("Enviar")
        }

    }
}