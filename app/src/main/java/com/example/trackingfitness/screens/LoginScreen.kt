package com.example.trackingfitness.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.activity.ErrorMessages
import com.example.trackingfitness.activity.WarningMessage
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        BackButton(
            navController = navController,
            ruta = "startScreen",
            modifier = Modifier
            .padding(end = 275.dp))
        Text(
            text = "Please enter your email and password",
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextFieldLogin(
            value = loginViewModel.email,
            onValueChange = {
                loginViewModel.updateEmail(it)
            },
            label = "Email",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
            ),
            isError = loginViewModel.emailError != null,
            errorMessage = loginViewModel.emailError,
        )
        Spacer(modifier = Modifier.height(15.dp))
        CustomTextFieldLogin(
            value = loginViewModel.password,
            onValueChange = { loginViewModel.updatePassword(it) },
            label = "Password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = loginViewModel.passwordError != null,
            errorMessage = loginViewModel.passwordError
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
                text = "¿Forgot your password?, click here",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable {
                        navController.navigate("recoverPassword")
                    }
        )
        Spacer(modifier = Modifier.height(16.dp))
        val loginSuccess by loginViewModel.loginSuccess.collectAsState()
        Button(
            onClick = {
                if(loginViewModel.validateAndUpdate()){
                    loginViewModel.loginUser()
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor =MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ), modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (loginViewModel.loginError) {
            WarningMessage(message = loginViewModel.loginMessage)
        }
        // Navegación basada en el estado de loginSuccess
        if (loginSuccess) {
            LaunchedEffect(Unit) {
                navController.navigate(AppScreens.PrincipalScreen.route)
                loginViewModel.resetLoginState() // Restablecer el estado si es necesario
            }
        }
        if (loginViewModel.loginUnverified) {
            WarningMessage(message = loginViewModel.loginMessage)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CustomTextFieldLogin(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    isError: Boolean,
    errorMessage: String?
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label,
            color = MaterialTheme.colorScheme.primary,
        ) },
        visualTransformation = visualTransformation,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = Color.LightGray,
            focusedIndicatorColor = if (isError) Color.Red else Color.Blue,
            unfocusedIndicatorColor = if (isError) Color.Red else Color.Gray,
            errorIndicatorColor = Color.Red
        ),

        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .border(
                2.dp,
                if (isError) Color.Red else MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(12.dp)
            )
            .height(50.dp),
        keyboardOptions = keyboardOptions
    )
    ErrorMessages(isError, errorMessage)
}


