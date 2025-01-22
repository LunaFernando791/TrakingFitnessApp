package com.example.trackingfitness.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.activity.ExperienceBar
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.viewModel.FriendsViewModel
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun FriendProfileScreen(
    friendUsername: String,
    navController: NavController,
    userSession: UserSessionManager,
    friendsViewModel: FriendsViewModel
){
    Surface {
        FriendProfileBodyContent(
            friendUsername = friendUsername,
            userSessionManager = userSession,
            navController = navController,
            friendsViewModel = friendsViewModel
        )
    }
}

@Composable
fun FriendProfileBodyContent(
    friendUsername: String,
    userSessionManager: UserSessionManager,
    navController: NavController,
    friendsViewModel: FriendsViewModel
){
    LaunchedEffect(friendUsername) {
        friendsViewModel.showFriendProfile(
            userSessionManager.getUserSession().token,
            friendUsername
        )
    }
    val friendProfile = friendsViewModel.friendProfile
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkTheme.value) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        BackButton(
            navController,
            "friendsScreen",
            Modifier
                .padding(end = 275.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Log.d("FriendProfile", "Friend url: ${friendProfile.value.icon_url}")
                Image(
                    painter = rememberAsyncImagePainter(friendProfile.value.icon_url),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    modifier = Modifier
                        .shadow(
                            8.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color.Black,
                            spotColor = Color.Black
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .width(200.dp)
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.secondary, shape = RectangleShape)
                        .wrapContentSize(Alignment.Center),
                    text = friendProfile.value.username,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(40.dp))
                ExperienceBar(
                    if(friendProfile.value.userLevel=="") "0" else friendProfile.value.userLevel,
                    if(friendProfile.value.experience_level=="") "0" else friendProfile.value.experience_level,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                DynamicInfoRow(
                    items = listOf(
                        "Nombre" to friendProfile.value.name,
                        "Apellido" to friendProfile.value.last_name,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Edad" to friendProfile.value.age,
                        "Altura" to friendProfile.value.height,
                        "Peso" to friendProfile.value.weight,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Genero" to friendProfile.value.gender,
                    )
                )
                Spacer(modifier = Modifier.height(30.dp))
                //PENDIENTE, CALENDARIO Y MEDALLAS.
            }
        }
    }
}