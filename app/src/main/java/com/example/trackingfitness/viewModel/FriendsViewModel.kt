package com.example.trackingfitness.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ShowUser(
    var icon_url: String,
    var experience_level: String,
    var userLevel: String,
    var username: String,
    var name: String,
    var last_name: String,
    var age: String,
    var gender: String,
    var height: String,
    var weight: String,
    var exercise_dates: List<LocalDate>?,
    var userMedals: List<Int> = emptyList()
)

data class FriendInformation(
    val id: Int,
    val username: String,
    val icon_url: String,
)

data class FriendRequest(
    val id: Int,
    val sender_id: Int,
    val sender_username: String,
    val icon_url: String,
)

data class UserFriends(
    var friendRequestCount: Int,
    var friends: Map<String, FriendInformation>,
    var friendRequests: Map<Int, FriendRequest>,
    var availableUsers: Map<Int, FriendInformation>?,
)


class FriendsViewModel : ViewModel() {
    private val apiService: UserService = RetrofitInstance.api
    private var _user = mutableStateOf(UserFriends(
        friendRequestCount = 0,
        friends = emptyMap(),
        friendRequests = emptyMap(),
        availableUsers = emptyMap()
        )
    )
    val user: State<UserFriends> = _user

    private val _friendProfile = mutableStateOf(ShowUser(
        icon_url = "",
        username = "",
        name = "",
        last_name = "",
        age = "",
        gender = "",
        height = "",
        weight = "",
        experience_level = "",
        userLevel = "",
        exercise_dates = emptyList(),
        userMedals = emptyList()
        )
    )
    val friendProfile: State<ShowUser> = _friendProfile

    var success = mutableStateOf("")

    fun friendsRequestCount(userToken: String){
        viewModelScope.launch {
            try {
                val response = apiService.getFriendsRequest("Bearer $userToken")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("FriendsRequest", "Friends request: ${body.friendsRequestCount}")
                        _user.value = user.value.copy(
                            friendRequestCount = body.friendsRequestCount
                        )
                    }
                }
                else {
                    Log.e("FriendsRequest", "Get friends request failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FriendsRequest", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun acceptFriendRequest(userToken: String, friendId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.acceptFriendRequest("Bearer $userToken", friendId)
                if (response.isSuccessful) {
                    Log.d("AcceptFriendRequest", "Friend request accepted: ${response.body()}")
                    _user.value = user.value.copy(
                        friendRequests = user.value.friendRequests.filter { it.key != friendId }
                    )
                } else {
                    Log.e("AcceptFriendRequest", "Accept friend request failed: ${response.errorBody()?.string()}")
                }
            }catch (e: Exception) {
                Log.e("Friends", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun declineFriendRequest(userToken: String, friendId: Int) {
        viewModelScope.launch {
            try{
                val response = apiService.declineFriendRequest("Bearer $userToken", friendId)
                if (response.isSuccessful) {
                    Log.d("DeclineFriendRequest", "Friend request declined: ${response.body()}")
                    _user.value = user.value.copy(
                        friendRequests = user.value.friendRequests.filter { it.key != friendId }
                    )
                } else {
                    Log.e("DeclineFriendRequest", "Decline friend request failed: ${response.errorBody()?.string()}")
                }
            }catch (e: Exception) {
                Log.e("Friends", "Error: ${e.localizedMessage}")
            }
        }
    }


    fun showFriend(userToken: String){
        viewModelScope.launch {
            try {
                val response = apiService.getFriends("Bearer $userToken")
                Log.d("Friends", "Friends: $response")
                if (response.isSuccessful) {
                    val body = response.body()
                    //Lista de solicitudes de amistad
                    val friendRequestsMap = body?.friendRequests?.associateBy { it.id }
                        ?.mapValues { (_, value) ->
                            FriendRequest(
                                id = value.id,
                                sender_id = value.sender_id,
                                sender_username = value.sender_username,
                                icon_url = value.icon_url
                            )
                        }
                    if (friendRequestsMap != null) {
                        _user.value = user.value.copy(
                            friendRequests = friendRequestsMap
                        )
                    }
                    // Lista de amigos
                    val friendsMap = body?.friends?.mapValues { (_, value) ->
                        FriendInformation(
                            id = value.id,
                            username = value.username,
                            icon_url = value.icon_url
                        )
                    }
                    if (friendsMap != null) {
                        _user.value = user.value.copy(
                            friends = friendsMap
                        )
                    }
                    //Usuarios disponibles
                    val availableUsers = body?.availableUsers?.associateBy { it.id }
                        ?.mapValues{ (_, value) ->
                        FriendInformation(
                            id = value.id,
                            username = value.username,
                            icon_url = value.icon_url
                        )
                    }
                    Log.d("Friends", "Available users: $availableUsers")
                    if (availableUsers != null) {
                        _user.value = user.value.copy(
                            availableUsers = availableUsers
                        )
                    }
                } else {
                    Log.e("Friends", "Get friends failed: ${response.errorBody()?.string()}")
                }
            }catch (e: Exception) {
                Log.e("Friends", "Error: ${e.localizedMessage}")
            }
        }
    }

    private fun calculateUserLevel(experienceLevel: Int): Pair<Int, Int> {
        val limit = 2000
        val level = experienceLevel / limit
        val currentProgressLevel = experienceLevel - (level * limit)
        val progressLevel = if (currentProgressLevel <= 2000) {
            currentProgressLevel
        } else {
            0
        }
        return Pair(level, progressLevel)
    }

    fun showFriendProfile(userToken: String, username: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getFriendUser("Bearer $userToken", username)
                if (response.isSuccessful) {
                    val body = response.body()
                    val (level, progressLevel) = calculateUserLevel(body?.score ?: 0)
                    val dateFormatter = DateTimeFormatter.ISO_DATE
                    _friendProfile.value = friendProfile.value.copy(
                        icon_url = body?.icon_number ?: "",
                        experience_level = progressLevel.toString(),
                        userLevel = level.toString(),
                        username = body?.user?.username ?: "",
                        name = body?.user?.personal_name ?: "",
                        last_name = body?.user?.last_name ?: "",
                        age = body?.user?.age.toString(),
                        gender = body?.user?.gender_id.toString(),
                        height = body?.user?.height.toString(),
                        weight = body?.user?.weight.toString(),
                        exercise_dates = body?.exercise_dates?.map { LocalDate.parse(it, dateFormatter) },
                        userMedals = body?.userMedals ?: emptyList()
                    )
                    Log.d("FriendProfile", "Friend profile: ${friendProfile.value}")
                } else {
                    Log.e("FriendProfile", "Get friend profile failed: ${response.errorBody()?.string()}")
                    }
            }catch (e: Exception) {
                Log.e("Friends", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun sendFriendRequest(userToken: String, friendId: Int){
        viewModelScope.launch {
            try {
                val response = apiService.sendFriendRequest("Bearer $userToken", friendId)
                val body = response.body()
                if (response.isSuccessful) {
                    _user.value = user.value.copy(
                        availableUsers = user.value.availableUsers?.filter { it.key != friendId }
                    )
                    if (body != null) {
                        Log.d("SendFriendRequest", "Friend request sent: ${body.success}")
                        success.value = body.success
                    }
                } else {
                    Log.e(
                        "SendFriendRequest",
                        "Send friend request failed: ${response.errorBody()?.string()}"
                    )
                }
            }catch (e: Exception) {
                Log.e("Friends", "Error: ${e.localizedMessage}")
            }
        }
    }
}