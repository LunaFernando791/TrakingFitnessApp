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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.trackingfitness.customFontFamily
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.navigation.AppScreens
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@Composable
fun OTPScreen() {
    BodyContent()
}

@Preview(showBackground = true)
@Composable
fun BodyContent(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Please enter your code",
            color = if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily,
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .width(320.dp)
                .height(200.dp)
                .border(
                    2.dp,
                    if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onPrimary,
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ){
            VerificationCodeInput(
                codeLength = 4,
                onCodeEntered = { code ->
                    // Handle the entered code here
                }
            )
        }
    }
}

@Composable
fun VerificationCodeInput(
    codeLength: Int = 4,
    onCodeEntered: (String) -> Unit
) {
    val code = remember { mutableStateOf(List(codeLength) { "" }) }
    val focusRequesters = List(codeLength) { FocusRequester() }

    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(20.dp)
    ) {
        code.value.forEachIndexed { index, value ->
            TextField(
                value = value,
                onValueChange = { input ->
                    if (input.length <= 1) {
                        code.value = code.value.toMutableList().also { it[index] = input }
                        if (input.isNotEmpty()) {
                            if (index < codeLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            } else {
                                onCodeEntered(code.value.joinToString(""))
                            }
                        }
                    }
                },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                ),
                modifier = Modifier
                    .size(60.dp)
                    .focusRequester(focusRequesters[index])
                    .border(
                        2.dp,
                        if (value.isEmpty()) Color.Gray else MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
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

