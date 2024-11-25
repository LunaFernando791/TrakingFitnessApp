package com.example.trackingfitness

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.navigation.AppNavigation
import com.example.trackingfitness.ui.theme.TrackingFitnessTheme

val customFontFamily = FontFamily(Font(R.font.custom_font))
const val darkTheme = false
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    TrackingFitnessTheme(darkTheme) {
        Scaffold {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation()
            }
        }
    }
}



@Composable
fun MyCalendar() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .width(360.dp)
            .height(250.dp)
            .background(Color.LightGray)
    ) {
        Text(
            text = "SIGUE TU DÍA A DÍA...",
            color = Color.Blue,
            style = TextStyle(
                fontSize = 20.sp,
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
            modifier = Modifier.padding(15.dp)
        )
    }
}

@Composable
fun TopMenu(navController: NavController) {
    Box(
        modifier = Modifier
            .padding(
                vertical = 25.dp,
                horizontal = 10.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)  // Color de fondo
            .width(170.dp)
            .height(300.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Empieza tu rutina diaria...",
                color = Color.Blue,
                style = TextStyle(
                    fontSize = 30.sp,
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                ),
                modifier = Modifier.padding(bottom = 30.dp)
            )
            Box(modifier = Modifier
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .fillMaxSize()
                .clickable { navController.navigate("screenTwo") }) {
                Text(text = "Reproduce tu rutina", modifier = Modifier.padding(10.dp))
            }

        }
    }
    Column {
        Box(
            modifier = Modifier
                .padding(vertical = 25.dp, horizontal = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .width(200.dp)
                .height(130.dp)
                .background(Color.LightGray),
        ) {}
        Box(
            modifier = Modifier
                .padding(vertical = 15.dp, horizontal = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .width(200.dp)
                .height(130.dp)
                .background(Color.LightGray),
        ) {}
    }
}

@Composable
fun NavBarMenu(navController: NavController) {
    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()

    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "calories",
                modifier = Modifier
                    .size(width = 70.dp, height = 70.dp)
                    .clickable { navController.navigate("screenOne") },
                contentScale = ContentScale.Fit
            )
        }
        Box {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "calories",
                modifier = Modifier.size(width = 70.dp, height = 70.dp),
                contentScale = ContentScale.Fit
            )
        }
        Box {
            Image(
                painter = painterResource(id = R.drawable.calorias),
                contentDescription = "calories",
                modifier = Modifier.size(width = 70.dp, height = 70.dp),
                contentScale = ContentScale.Fit
            )
        }
        Box {
            Image(
                painter = painterResource(id = R.drawable.calorias),
                contentDescription = "calories",
                modifier = Modifier.size(width = 70.dp, height = 70.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}