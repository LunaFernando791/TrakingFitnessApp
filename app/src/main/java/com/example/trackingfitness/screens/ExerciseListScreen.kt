package com.example.trackingfitness.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.trackingfitness.R
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.ui.theme.BlueGreen


@Composable
fun ExerciseListScreen(
    navController: NavController
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)
            .background(MaterialTheme.colorScheme.background),
    ) {
        val (scrollableContent, stickyButton) = createRefs()
        // LazyColumn directly, no verticalScroll Modifier needed
        LazyColumn(
            modifier = Modifier
                .constrainAs(scrollableContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(stickyButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(20.dp)
        ) {
            item {
                Button(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = {
                        navController.navigate(AppScreens.PrincipalScreen.route)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Volver",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Text(
                    text = "Lista de ejercicios",
                    fontSize = 35.sp,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            // Add your list of exercises
            items(10) {
                ExerciseCard(i = it)
            }
        }
        // Sticky button at the bottom
        Button(
            onClick = {
                navController.navigate("exerciseCameraScreen")
            },
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
                .constrainAs(stickyButton) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .padding(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (darkTheme.value) Color.White else BlueGreen,
            ),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                tint = if (darkTheme.value) Color.Black else Color.White,
                contentDescription = "play icon",
                modifier = Modifier
                    .size(50.dp)
            )
        }
    }
}

//Preview of the screen Exercises
@Preview(showBackground = true)
@Composable
fun ExerciseScreenPreview() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        color = Color.White
    ) {
        //ExerciseScreen()
    }
}

@Composable
fun ExerciseCard(i: Int) {
    Box(
        modifier = Modifier
            .border(
                width = 5.dp,
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)) // Asegura que el contenido quede dentro de la forma redondeada
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ){
        Row (
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ){
            val img = painterResource(R.drawable.playicon)
            Text(text = "Ejercicio $i",
                color = MaterialTheme.colorScheme.primary)
            Text(text = "Descripción",
                modifier = Modifier
                    .padding(start = 20.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Image(
                painter = img,
                contentDescription = "play",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .padding(10.dp)
            )
        }
    }
}