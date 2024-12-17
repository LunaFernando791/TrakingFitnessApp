package com.example.trackingfitness.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.UserSessionManager


@Composable
fun EditEmailScreen(
    navController: NavController,
    userSession: UserSessionManager
) {
    Surface (
        modifier = Modifier
            .fillMaxSize()
    ){
        BodyScreenContent(
            navController,
            userSession
        )
    }
}

@Composable
fun BodyScreenContent(
    navController: NavController,
    userSession: UserSessionManager
    ) {
    Column (
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        CustomTextField(
            value = userSession.email,
            onValueChange = { userSession.changeEmailValue(it) },
            label = "New Email",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            isError = userSession.obtenerEmailError() != null,
            errorMessage = userSession.obtenerEmailError()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button (
            modifier = Modifier
                .padding(10.dp),
            onClick = {
                userSession.updateEmail(userSession.email)
                userSession.logoutUser()
                navController.navigate("startScreen")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = if (darkTheme.value) Color.White else Color.Black
            )
        ) {
            Text("Update Email")
        }
    }
}
