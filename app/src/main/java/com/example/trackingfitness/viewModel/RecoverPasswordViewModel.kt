package com.example.trackingfitness.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.ForgotPasswordRequest
import com.example.trackingfitness.conection.ResetPasswordRequest
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
    var newPassword by mutableStateOf("")
    var newPasswordError by mutableStateOf<String?>(null)
    var confirmPassword by mutableStateOf("")
    var confirmPasswordError by mutableStateOf<String?>(null)

    fun updateEmail(email: String) {
        this.email = email
    }
    fun updateOTP(otp: String) {
        this.otp = otp
    }
    fun updateNewPassword(newPassword: String) {
        this.newPassword = newPassword
    }
    fun updateConfirmPassword(confirmPassword: String) {
        this.confirmPassword = confirmPassword
    }
    private fun updateNewPasswordError(error: String?) {
        newPasswordError = error
    }
    private fun updateConfirmPasswordError(error: String?) {
        confirmPasswordError = error
    }
    private fun updateEmailError(error: String?) {
        emailError = error
    }


        // Funciones para actualizar los valores de las variables
        private fun validateNewPassword(): String? {
            return when {
                newPassword.isEmpty() -> "Este campo no puede estar vacío"
                newPassword.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
                else -> null
            }
        }

        private fun validateConfirmPassword(): String? {
            return when {
                confirmPassword.isEmpty() -> "Este campo no puede estar vacío"
                confirmPassword != newPassword -> "Las contraseñas no coinciden"
                else -> null
            }
        }

        private fun validateEmail(): String? {
            return when {
                email.isEmpty() -> "Este campo no puede estar vacío"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() -> "El formato de correo electrónico no es válido"

                else -> null
            }
        }
        fun validateAndUpdate(): Boolean {
            val emailValidationError = validateEmail()
            updateEmailError(emailValidationError)
            return emailValidationError == null
        }
        fun validateAndUpdateNewPassword(): Boolean {
            val newPasswordValidationError = validateNewPassword()
            updateNewPasswordError(newPasswordValidationError)
            return newPasswordValidationError == null
        }
        fun validateAndUpdateConfirmPassword(): Boolean {
            val confirmPasswordValidationError = validateConfirmPassword()
            updateConfirmPasswordError(confirmPasswordValidationError)
            return confirmPasswordValidationError == null
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
                    } else {
                        Log.e("Error", response.body().toString())
                        val error = response.errorBody()?.string()
                        Log.e("API Error", "Error: $error")
                    }
                } catch (e: Exception) {
                    e.localizedMessage?.let { Log.e("Error", it) }
                }
            }
        }
    fun createOTP() {
            viewModelScope.launch {
                val request = ValidateOTPRequest(email, otp)
                try {
                    val response = apiService.validateOTP(request)
                    Log.d("Response", response.toString())
                    if (response.isSuccessful) {
                        Log.d("Message: ", response.body()?.message.toString())
                        Log.d("Message: ", otp)
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("API Error", "Error: $error")
                        Log.e("Error", response.message().toString())
                        Log.e("Error", request.otp)
                        Log.e("Error", request.email)
                    }
                } catch (e: Exception) {
                    e.localizedMessage?.let { Log.e("Error", it) }
                }
            }
        }
    fun changePassword() {
        viewModelScope.launch {
            val request = ResetPasswordRequest(email, otp, newPassword)
            try {
                val response = apiService.resetPassword(request)
                Log.d("Response", response.toString())
                if (response.isSuccessful) {
                    Log.d("Message", response.body()?.message.toString())
                }
                else{
                    Log.e("Error", response.body()?.message.toString())
                    val error = response.errorBody()?.string()
                    Log.e("API Error", "Error: $error")
                    Log.e("Datos: ", request.email)
                    Log.e("Datos: ", request.otp)
                    Log.e("Datos: ", request.password)
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { Log.e("Error", it) }
            }
        }
    }
}