package com.example.trackingfitness.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.LockOrientationInThisScreen
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.activity.ExperienceBar
import com.example.trackingfitness.viewModel.FriendsViewModel
import com.example.trackingfitness.viewModel.Medal
import com.example.trackingfitness.viewModel.MedalViewModel
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun FriendProfileScreen(
    friendUsername: String,
    darkTheme: Boolean?,
    navController: NavController,
    userSession: UserSessionManager,
    friendsViewModel: FriendsViewModel
){
    LockOrientationInThisScreen()
    Surface {
        FriendProfileBodyContent(
            friendUsername = friendUsername,
            darkTheme = darkTheme,
            userSessionManager = userSession,
            navController = navController,
            friendsViewModel = friendsViewModel
        )
    }
}

@Composable
fun FriendProfileBodyContent(
    friendUsername: String,
    darkTheme: Boolean?,
    userSessionManager: UserSessionManager,
    navController: NavController,
    friendsViewModel: FriendsViewModel
){
    val medalViewModel: MedalViewModel = viewModel()
    LaunchedEffect(friendUsername) {
        friendsViewModel.showFriendProfile(
            userSessionManager.getUserSession().token,
            friendUsername
        )
        medalViewModel.getAllMedals()
    }
    val friendProfile = friendsViewModel.friendProfile
    val medalList = medalViewModel.allMedals
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
                    darkTheme,
                    if(friendProfile.value.userLevel=="") "0" else friendProfile.value.userLevel,
                    if(friendProfile.value.experience_level=="") "0" else friendProfile.value.experience_level,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                DynamicInfoRow(
                    items = listOf(
                        "Name" to friendProfile.value.name,
                        "Lastname" to friendProfile.value.last_name,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Age" to friendProfile.value.age,
                        "Height" to friendProfile.value.height,
                        "Weight" to friendProfile.value.weight,
                    )
                )
                DynamicInfoRow(
                    items = listOf(
                        "Gender" to friendProfile.value.gender,
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                MyCalendar(friendProfile.value.exercise_dates?: emptyList(), darkTheme)
                Spacer(modifier = Modifier.height(10.dp))
                medalList.value.forEach(
                    fun(medal){
                        friendProfile.value.userMedals.forEach(
                            fun(userMedal) {
                                if (medal.id == userMedal) {
                                    Log.d("FriendProfile", "Medal unlocked: ${medal.id}")
                                    medal.unLockMedal()
                                }
                            }
                        )
                    }
                )
                MedalList(medals = medalList.value)
                Spacer(modifier = Modifier.height(10.dp))

            }
        }
    }
}

@Composable
fun MedalList(
    medals: List<Medal>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Medals",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Gray,
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            ),
        )
        Spacer(modifier = Modifier.height(20.dp))
        medals.chunked(3).forEach { rowMedals ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                rowMedals.forEach { medal ->
                    Log.d("Medal", medal.showMedal())
                    MedalCard(
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                        ,medalUrl = medal.showMedal(), onClick = {})

                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .width(200.dp)
                .height(50.dp),
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text(text = "Delete friend")
        }
    }
}
