package com.example.trackingfitness.viewModel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.launch

data class Medal(
    val id: Int,
    val type: Int,
    val unlock_request: Int,
    val description: String,
    val image_path: String,
    var unLocked: Boolean = false
){
    fun unLockMedal(){
        unLocked = true
    }
    fun showMedal(): String {
        return if (unLocked) {
            "http://192.168.1.7:8000/storage/$image_path"
        } else {
            "http://192.168.1.7:8000/storage/medals/locked.png"
        }
    }
}

class MedalViewModel(application: Application): AndroidViewModel(application) {
    private val apiService: UserService = RetrofitInstance.api
    private val _allMedals = mutableStateOf(emptyList<Medal>())
    var allMedals: State<List<Medal>> = _allMedals
    fun getAllMedals(){
        viewModelScope.launch{
            try{
                val response = apiService.getMedals()
                if(response.isSuccessful){
                    val body = response.body()
                    body?.medals?.forEach { _ ->
                        _allMedals.value = body.medals
                    }
                }else{
                    Log.e("GetMedals", "Get medals failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GetMedals", "Error: ${e.localizedMessage}")
            }
        }
    }

    init {
        getAllMedals()
    }
}