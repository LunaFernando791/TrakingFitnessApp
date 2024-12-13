package com.example.trackingfitness.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trackingfitness.R
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.navigation.AppScreens
import com.example.trackingfitness.ui.theme.BlueGreen
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun PrincipalScreen(
    navController: NavController,
    userSession: UserSessionManager
){
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ){
        BodyContent(
            navController,
            userSession
        )
    }
}

@Composable
fun BodyContent(
    navController: NavController,
    userSessionManager: UserSessionManager
){
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 80.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier = Modifier.padding(end = 30.dp),
                text = "Bienvenido ${userSessionManager.getUserSession().name}",
                color = MaterialTheme.colorScheme.primary,
                style = TextStyle(
                    fontSize = 25.sp,
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(10f, 10f),
                        blurRadius = 8f
                    )
                )
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ToggleSwitch(
                    isChecked = darkTheme.value,
                    onCheckedChange = { isChecked ->
                        darkTheme.value = isChecked
                    }
                )
                Text(
                    text = if (darkTheme.value) "Modo Oscuro" else "Modo Claro",
                    color = MaterialTheme.colorScheme.primary,
                    style = TextStyle(
                        fontSize = 10.sp,
                    )
                )
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            TopMenu(navController)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyCalendar()
        }
    }
}

// Calendario de los días donde se realizó la rutina
@Composable
fun MyCalendar() {
    Box( // CALENDAR TO SHOW THE DAY TO DAY PROGRESS
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .width(400.dp)
            .height(320.dp)
            .background(MaterialTheme.colorScheme.secondary)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = "SIGUE TU DÍA A DÍA...",
            color = MaterialTheme.colorScheme.primary,
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

// Menú de opciones
@Composable
fun TopMenu(
    navController: NavController
) {
    Box( // Botón para iniciar la rutina
        modifier = Modifier
            .padding(
                vertical = 25.dp,
                horizontal = 10.dp
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)  // Color de fondo
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
                color = MaterialTheme.colorScheme.primary,
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
                .background(Color.Gray)
                .fillMaxSize()
                .clickable {
                    navController.navigate("exerciseScreen")
                }) {
                val img = painterResource(R.drawable.playicon)
                Image(
                    painter = img ,
                    contentDescription = "startImage",
                    modifier = Modifier.fillMaxSize()
                )
            }

        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
            ){
        Row( // Botón para ver el ranking
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp, horizontal = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp)
                )
                .height(130.dp)
                .background(MaterialTheme.colorScheme.secondary),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "profile Details",
                modifier = Modifier
                    .size(90.dp)
                    .padding(start = 30.dp, end = 10.dp)
                )
            Text(
                text = "Ranking semanal.",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                )
        }
        Row( // Botón para ver el perfil
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp)
                )
                .height(130.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { navController.navigate("profileScreen") },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Default.AccountCircle,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "profile Details",
                modifier = Modifier
                    .size(55.dp)
            )
            Text(
                text = "Mi Perfil",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 25.sp,
                modifier = Modifier
                    .padding(start = 5.dp)
            )
        }
    }
}

// Botón para cambiar el tema
@Composable
fun ToggleSwitch(isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = isChecked,
        onCheckedChange = { isChecked ->
            onCheckedChange(isChecked)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = BlueGreen,
            uncheckedThumbColor = Color.Gray
        )
    )
}