package com.example.trackingfitness.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
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
import com.example.trackingfitness.activity.ErrorMessages
import com.example.trackingfitness.activity.WarningMessage
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.LoginViewModel
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
) {
    Column(
        modifier = Modifier
            .background(if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Please enter your email and password",
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally),
            color = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
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
        ClickableText(
            text = AnnotatedString(
                text = "Forgot password?, click here",
                spanStyle = SpanStyle(
                    color = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 18.sp
                )
            ),
            onClick = {
                navController.navigate("recoverPassword")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if(loginViewModel.validateAndUpdate()){
                    loginViewModel.loginUser()
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                contentColor = if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
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
        if (loginViewModel.loginSuccess) {
            WarningMessage(message = "Inicio de sesión exitoso")
            navController.navigate(AppScreens.PrincipalScreen.route)
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
            color = if(darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary) },
        visualTransformation = visualTransformation,
        textStyle = TextStyle(
            color = if(darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
            fontSize = 15.sp
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = if(darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
            focusedIndicatorColor = if (isError) Color.Red else Color.Blue,
            unfocusedIndicatorColor = if (isError) Color.Red else Color.Gray,
            errorIndicatorColor = Color.Red
        ),

        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .border(
                2.dp,
                if (isError) Color.Red else if(darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                RoundedCornerShape(12.dp)
            )
            .height(50.dp),
        keyboardOptions = keyboardOptions
    )
    ErrorMessages(isError, errorMessage)
}



/*val loginState by loginViewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        when{
            loginState?.isSuccess == true -> {
                val userId = loginState?.getOrNull()?.user
                val user = UserLogin(
                    id = loginState?.getOrNull()?.user?.id ?: 0,
                    name = loginState?.getOrNull()?.user?.personal_name ?: "",
                    lastname = loginState?.getOrNull()?.user?.last_name ?: "",
                    age = loginState?.getOrNull()?.user?.age ?: 0,
                    height = loginState?.getOrNull()?.user?.height ?: 0.0,
                    weight = loginState?.getOrNull()?.user?.weight ?: 0.0,
                    email = loginState?.getOrNull()?.user?.email ?: "",
                    username = loginState?.getOrNull()?.user?.username ?: "",
                    experienceLevel = loginState?.getOrNull()?.user?.experience_level_id ?: 0,
                )
                if (userId != null) {
                    navController.navigate("screenOne/${user}")
                }
            }
            loginState?.isFailure == true -> {
                val errorMessage = loginState?.exceptionOrNull()?.message

            }
            else -> Unit
        }
    }*/