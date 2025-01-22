package com.example.trackingfitness.activity

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.ui.theme.BlueGreen
import com.example.trackingfitness.viewModel.UserSessionManager

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

@Composable
fun BackButton(navController: NavController,ruta: String, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        onClick = { navController.navigate(ruta) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = "Volver")
    }
}

@Composable
fun ExperienceBar(
    userLevel: String,
    percentageProgress: String,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ){
        Box(
            modifier = Modifier
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(16.dp))
                .width(50.dp)
                .height(50.dp)
                .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "NIVEL")
                Text(text = userLevel.ifEmpty { "0" })
            }
        }
        Column{

            val progressBar = if (percentageProgress.toFloat() == 0.0f) {
                0
            }else{
                percentageProgress.toFloat()/2000
            }
            LinearProgressIndicator(
                progress = {progressBar.toFloat()},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(40.dp))
                ,
                color = if (darkTheme.value) Color.White else BlueGreen,
                trackColor = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = "${percentageProgress}/2000",
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}
