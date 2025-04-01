package com.example.trackingfitness.screens
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.viewModel.UserSessionManager


@Composable
fun EditPasswordScreen(
    navController: NavController,
    darkTheme: Boolean?,
    userSession: UserSessionManager
) {
    Surface (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp)
    ){
        BodyScreen(
            navController,
            darkTheme,
            userSession
        )
    }
}

@Composable
fun BodyScreen(
    navController: NavController,
    darkTheme: Boolean?,
    userSession: UserSessionManager
) {
    Column (
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        BackButton(
            navController = navController,
            ruta = "profileScreen",
            modifier = Modifier
            .padding(end = 275.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Update your password",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            fontSize = 15.sp,
            text = "Update your password and then login again to verify the change",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(15.dp))
        CustomTextField(
            value = userSession.oldPassword,
            onValueChange = { userSession.changeOldPasswordValue(it) },
            label = "Current password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            isError = userSession.obtenerPasswordError() != null,
            errorMessage = userSession.obtenerPasswordError()
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = userSession.newPassword,
            onValueChange = { userSession.changeNewPasswordValue(it) },
            label = "New password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            isError = userSession.obtenerPasswordError() != null,
            errorMessage = userSession.obtenerPasswordError()
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = userSession.passwordConfirmation,
            onValueChange = { userSession.changePasswordConfirmationValue(it) },
            label = "Confirm password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            isError = userSession.obtenerPasswordError() != null,
            errorMessage = userSession.obtenerPasswordError()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button (
            modifier = Modifier
                .padding(10.dp),
            onClick = {
                if(userSession.validateAndUpdatePassword()) {
                    userSession.updatePassword(userSession.oldPassword, userSession.newPassword, userSession.passwordConfirmation)
                    userSession.logoutUser()
                    navController.navigate("startScreen")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = if (darkTheme==true) Color.White else Color.Black
            )
        ) {
            Text("Update")
        }
    }
}
