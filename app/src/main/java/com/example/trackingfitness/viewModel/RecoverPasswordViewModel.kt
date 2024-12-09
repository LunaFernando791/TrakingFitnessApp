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
import com.example.trackingfitness.conection.ValidateOTPRequest
import kotlinx.coroutines.launch

class RecoverPasswordViewModel: ViewModel() {
    private val apiService: UserService = RetrofitInstance.api

    var email by mutableStateOf("")
        private set
    var emailError by mutableStateOf<String?>(null)
    var otpError by mutableStateOf<String?>(null)
    var otp by mutableStateOf("")

    fun updateOTP(otp: String) {
        this.otp = otp
    }

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

    private fun validateCorrectlyOTP(): String? {
        return when {
            otp.isEmpty() -> "Este campo no puede estar vacío"
            otp.length != 6 -> "El código debe tener 6 dígitos"
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
                    Log.d("Message", request.email)
                }
                else{
                    Log.e("Error", response.body().toString())
                }
            }catch (e: Exception) {
                e.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }



    fun validateOTP(){
        viewModelScope.launch {
            val request = ValidateOTPRequest(email, otp)
            try {
                val response = apiService.validateOTP(request)
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    Log.d("Message", response.body()?.message.toString())
                }
                else{
                    Log.e("Error", response.body().toString())
                    Log.e("Error", response.message().toString())
                    Log.e("Error", request.otp)
                    Log.e("Error", request.email)
                }
            }catch (e: Exception) {
                e.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }

}