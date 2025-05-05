//package com.example.trackingfitness.viewModel
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.trackingfitness.conection.GuessUser
//import com.example.trackingfitness.conection.MinigameScoreRequest
//import com.example.trackingfitness.conection.RetrofitInstance
//import com.example.trackingfitness.conection.UserService
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//
//
//class MiniGamesViewModel(application: Application) : AndroidViewModel(application) {
//    private val apiService: UserService = RetrofitInstance.api
//    var _globalRanking = MutableStateFlow<List<GuessUser>>(emptyList())
//    var globalRanking: StateFlow<List<GuessUser>> = _globalRanking
//    fun getGlobalRanking(userToken: String){
//        viewModelScope.launch {
//            val response = apiService.getRankingMiniGames("Bearer $userToken")
//            try {
//                if (response.isSuccessful) {
//                    _globalRanking.value = response.body()!!.globalRanking
//                    Log.d("globalRanking", _globalRanking.value.toString())
//                } else {
//                    Log.e("Error", response.message())
//                }
//            }catch (e: Exception){
//                Log.e("Error", e.toString())
//            }
//        }
//    }
//
//    fun updateMinigameScore(
//        token: String,
//        telephone: String,
//        minigameId: Int,
//        score: Int,
//        alias: String? = null
//    ) {
//        viewModelScope.launch {
//            try {
//                val request = MinigameScoreRequest(
//                    telephone = telephone,
//                    minigame_id = minigameId,
//                    score = score,
//                    alias = alias
//                )
//                val response = apiService.updateMinigameScore("Bearer $token", request)
//                if (response.isSuccessful) {
//                    Log.d("MinigameScore", "Score updated/created successfully")
//                } else {
//                    Log.e("MinigameScore", "Error updating score: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("MinigameScore", "Exception: ${e.localizedMessage}")
//            }
//        }
//    }
//}
package com.example.trackingfitness.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.GuessUser
import com.example.trackingfitness.conection.MinigameScoreRequest
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MiniGamesViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: UserService = RetrofitInstance.api

    private val _globalRanking = MutableStateFlow<List<GuessUser>>(emptyList())
    val globalRanking: StateFlow<List<GuessUser>> = _globalRanking

    fun getGlobalRanking(userToken: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRankingMiniGames("Bearer $userToken")
                if (response.isSuccessful) {
                    _globalRanking.value = response.body()?.globalRanking ?: emptyList()
                    Log.d("globalRanking", _globalRanking.value.toString())
                } else {
                    Log.e("Error", response.message())
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }
    }

    fun updateMinigameScore(
        token: String,
        telephone: String,
        minigameId: Int,
        score: Int,
        alias: String? = null
    ) {
        viewModelScope.launch {
            try {
                val request = MinigameScoreRequest(
                    telephone = telephone,
                    minigame_id = minigameId,
                    score = score,
                    alias = alias
                )
                val response = apiService.updateMinigameScore("Bearer $token", request)
                if (response.isSuccessful) {
                    Log.d("MinigameScore", "Score updated/created successfully")
                } else {
                    Log.e("MinigameScore", "Error updating score: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MinigameScore", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun clearRanking() {
        _globalRanking.value = emptyList()
    }
}

