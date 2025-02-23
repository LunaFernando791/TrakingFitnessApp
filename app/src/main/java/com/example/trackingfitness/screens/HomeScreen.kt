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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PlayCircleOutline
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
import androidx.compose.ui.graphics.ColorFilter
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
    LaunchedEffect(Unit) {
        friendsViewModel.friendsRequestCount(userSessionManager.getUserSession().token)
        userSessionManager.getLevel()
        userSessionManager.getDatesWhenUserExercised()
    }
    val datesWithExercise by userSessionManager.exerciseDates.collectAsState()
    val user by userSessionManager.user.collectAsState()
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
                text = "Welcome ${user.name}",
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
                    text = if (darkTheme.value) "Dark mode" else "Light mode",
                    style = TextStyle(
                        fontSize = 10.sp,
                    )
                )
            }
        }
        ExperienceBar(
            user.userLevel.ifEmpty { "0" },
            user.progressLevel.ifEmpty { "0" },
            modifier = Modifier.padding(0.dp)
        )
        TopMenu(navController)
        MyCalendar(datesWithExercise)
        Row(
            modifier = Modifier
                .padding(top = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            MedalBottom {navController.navigate("medalsScreen")}
            FriendRequest(friendsViewModel){navController.navigate("friendsScreen")}
        }
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
                    text = "Start your routine...",
                    style = TextStyle(
                        fontSize = 30.sp,
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(4f, 4f),
                            blurRadius = 8f
                        )
                    ),
                )
                Box(
                    modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .background(MaterialTheme.colorScheme.onSecondaryContainer)
                    .width(180.dp)
                    .height(120.dp)
                    .clickable {
                        navController.navigate("camera_screen")
                    },
                    contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = "profile Details",
                        modifier = Modifier
                            .size(100.dp)
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
                    .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
                    .clickable { navController.navigate("rankingScreen") },
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
                    text = "Monthly Ranking",
                    fontSize = 20.sp,
                )
            }
            Row(
                // Botón para ver el perfil
                modifier = Modifier
                    .padding( horizontal = 15.dp)
                    .shadow(
                        8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .height(120.dp)
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
                    text = "Profile",
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
            text = "DAY TO DAY...",
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
) {
    // Contenedor padre que permite que el contador rojo flote
    Box(
        modifier = Modifier
            .wrapContentSize(), // Ajusta el tamaño al contenido
        contentAlignment = Alignment.Center
    ) {
        // Contenedor principal (el cuadro de "Solicitudes de amistad")
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .wrapContentSize()
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
        ) {
            // Contenido principal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "MY FRIENDS",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(15.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.agregar_usuario),
                    contentDescription = "agregar usuario",
                    modifier = Modifier
                        .size(60.dp)
                        .wrapContentSize(),
                    colorFilter = ColorFilter.tint(
                        MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Contador rojo flotante (fuera del contenedor principal)
        if(friendsViewModel.user.value.friendRequestCount != 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd) // Alinea en la esquina superior derecha del contenedor padre
                    .offset(x = 10.dp, y = (-10).dp) // Ajusta la posición para que flote
                    .size(30.dp)
                    .clip(RoundedCornerShape(200.dp))
                    .background(Color.Red)
                    .zIndex(1f), // Asegura que esté por encima de otros elementos
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friendsViewModel.user.value.friendRequestCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
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

@Composable
fun MedalBottom(
    onClickAction: () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .wrapContentSize()
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
            .clickable { onClickAction() }
    ) {
        Text(text = "MY MEDALS",modifier = Modifier.wrapContentSize())
        Icon(imageVector = Icons.Default.Star, contentDescription = "medals",modifier = Modifier.wrapContentSize().size(60.dp))
    }
}

@Composable
fun ExerciseCalendar(datesWithExercise: List<LocalDate>) {
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .padding(10.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
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
                        if (isExerciseDay) if (darkTheme.value) Color.White else MaterialTheme.colorScheme.onSecondaryContainer else Color.Transparent,
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