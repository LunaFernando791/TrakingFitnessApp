package com.example.trackingfitness.screens
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackingfitness.activity.BackButton
import com.example.trackingfitness.ui.theme.BlueGreen
import com.example.trackingfitness.viewModel.FriendInformation
import com.example.trackingfitness.viewModel.FriendRequest
import com.example.trackingfitness.viewModel.FriendsViewModel
import com.example.trackingfitness.viewModel.UserSessionManager

@Composable
fun FriendsScreen(
    navController: NavController,
    darkTheme:  Boolean?,
    userSession: UserSessionManager,
    friendsViewModel: FriendsViewModel
){
    Surface {

        FriendsBodyContent(
            userSessionManager = userSession,
            darkTheme = darkTheme,
            navController = navController,
            friendsViewModel = friendsViewModel
        )
    }
}

@Composable
fun FriendsBodyContent(
    userSessionManager: UserSessionManager,
    darkTheme: Boolean?,
    navController: NavController,
    friendsViewModel: FriendsViewModel
){
    LaunchedEffect(Unit) {
        friendsViewModel.showFriend(userSessionManager.getUserSession().token)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        BackButton(
            navController,
            "homeScreen",
            Modifier
                .padding(end = 275.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            item {
                val userFriends = friendsViewModel.user
                Column(
                    modifier = Modifier
                        .imePadding()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pending Friend Requests",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    when {
                        userFriends.value.friendRequestCount == 0 -> { // Cuando no hay solicitudes pendientes
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text =
                                "Don't have any friend requests.",
                            )
                        }
                        else -> { // Cuando hay solicitudes pendientes
                            val friendRequestList = userFriends.value.friendRequests.values.toList()
                            for (request in friendRequestList) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    FriendRequestCard(request, userSessionManager, friendsViewModel)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Friends",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    when {
                        userFriends.value.friends.isEmpty() -> { // Cuando no hay amigos
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "You don't have any friends.",
                                modifier = Modifier.fillMaxSize(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        else -> { // Cuando hay amigos disponibles
                            val friendsList = userFriends.value.friends.values.toList()
                            SearchableFriendList(friendsList, darkTheme,true,navController, friendsViewModel,"")
                        }
                    }
                    Text(
                        text = "Available Users",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    when {
                        userFriends.value.availableUsers?.isEmpty() == true -> { // Cuando no hay amigos
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "There are no available users.",
                                modifier = Modifier.fillMaxSize(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        else -> { // Cuando hay usuarios disponibles
                            val usersAvailables = userFriends.value.availableUsers?.values?.toList()
                            if (usersAvailables != null) {
                                SearchableFriendList(usersAvailables,
                                    darkTheme,
                                    false ,
                                    navController,
                                    friendsViewModel,
                                    userSessionManager.getUserSession().token)
                            }
                        }
                    }
                }
            }
        }
        if(friendsViewModel.success.value == "Request sent."){
            Toast.makeText(
                LocalContext.current,
                friendsViewModel.success.value,
                Toast.LENGTH_SHORT
            ).show()
            friendsViewModel.success.value = ""
        }
    }
}

@Composable
fun FriendRequestCard(
    request: FriendRequest,
    userSessionManager: UserSessionManager,
    friendsViewModel: FriendsViewModel
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .height(120.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = RectangleShape
            )
            .padding(5.dp),
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(0.35f)
        ){
            Spacer(modifier = Modifier.height(5.dp))
            Image(
                painter = rememberAsyncImagePainter(request.icon_url),
                contentDescription = "Friend Icon",
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape = CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = request.sender_username,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        friendsViewModel.acceptFriendRequest(
                            userSessionManager.getUserSession().token,
                            request.id
                        )
                    },
                    modifier = Modifier
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueGreen
                    )
                ) {
                    Text(
                        text = "Accept",
                        color = Color.White
                    )
                }
                Button(
                    onClick = {
                        friendsViewModel.declineFriendRequest(
                            userSessionManager.getUserSession().token,
                            request.id
                        )
                    },
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(
                        text = "Decline",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SearchableFriendList(
    friends: List<FriendInformation>,
    darkTheme: Boolean?,
    areFriends: Boolean,
    navController: NavController,
    friendsViewModel: FriendsViewModel,
    userToken: String
){
    var searchQuery by remember { mutableStateOf("") }
    val filteredFriends by rememberUpdatedState(
        friends.filter { it.username.contains(searchQuery, ignoreCase = true) }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { if(!areFriends) Text("Search user") else Text("Search friend") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = if (darkTheme==true) MaterialTheme.colorScheme.primary else BlueGreen) },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = if (darkTheme==true) MaterialTheme.colorScheme.primary else BlueGreen,
                unfocusedIndicatorColor = if (darkTheme==true) MaterialTheme.colorScheme.primary else BlueGreen,
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp
            ),
            shape = RoundedCornerShape(30.dp),
        )
        if (filteredFriends.isEmpty()) {
            Text(
                text = "No friends found.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(30.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredFriends) { friend ->
                    FriendProfileCard(
                        friend = friend,
                        isFriend = areFriends,
                        navController = navController,
                        friendsViewModel,
                        userToken
                    )
                }
            }
        }
    }
}

@Composable
fun FriendProfileCard(
    friend: FriendInformation,
    isFriend: Boolean,
    navController: NavController,
    friendsViewModel: FriendsViewModel,
    userToken: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .shadow(
                8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .width(150.dp)
            .height(200.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = RectangleShape
            )
            .clickable {
                if(isFriend)
                    navController.navigate("friendProfileScreen/${friend.username}")
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(friend.icon_url),
            contentDescription = "Friend Icon",
            alignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(shape = CircleShape)
        )
        Text(
            text = friend.username,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        if(!isFriend){
            Button(
                onClick = {
                    friendsViewModel.sendFriendRequest(userToken, friend.id)
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Add",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}


