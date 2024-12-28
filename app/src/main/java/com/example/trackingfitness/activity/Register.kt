package com.example.trackingfitness.activity

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.RegisterViewModel

@Composable
fun RegisterForm(
    viewModel: RegisterViewModel,
    content: @Composable (RegisterViewModel) -> Unit,
    onButtonClick: () -> Unit
) {
    val progressColor by viewModel.progressColor
    val trackColor by viewModel.trackColor

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(25.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        LinearProgressIndicator(
            progress = { viewModel.progress },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .height(7.dp),
            color = progressColor,
            trackColor = trackColor,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Share us your data...",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
        )
        content(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                when (viewModel.progress) {
                    0f -> {
                        if(viewModel.updateProgressRegister())
                            onButtonClick()
                    }
                    0.25f -> {
                        if(viewModel.updateProgressRegister2())
                            onButtonClick()
                    }

                    0.5f -> {
                        if(viewModel.updateProgressRegister3())
                            onButtonClick()
                    }
                    0.75f -> {
                        viewModel.updateProgressRegister4()
                    }
                }
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ), modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp)
        ) {
            Text("Continuar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (viewModel.errorRegister) {
            WarningMessage(message = viewModel.emailErrors)
        }else if (viewModel.registrationSuccess) {
            viewModel.incrementProgress(0.25f)
            Toast.makeText(
                LocalContext.current,
                "Registro exitoso, confirma tu correo para iniciar sesión.",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.resetStates()
            onButtonClick()
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun WarningMessage(
    message: String,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar(
            message = message,
        )
    }
    SnackbarHost(
        hostState = snackbarHostState
    ) {
        Snackbar(
            snackbarData = it,
            containerColor = Color.Red,
            contentColor = Color.White,
        )
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
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(
            label,
            color = MaterialTheme.colorScheme.primary
        ) },
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedLabelColor = Color.LightGray,
            focusedIndicatorColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = if (isError) Color.Red else MaterialTheme.colorScheme.tertiary,
            errorIndicatorColor = Color.Red
        ),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .border(
                2.dp,
                if (isError) Color.Red else MaterialTheme.colorScheme.tertiary,
                RoundedCornerShape(15.dp)
            )
            .height(50.dp),
        keyboardOptions = keyboardOptions
    )
    ErrorMessages(isError, errorMessage)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextFieldMenu(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    placeholder: String = "Select an option",
    isError: Boolean,
    errorMessage: String?,
    typeOfOption: String? = ""
) {
    val selectedText = remember { mutableStateOf(value.ifEmpty { placeholder }) }
    val expanded = remember { mutableStateOf(false) }
    Box {
        ExposedDropdownMenuBox(
            expanded = expanded.value, onExpandedChange = {
                expanded.value = !expanded.value
            }, modifier = Modifier
        ) {
            TextField(
                value = selectedText.value,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.primary
                    )
                        },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = Color.LightGray,
                    focusedIndicatorColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (isError) Color.Red else MaterialTheme.colorScheme.tertiary,
                    errorIndicatorColor = Color.Red
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .border(
                        2.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(15.dp)
                    )
                    .height(50.dp),
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .width(200.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                options.forEach {
                    DropdownMenuItem(text = { Text(text = "$it $typeOfOption"
                        , color = MaterialTheme.colorScheme.primary) }
                        , onClick = {
                        selectedText.value = "$it $typeOfOption"
                        onValueChange(it)
                        expanded.value = false
                    }, modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .align(Alignment.CenterHorizontally)
                        .height(60.dp)
                    )
                }
            }
        }
    }
    ErrorMessages(isError, errorMessage)
}

fun handleBackPress(navController: NavController, viewModel: RegisterViewModel) {
    if (viewModel.progress > 0) {
        viewModel.decrementProgress(0.25f)
        navController.popBackStack()
    }
    viewModel.errorRegister = false
}