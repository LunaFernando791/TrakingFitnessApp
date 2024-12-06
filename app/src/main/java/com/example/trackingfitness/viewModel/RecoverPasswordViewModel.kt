package com.example.trackingfitness.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.ForgotPasswordRequest
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.launch

class RecoverPasswordViewModel: ViewModel() {
    private val apiService: UserService = RetrofitInstance.api

    var email by mutableStateOf("")
    var emailError by mutableStateOf<String?>(null)

    fun updateEmail(email: String) {
        this.email = email
    }
    // Funciones para actualizar los valores de las variables
    private fun updateEmailError(error: String?) {
        emailError = error
    }

    fun validateAndUpdate(): Boolean {
        val emailValidationError = validateEmail()
        updateEmailError(emailValidationError)
        return emailValidationError == null

    }

    private fun validateEmail(): String? {
        return when {
            email.isEmpty() -> "Este campo no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "El formato de correo electrónico no es válido"

            else -> null
        }
    }

    fun forgetPassword() {
        viewModelScope.launch {
            val request = ForgotPasswordRequest(email)
            try {
                val response = apiService.forgotPassword(request)
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    Log.d("Message", response.body()?.message.toString())
                }
                else{
                    Log.e("Error", response.body().toString())
                }
            }catch (e: Exception) {
                e.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }

}