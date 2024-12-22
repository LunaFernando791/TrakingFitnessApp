package com.example.trackingfitness.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UpdateEmailRequest
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

class UserSessionManager(application: Context) : AndroidViewModel(application as Application) {
    private val apiService: UserService = RetrofitInstance.api
    var email by mutableStateOf("")
    private var emailError by mutableStateOf<String?>(null)
    var unLoginOk by mutableStateOf(false)

    fun changeEmailValue(newEmail: String) {
        this.email = newEmail
    }

    private fun validateEmail(): String? {
        return when {
            email.isEmpty() -> "Este campo no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "El formato de correo electrónico no es válido"
            else -> null
        }
    }
    private fun updateEmailError(error: String?) {
        emailError = error
    }
    fun obtenerEmailError(): String? {
        return emailError
    }

    fun validateAndUpdate(): Boolean{
        val emailValidationError = validateEmail()
        updateEmailError(emailValidationError)
        return emailValidationError == null
    }

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

    suspend fun isUserLoggedIn(): Boolean {
        return try {
            val sharedPreferences = getApplication<Application>().getSharedPreferences(
                "user_session",
                Context.MODE_PRIVATE
            )
            if (sharedPreferences.getString("token", "")!!.isEmpty()) {
                return false
            }
            else {
                val response =
                    apiService.validateToken("Bearer ${sharedPreferences.getString("token", "")}")
                response.isSuccessful && (response.body()?.valid ?: false)
            }
        } catch (e: Exception) {
            false
        }
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
    fun deleteUserAccount() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )
        viewModelScope.launch {
            try {
                val response = apiService.deleteAccount("Bearer ${getUserSession().token}")
                if (response.isSuccessful) {
                    Log.d("DeleteAccount", "Delete account")
                    sharedPreferences.edit().clear().apply() // Limpiar aquí
                } else {
                    Log.e("DeleteAccount", "Delete account failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DeleteAccount", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun updateEmail(newEmail: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )
        viewModelScope.launch {
            try {
                val response = apiService.updateEmail("Bearer ${getUserSession().token}", UpdateEmailRequest(newEmail))
                Log.d("UpdateEmail", "Response: $email")
                if (response.isSuccessful) {
                    Log.d("UpdateEmail", "Update email success: ${response.message()}")
                    sharedPreferences.edit().putString("email", newEmail).apply()
                } else {
                    Log.e("UpdateEmail", "Update email failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateEmail", "Error: ${e.localizedMessage}")
                Log.e("UpdateEmail", "Error: ${e.stackTrace}")
                Log.e("UpdateEmail", "Error: ${e.cause}")
                Log.e("UpdateEmail", "Error: ${e.suppressed}")
            }
        }
    }

    private suspend fun getImageProfile(): Bitmap? {
        return try {
            val response = apiService.getIcon("Bearer ${getUserSession().token}", "8.png")
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.byteStream()?.let { inputStream ->
                    BitmapFactory.decodeStream(inputStream) ?: run {
                        Log.e("GetIcon", "No se pudo decodificar la imagen")
                        null
                    }
                }
            } else {
                Log.e("GetIcon", "Error al obtener imagen: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("GetIcon", "Excepción al obtener imagen: ${e.localizedMessage}")
            null
        }
    }

    private val _profileImage = MutableLiveData<Bitmap?>()
    val profileImage: LiveData<Bitmap?> = _profileImage

    fun fetchImageProfile() {
        viewModelScope.launch {
            val bitmap = getImageProfile()
            if (bitmap != null) {
                _profileImage.postValue(bitmap)
            } else {
                Log.e("FetchImageProfile", "No se pudo obtener la imagen de perfil")
                // Aquí podrías agregar un Toast o actualizar algún mensaje en la UI
            }
        }
    }

}

