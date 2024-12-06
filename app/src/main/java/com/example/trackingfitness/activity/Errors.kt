package com.example.trackingfitness.activity

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorMessages(isError: Boolean, errorMessage: String?) {
    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
