package com.example.trackingfitness.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.trackingfitness.R
import com.example.trackingfitness.activity.ExperienceBar
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.ui.theme.BlueGreen
import com.example.trackingfitness.viewModel.FriendsViewModel
import com.example.trackingfitness.viewModel.UserSessionManager
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun PrincipalScreen(
    navController: NavController,
    userSession: UserSessionManager,
    friendsViewModel: FriendsViewModel
){
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ){
        BodyContent(
            navController,
            userSession,
            friendsViewModel
        )
    }
}

@Composable
fun BodyContent(
    navController: NavController,
    userSessionManager: UserSessionManager,
    friendsViewModel: FriendsViewModel
){
    val onClickAction = {
        navController.navigate("friendsScreen")
    }
    val datesWithExercise by userSessionManager.exerciseDates.collectAsState()
    LaunchedEffect(Unit) {
        friendsViewModel.friendsRequestCount(userSessionManager.getUserSession().token)
        userSessionManager.getLevel()
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(15.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier = Modifier.padding(end = 30.dp),
                text = "Bienvenido ${userSessionManager.getUserSession().name}",
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
                    style = TextStyle(
                        fontSize = 10.sp,
                    )
                )
            }
        }
        ExperienceBar(
            if(userSessionManager.getUserSession().userLevel.isNotEmpty()) userSessionManager.getUserSession().userLevel else "0",
            if(userSessionManager.getUserSession().progressLevel.isNotEmpty()) userSessionManager.getUserSession().progressLevel else "0",
            modifier = Modifier.padding(0.dp)
        )
        TopMenu(navController)
        MyCalendar(datesWithExercise)
        FriendRequest(friendsViewModel ,onClickAction)
    }
}

// Menú de opciones
@Composable
fun TopMenu(
    navController: NavController
) {
    Row {
        Box( // Botón para iniciar la rutina
            modifier = Modifier
                .padding(
                    vertical = 15.dp,
                )
                .shadow(
                    8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(16.dp))
                .width(170.dp)
                .height(250.dp)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    shape = RectangleShape
                )  // Color de fondo
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Empieza tu rutina diaria...",
                    style = TextStyle(
                        fontSize = 30.sp,
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(4f, 4f),
                            blurRadius = 8f
                        )
                    ),
                )
                Box(modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .fillMaxSize()
                    .clickable {
                        navController.navigate("exerciseListScreen")
                    }) {
                    val img = painterResource(R.drawable.playicon)
                    Image(
                        painter = img,
                        contentDescription = "startImage",
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                // Botón para ver el ranking
                modifier = Modifier
                    .padding(vertical = 15.dp, horizontal = 15.dp)
                    .shadow(
                        8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape),
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
                )
            }
            Row(
                // Botón para ver el perfil
                modifier = Modifier
                    .padding(vertical = 15.dp, horizontal = 15.dp)
                    .shadow(
                        8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
                    .clickable { navController.navigate("profileScreen") },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "profile Details",
                    modifier = Modifier
                        .size(55.dp)
                )
                Text(
                    text = "Mi Perfil",
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                )
            }
        }
    }
}

// Calendario de los días donde se realizó la rutina
@Composable
fun MyCalendar(
    datesWithExercise: List<LocalDate>
) {
    Column( // CALENDAR TO SHOW THE DAY TO DAY PROGRESS
        modifier = Modifier
            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
    ) {
        Text(
            text = "SIGUE TU DÍA A DÍA...",
            style = TextStyle(
                fontSize = 20.sp,
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
            modifier = Modifier.padding(top = 15.dp, start = 15.dp)
        )
        ExerciseCalendar(datesWithExercise)
    }
}

@Composable
fun FriendRequest(
    friendsViewModel: FriendsViewModel,
    onClickAction: () -> Unit
){
    if (friendsViewModel.user.value.friendRequestCount!= 0) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = 185.dp, y = 15.dp)
                .clip(RoundedCornerShape(200.dp))
                .background(Color.Red)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friendsViewModel.user.value.friendRequestCount.toString(),
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }else{
        Spacer(modifier = Modifier.size(15.dp))
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClickAction() }
            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .width(200.dp)
            .height(200.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = RectangleShape
            )
            .padding(bottom = 15.dp),
    ){
        Text(
            text = "SOLICITUDES DE AMISTAD",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(15.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.agregar_usuario),
            contentDescription = "agregar usuario",
            modifier = Modifier
                .size(70.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                MaterialTheme.colorScheme.primary
            )
        )
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

@Composable
fun ExerciseCalendar(datesWithExercise: List<LocalDate>) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier.padding(10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Calendario mensual
        MonthCalendar(
            month = currentMonth.value,
            onMonthChange = { newMonth -> currentMonth.value = newMonth },
            datesWithExercise = datesWithExercise
        )
    }
}

@Composable
fun MonthCalendar(
    month: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    datesWithExercise: List<LocalDate>
) {
    // Cabecera para cambiar de mes
    Row(
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = month.month.name, style = MaterialTheme.typography.titleMedium)
            Text(text = month.year.toString(), style = MaterialTheme.typography.titleMedium)
        }
        IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
        }
    }

    // Grid del calendario
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        contentPadding = PaddingValues(15.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Espaciado entre columnas
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre filas
    ) {
        items(month.lengthOfMonth()) { day ->
            val date = LocalDate.of(month.year, month.month, day + 1)
            val isExerciseDay = datesWithExercise.contains(date)
            Box(
                modifier = Modifier
                    .aspectRatio(1f) // Cuadrado perfecto
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isExerciseDay) if (darkTheme.value) Color.White else BlueGreen else Color.Transparent,
                    )
                    .padding(5.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = "${day + 1}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isExerciseDay) if (darkTheme.value) Color.Black else Color.White else Color.Gray
                )
            }
        }
    }
}