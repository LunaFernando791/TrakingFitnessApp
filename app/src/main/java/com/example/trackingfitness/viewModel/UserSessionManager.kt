package com.example.trackingfitness.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.launch

class UserSessionManager(application: Context) : AndroidViewModel(application as Application) {
    private val apiService: UserService = RetrofitInstance.api
    fun saveUserSession(
        token: String,
        name: String,
        lastname: String,
        age: String,
        height: String,
        weight: String,
        gender: String,
        email: String,
        username: String,
        experienceLevel: String
    ) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("user_session",
            Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("token", token)
            putString("name", name)
            putString("lastname", lastname)
            putString("age", age)
            putString("height", height)
            putString("weight", weight)
            putString("gender", gender)
            putString("email", email)
            putString("username", username)
            putString("experienceLevel", experienceLevel)
            apply()
        }
    }

    fun getUserSession(): User {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("user_session",
            Context.MODE_PRIVATE)
        return User(
            name = sharedPreferences.getString("name", "")!!,
            token = sharedPreferences.getString("token", "")!!,
            lastname = sharedPreferences.getString("lastname", "")!!,
            age = sharedPreferences.getString("age", "")!!,
            height = sharedPreferences.getString("height", "")!!,
            weight = sharedPreferences.getString("weight", "")!!,
            gender = sharedPreferences.getString("gender", "")!!,
            email = sharedPreferences.getString("email", "")!!,
            username = sharedPreferences.getString("username", "")!!,
            experienceLevel = sharedPreferences.getString("experienceLevel", "")!!
        )
    }
    fun isUserLoggedIn(): Boolean {
        return getUserSession().token.isNotEmpty()
    }

    fun logoutUser() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )
        viewModelScope.launch {
            try {
                val response = apiService.logout("Bearer ${getUserSession().token}")
                if (response.isSuccessful) {
                    Log.d("Logout", "Logout success: ${response.message()}")
                    sharedPreferences.edit().clear().apply() // Limpiar aquí
                } else {
                    Log.e("Logout", "Logout failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Logout", "Error: ${e.localizedMessage}")
            }
        }
    }
}

data class User(
    val token: String,
    val name: String,
    val lastname: String,
    val age: String,
    val height: String,
    val weight: String,
    val gender: String,
    val email: String,
    val username: String,
    val experienceLevel: String
)