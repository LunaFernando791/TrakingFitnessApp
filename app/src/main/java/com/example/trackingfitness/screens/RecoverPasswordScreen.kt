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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.activity.ErrorMessages
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.viewModel.LoginViewModel
import com.example.trackingfitness.viewModel.RecoverPasswordViewModel

@Composable
fun RecoverPasswordScreen(navController: NavController, recoverPasswordViewModel: RecoverPasswordViewModel){
    ContainerContent(recoverPasswordViewModel, navController,)
}


@Composable
fun ContainerContent(viewModel: RecoverPasswordViewModel, navController: NavController){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(25.dp)
    ){
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Recover password",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily,
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Add your email and we will send you a code to reset your password",
            color = if (darkTheme.value) Color.White else Color.Black,
        )
        Spacer(modifier = Modifier.height(20.dp))
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
        Button(
            onClick = {
                if(viewModel.validateAndUpdate()){
                    viewModel.forgetPassword()
                    navController.navigate(AppScreens.OTPScreen.route)
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ), modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
        ) {
            Text("Solicitar código")
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    isError: Boolean,
    errorMessage: String?
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label,
            color = MaterialTheme.colorScheme.primary) },
        visualTransformation = visualTransformation,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
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
                if (isError) Color.Red else MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(12.dp)
            )
            .height(50.dp),
        keyboardOptions = keyboardOptions
    )
    ErrorMessages(isError, errorMessage)
}
