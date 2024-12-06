package com.example.trackingfitness.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackingfitness.conection.LoginRequestUser
import com.example.trackingfitness.conection.LoginResponseUser
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val apiService: UserService = RetrofitInstance.api

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // Variables para el email y la contraseña

    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    // Variables para el mensaje de error

    var loginSuccess by mutableStateOf(false)
    var loginError by mutableStateOf(false)
    var loginUnverified by mutableStateOf(false)
    var loginMessage by mutableStateOf("")

    // Variables para el éxito y el error de inicio de sesión

    var accessToken: String? = null
    // Variable para el token de acceso

    private val _loginState = MutableStateFlow<Result<LoginResponseUser>?>(null)
    val loginState: StateFlow<Result<LoginResponseUser>?> get() = _loginState

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    // Funciones para actualizar los valores de las variables

    private fun validateEmail(): String? {
        return when {
            email.isEmpty() -> "Este campo no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "El formato de correo electrónico no es válido"

            else -> null
        }
    }

    private fun validatePassword(): String? {
        return when {
            password.isEmpty() -> "Este campo no puede estar vacío"
            password.length < 8 -> "La contraseña debe contener al menos 8 caracteres"
            else -> null
        }
    }
    // Validaciones de los campos

    private fun updateEmailError(error: String?) {
        emailError = error
    }

    private fun updatePasswordError(error: String?) {
        passwordError = error
    }

    fun validateAndUpdate(): Boolean{
        val emailValidationError = validateEmail()
        updateEmailError(emailValidationError)
        val passwordValidationError = validatePassword()
        updatePasswordError(passwordValidationError)
        return emailValidationError == null && passwordValidationError == null
    }

    // Actualización de los errores

    fun loginUser() {
        viewModelScope.launch {
            val request = LoginRequestUser(email, password)
            try {
                val response = apiService.loginUser(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        if (body.message!=null) {
                            Log.d("Login", "Login failed: ${body.message}")
                            loginMessage = body.message
                            loginUnverified = true
                        } else {
                            Log.d("Login", "Login successful")
                            loginSuccess = true
                            accessToken = body.access_token
                            Log.d("Login", "Access Token: $body")
                        }
                    }
                } else {
                    loginError = true
                    loginMessage = response.message()
                    Log.e("Login", "Error: ${response.message()}")
                }
                clearFields()
            } catch (e: Exception) {
                loginError = true
                Log.e("Login", "Error: ${e.localizedMessage}")
            }
        }
    }

    private fun clearFields() {
        email = ""
        password = ""
    } // LIMPIAR CAMPOS DE TEXTO
}