package com.example.trackingfitness.viewModel

import android.util.Log
import android.util.Patterns
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.User
import com.example.trackingfitness.conection.UserService
import com.example.trackingfitness.darkTheme
import com.example.trackingfitness.ui.theme.BlueGreen
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel : ViewModel() {
    private val apiService: UserService = RetrofitInstance.api //INSTANCIA PARA INTERACTUAR CON LA API

    // VARIABLES NECESARIAS PARA EL REGISTRO DEL USUARIO
    var name by mutableStateOf("")
    var lastname by mutableStateOf("")
    var age by mutableStateOf("")
    var height by mutableStateOf("")
    var weight by mutableStateOf("")
    var gender by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var username by mutableStateOf("")
    var experienceLevel by mutableStateOf("")
    var routineType by mutableStateOf("")
    var injuriesList by mutableStateOf(listOf<Int>())
        private set


    // VARIABLES PARA EL PROGRESO DE LA BARRA
    var progress by mutableFloatStateOf(0.0f)
        private set
    private val _progressColor = mutableStateOf(if (darkTheme.value) BlueGreen else Color.Gray)
    val progressColor: State<Color> get() = _progressColor
    private val _trackColor = mutableStateOf(Color.Gray)
    val trackColor: State<Color> get() = _trackColor


    var nameError by mutableStateOf<String?>(null)
    var lastnameError by mutableStateOf<String?>(null)
    var ageError by mutableStateOf<String?>(null)
    var heightError by mutableStateOf<String?>(null)
    var weightError by mutableStateOf<String?>(null)
    var genderError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    private var confirmPasswordError by mutableStateOf<String?>(null)
    var usernameError by mutableStateOf<String?>(null)
    var experienceLevelError by mutableStateOf<String?>(null)
    var routineTypeError by mutableStateOf<String?>(null)
    var registrationSuccess by mutableStateOf(false)
    private var registrationError by mutableStateOf("")
    var errorRegister by mutableStateOf(false)
    var emailErrors by mutableStateOf("")
    //VARIABLES PARA EL MANEJO DE LOS ERRORES DE LOS CAMPOS

    fun incrementProgress(stepIncrement: Float) {
        if (progress + stepIncrement <= 1.0f) {
            progress += stepIncrement
        }
    } //FUNCION PARA EL INCREMENTO DE LA BARRA

    fun decrementProgress(stepDecrement: Float) {
        if (progress - stepDecrement >= 0.0f) {
            progress -= stepDecrement
        }
    } //FUNCION PARA EL DECREMENTO DE LA BARRA


    fun updateName(name: String) {
        this.name = name
    }
    fun updateLastName(lastname: String) {
        this.lastname = lastname
    }
    fun updateAge(age: String) {
        this.age = age

    }
    fun updateHeight(height: String) {
        this.height = height
    }
    fun updateWeight(weight: String) {
        this.weight = weight
    }
    fun updateGender(gender: String) {
        this.gender = when (gender) {
            "Male" -> "1"
            "Female" -> "2"
            "Other" -> "3"
            else -> ""
        }
    }
    fun updateEmail(email: String) {
        this.email = email
    }
    fun updatePassword(password: String) {
        this.password = password
    }
    fun updateConfirmPassword(confirmPassword: String) {
        this.confirmPassword = confirmPassword
    }
    fun updateUsername(username: String) {
        this.username = username
    }
    fun updateExperienceLevel(experienceLevel: String) {
        this.experienceLevel = when(experienceLevel) {
            "Principiante" -> "1"
            "Intermedio" -> "2"
            "Avanzado" -> "3"
            else -> ""
        }
    }
    fun updateRoutineType(routineType: String) {
        this.routineType = when (routineType) {
            "Improve cardiovascular health" -> "1"
            "Strengthen muscles" -> "2"
            "Improve flexibility" -> "3"
            "Reduce stress" -> "4"
            "Weight control" -> "5"
            "Increase energy" -> "6"
            "Prevent diseases" -> "7"
            "Improve posture" -> "8"
            else -> ""
        }
    }
    fun updateInjuries(injuries: SnapshotStateList<Int>) {
        this.injuriesList = injuries
    }

    //FUNCIONES PARA ACTUALIZAR EL ESTADO DE LOS DATOS DEL USUARIO.


    private fun validateName(): String? {
            return when {
                name.isEmpty() -> "Este campo no puede estar vacío"
                name.length < 3 -> "El nombre debe contener al menos 3 caracteres"
                !name.all { it.isLetter() } -> "El nombre debe contener solo letras"
                else -> null
            }
        }

    private fun validateLastName(): String?{
        return when{
            lastname.isEmpty() -> "Este campo no puede estar vacío"
            lastname.length < 3 -> "El apellido debe contener al menos 3 caracteres"
            !lastname.all { it.isLetter() } -> "El apellido debe contener solo letras"
            else-> null
        }
    }
    private fun validateAge():String?{
        return when{
            age.isEmpty() -> "Este campo no puede estar vacío"
            !age.all { it.isDigit() } -> "La edad debe contener solo números"
            else-> null
        }
    }
    private fun validateHeight(): String?{
        return when{
            this.height.isEmpty() -> "Este campo no puede estar vacío"
            !this.height.all { it.isDigit() } -> "La altura debe contener solo números"
            else-> null
        }
    }
    private fun validateWeight(): String? {
        return when {
            this.weight.isEmpty() -> "Este campo no puede estar vacío"
            !this.weight.all { it.isDigit() } -> "El peso debe contener solo números"
            else -> null
        }
    }
    private fun validateGender(): String? {
        return when {
            this.gender.isEmpty() -> "Este campo no puede estar vacío"
            else -> null
        }
    }
    private fun validateEmail(): String?{
        return when{
            email.isEmpty() -> "Este campo no puede estar vacío"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "El formato de correo electrónico no es válido"
            else-> null
        }
    }
    private fun validatePassword(): String?{
        return when{
            password.isEmpty() -> "Este campo no puede estar vacío"
            password.length < 8 -> "La contraseña debe contener al menos 8 caracteres"
            else-> null
        }
    }
    private fun validateConfirmPassword(): String? {
        return when {
            confirmPassword.isEmpty() -> "Este campo no puede estar vacío"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }
    private fun validateUsername(): String? {
        return when{
            username.isEmpty() -> "Este campo no puede estar vacío"
            username.length < 8 -> "La contraseña debe contener al menos 8 caracteres"
            else-> null
        }
    }
    private fun validateExperienceLevel(): String? {
        return when {
            experienceLevel.isEmpty() -> "Este campo no puede estar vacío"
            else -> null
        }
    }
    private fun validateRoutineType(): String? {
        return when {
            routineType.isEmpty() -> "Este campo no puede estar vacío"
            else -> null
        }
    }


    // VALIDACIONES DE CADA UNO DE LOS CAMPOS.

    private fun updateNameError(error: String?) { nameError = error }
    private fun updateLastNameError(error: String?) { lastnameError = error }
    private fun updateAgeError(error: String?) { ageError = error }
    private fun updateHeightError(error: String?) { heightError = error }
    private fun updateWeightError(error: String?) { weightError = error }
    private fun updateGenderError(error: String?) { genderError = error }
    private fun updateEmailError(error: String?) { emailError = error }
    private fun updatePasswordError(error: String?) { passwordError = error }
    private fun updateConfirmPasswordError(error: String?) { confirmPasswordError = error }
    private fun updateUsernameError(error: String?) { usernameError = error }
    private fun updateExperienceLevelError(error: String?) { experienceLevelError = error }
    private fun updateRoutineTypeError(error: String?) { routineTypeError = error }
    // ACTUALIZACIÓN DEL ESTADO DE CADA UNO DE LOS ERRORES DE LOS CAMPOS.

    fun updateProgressRegister() : Boolean{
        nameError = validateName()
        lastnameError = validateLastName()
        ageError = validateAge()
        updateNameError(nameError)
        updateLastNameError(lastnameError)
        updateAgeError(ageError)
        if (nameError == null && lastnameError == null && ageError == null) {
            incrementProgress(0.25f)
            return true
        }else
            return false

    }
    fun updateProgressRegister2() : Boolean{
        heightError = validateHeight()
        weightError = validateWeight()
        genderError = validateGender()
        updateHeightError(heightError)
        updateWeightError(weightError)
        updateGenderError(genderError)
        if (heightError == null && weightError == null && genderError == null) {
            incrementProgress(0.25f)
            return true
        }else
            return false
    }
    fun updateProgressRegister3() : Boolean{
        emailError = validateEmail()
        passwordError = validatePassword()
        confirmPasswordError = validateConfirmPassword()
        updateEmailError(emailError)
        updatePasswordError(passwordError)
        updateConfirmPasswordError(confirmPasswordError)
        if (emailError == null && passwordError == null && confirmPasswordError == null) {
            incrementProgress(0.25f)
            return true
        }else
            return false
    }

    fun updateProgressRegister4() {
        usernameError = validateUsername()
        experienceLevelError = validateExperienceLevel()
        updateUsernameError(usernameError)
        updateExperienceLevelError(experienceLevelError)
        updateRoutineTypeError(routineTypeError)
        if (usernameError == null && experienceLevelError == null && routineTypeError == null) {
            formRegister()
        }
    }


    @OptIn(UnstableApi::class)
    private fun formRegister() {
        viewModelScope.launch {
            val userData = User( // CAST DE LOS DATOS A SU CORRECTO TIPO DE DATO PARA SU ENVÍO
                personal_name = name,
                last_name = lastname,
                age = age.toInt(),
                height = height.toFloat(),
                weight = weight.toFloat(),
                gender_id = gender.toInt(),
                email = email,
                password = password,
                username = username,
                experience_level_id = experienceLevel.toInt(),
                routine_type_id = routineType.toInt(),
                injuries = injuriesList
            )
            try {
                val response = apiService.register(userData) //SOLICITUD POST
                Log.d("Registration", "Response: $response")
                if (response.isSuccessful) {
                    registrationSuccess = true
                    clearFields()
                    progress = 0F
                    // Muestra un mensaje de éxito con un registro exitoso
                } else {
                    errorRegister = true
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = errorBody?.let { JSONObject(it) }
                    Log.e("Registration", "Error: $errorResponse")
                }
            } catch (e: Exception) {
                registrationError = "Network error: ${e.localizedMessage}"
                Log.e("Registration", "Network error: ${e.localizedMessage}")
                // Muestra un mensaje de error de red
            }
        }
    } // FUNCIÓN PARA ENVIAR LA SOLICITUD DE REGISTRO.

    private fun clearFields() {
        name = ""
        lastname = ""
        age = ""
        height = ""
        weight = ""
        gender = ""
        email = ""
        password = ""
        confirmPassword = ""
        username = ""
        experienceLevel = ""
        routineType = ""
        progress= 0F
    } // LIMPIAR CAMPOS DE TEXTO
    fun resetStates(){
        registrationSuccess = false
        errorRegister = false
    }




}