package com.example.trackingfitness


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.trackingfitness.navigation.AppNavigation
import com.example.trackingfitness.ui.theme.TrackingFitnessTheme

val customFontFamily = FontFamily(Font(R.font.custom_font))

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
                MyApp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val destination = data?.getStringExtra("navigateTo")

            if (destination == "exerciseListScreen") {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navigateTo", "exerciseListScreen") // 🔥 Mandamos la navegación
                startActivity(intent)
                finish() // 🔥 Evita pantalla en blanco
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    var darkTheme by remember { mutableStateOf<Boolean?>(null) }
    val isDarkTheme = darkTheme ?: isSystemInDarkTheme()
    TrackingFitnessTheme(
        darkTheme = isDarkTheme,
    ) {
        Scaffold {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AppNavigation(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { newTheme ->
                        darkTheme = newTheme
                    }
                )
            }
        }
    }
}
