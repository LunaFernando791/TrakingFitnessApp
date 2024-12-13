package com.example.trackingfitness.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.RecoverPasswordViewModel


var codeLength = 6
@Composable
fun OTPScreen(
    navController: NavController,
    recoverPasswordViewModel: RecoverPasswordViewModel) {
    Surface {
        BodyContentTwo(recoverPasswordViewModel,navController)
    }
}

@Composable
fun BodyContentTwo(viewModel: RecoverPasswordViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Please enter your code",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily,
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .width(370.dp)
                .height(200.dp)
                .padding(10.dp)
                ,
            contentAlignment = Alignment.TopCenter
        ){
            VerificationCodeInput(
                codeLength = codeLength,
                onCodeEntered = {
                    viewModel.updateOTP(it)
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    viewModel.createOTP()
                    navController.navigate("changePassScreen")
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary
                ), modifier = Modifier
                    .width(200.dp)
                    .padding(top = 100.dp)
            ) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun VerificationCodeInput(
    codeLength: Int,
    onCodeEntered: (String) -> Unit
) {
    val code = remember { mutableStateOf(List(codeLength) { "" }) }
    val focusRequesters = List(codeLength) { FocusRequester() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        code.value.forEachIndexed { index, value ->
            TextField(
                value = value,
                onValueChange = { input ->
                    if (input.length <= 1) {
                        code.value = code.value.toMutableList().also { it[index] = input }
                        if (input.isNotEmpty()) {
                            if (index < 6 - 1) {
                                focusRequesters[index + 1].requestFocus()
                            } else {
                                onCodeEntered(code.value.joinToString(""))
                            }
                        }
                    }
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    textDecoration = null
                ),
                modifier = Modifier
                    .width(50.dp)
                    .height(80.dp)
                    .focusRequester(focusRequesters[index])
                    .border(
                        1.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(10.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == codeLength - 1) ImeAction.Done else ImeAction.Next
                )
            )
        }
    }
}

