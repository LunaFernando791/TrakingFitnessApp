package com.example.trackingfitness.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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
import com.example.trackingfitness.conection.Exercise
import com.example.trackingfitness.conection.MyExerciseResponse
import com.example.trackingfitness.conection.RankingResponse
import com.example.trackingfitness.conection.RetrofitInstance
import com.example.trackingfitness.conection.RoutineResponse
import com.example.trackingfitness.conection.UpdateEmailRequest
import com.example.trackingfitness.conection.UpdateIconRequest
import com.example.trackingfitness.conection.UpdatePasswordRequest
import com.example.trackingfitness.conection.UserRequest
import com.example.trackingfitness.conection.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.State

data class User(
    val token: String,
    var name: String,
    var lastname: String,
    var age: String,
    var height: String,
    var weight: String,
    var gender: String,
    var email: String,
    var username: String,
    var iconNumber: String,
    var injuries: List<Int?>,
    var experienceLevel: String,
    val routineType: String,
    var progressLevel: String,
    var userLevel: String,
    var userMedals: List<Int> = emptyList()
)
data class ShowExercise(
    var exercise: Exercise,
    var sets: Int,
    var reps: Int
)
class UserSessionManager(application: Context) : AndroidViewModel(application as Application) {
    private val apiService: UserService = RetrofitInstance.api
    private val sharedPreferences: SharedPreferences =
        getApplication<Application>().getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )
    var email by mutableStateOf("")
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var passwordConfirmation by mutableStateOf("")
    private val _exerciseDates = MutableStateFlow(emptyList<LocalDate>())
    var exerciseDates: StateFlow<List<LocalDate>> = _exerciseDates

    private val _ranking = MutableStateFlow<RankingResponse?>(null)
    var ranking: StateFlow<RankingResponse?> = _ranking


    private var _user = MutableStateFlow(
        User(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            emptyList(),
            "",
            "",
            "",
            "",
            emptyList()
        )
    )
    var user: StateFlow<User> = _user
    private var emailError by mutableStateOf<String?>(null)
    private var passwordError by mutableStateOf<String?>(null)
    private var nameError by mutableStateOf<String?>(null)
    private var lastnameError by mutableStateOf<String?>(null)
    private var usernameError by mutableStateOf<String?>(null)
    private var ageError by mutableStateOf<String?>(null)
    private var heightError by mutableStateOf<String?>(null)
    private var weightError by mutableStateOf<String?>(null)
    private var genderError by mutableStateOf<String?>(null)
    private var experienceLevelError by mutableStateOf<String?>(null)

    private fun incrementLevelProgress() {
        val limit = 2000
        val experience = _user.value.experienceLevel.toIntOrNull() ?: 0
        val userLevel = (experience / limit)
        val currentProgressLevel = experience - (userLevel * limit)
        _user.value = _user.value.copy(
            userLevel =  userLevel.toString(),
            progressLevel = if(currentProgressLevel<=2000){
                currentProgressLevel.toString()
            }else{
                "0"
            }
        )
    }
    private fun validateEmail(): String? {
        return when {
            email.isEmpty() -> "Este campo no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "El formato de correo electrónico no es válido"

            else -> null
        }
    }
    private fun validateName(): String? {
        return when {
            getUserSession().name.isEmpty() -> "Este campo no puede estar vacío"
            getUserSession().name.length < 3 -> "El nombre debe contener al menos 3 caracteres"
            !getUserSession().name.all { it.isLetter() } -> "El nombre debe contener solo letras"
            else -> null
        }
    }
    private fun validateLastname(): String? {
        return when {
            getUserSession().lastname.isEmpty() -> "Este campo no puede estar vacío"
            getUserSession().lastname.length < 3 -> "El apellido debe contener al menos 3 caracteres"
            !getUserSession().lastname.all { it.isLetter() } -> "El apellido debe contener solo letras"
            else -> null
        }
    }
    private fun validateUsername(): String? {
        return when {
            getUserSession().username.isEmpty() -> "Este campo no puede estar vacío"
            getUserSession().username.length < 8 -> "El nombre de usuario debe contener al menos 8 caracteres"
            else -> null
        }
    }
    private fun validateAge(): String? {
        return when {
            getUserSession().age.isEmpty() -> "Este campo no puede estar vacío"
            getUserSession().age.isEmpty() -> "Este campo no puede estar vacío"
            !getUserSession().age.all { it.isDigit() } -> "La edad debe contener solo números"
            else -> null
        }
    }
    private fun validateHeight(): String? {
        return when {
            getUserSession().height.isEmpty() -> "Este campo no puede estar vacío"
            !getUserSession().height.all { it.isDigit() } -> "La altura debe contener solo números"
            else -> null
        }
    }
    private fun validateWeight(): String? {
        return when {
            getUserSession().weight.isEmpty() -> "Este campo no puede estar vacío"
            !getUserSession().weight.all { it.isDigit() } -> "El peso debe contener solo números"
            else -> null
        }
    }
    private fun validateGender(): String? {
        return when {
            getUserSession().gender.isEmpty() -> "Este campo no puede estar vacío"
            else -> null
        }
    }
    private fun validateExperienceLevel(): String? {
        return when {
            getUserSession().experienceLevel.isEmpty() -> "Este campo no puede estar vacío"
            else -> null
        }
    }
    private fun validatePassword(): String? {
        return when {
            oldPassword.isEmpty() -> "Este campo no puede estar vacío"
            newPassword.isEmpty() -> "Este campo no puede estar vacío"
            passwordConfirmation.isEmpty() -> "Este campo no puede estar vacío"
            else -> null
        }
    }

    fun changeEmailValue(newValue: String) {
        email = newValue
    }

    fun changeOldPasswordValue(newValue: String) {
        oldPassword = newValue
    }

    fun changeNewPasswordValue(newValue: String) {
        newPassword = newValue
    }

    fun changePasswordConfirmationValue(newValue: String) {
        passwordConfirmation = newValue
    }

    private fun updatePasswordError(error: String?) {
        passwordError = error
    }

    fun obtenerPasswordError(): String? {
        return passwordError
    }

    private fun updateNameError(error: String?) {
        nameError = error
    }

    fun obtenerNameError(): String? {
        return nameError
    }

    private fun updateLastnameError(error: String?) {
        lastnameError = error
    }

    fun obtenerLastnameError(): String? {
        return lastnameError
    }

    private fun updateAgeError(error: String?) {
        ageError = error
    }

    fun obtenerAgeError(): String? {
        return ageError
    }

    private fun updateHeightError(error: String?) {
        heightError = error
    }

    fun obtenerHeightError(): String? {
        return heightError
    }

    private fun updateWeightError(error: String?) {
        weightError = error
    }

    fun obtenerWeightError(): String? {
        return weightError
    }

    private fun updateGenderError(error: String?) {
        genderError = error
    }

    fun obtenerGenderError(): String? {
        return genderError
    }

    private fun updateExperienceLevelError(error: String?) {
        experienceLevelError = error
    }

    fun obtenerExperienceLevelError(): String? {
        return experienceLevelError
    }

    private fun updateUsernameError(error: String?) {
        usernameError = error
    }

    fun obtenerUsernameError(): String? {
        return usernameError
    }

    private fun updateEmailError(error: String?) {
        emailError = error
    }

    fun obtenerEmailError(): String? {
        return emailError
    }

    fun validateUserSettings(): Boolean {
        val nameValidationError = validateName()
        val lastnameValidationError = validateLastname()
        val usernameValidationError = validateUsername()
        val ageValidationError = validateAge()
        val heightValidationError = validateHeight()
        val weightValidationError = validateWeight()
        val genderValidationError = validateGender()
        val experienceLevelValidationError = validateExperienceLevel()
        updateNameError(nameValidationError)
        updateLastnameError(lastnameValidationError)
        updateUsernameError(usernameValidationError)
        updateAgeError(ageValidationError)
        updateHeightError(heightValidationError)
        updateWeightError(weightValidationError)
        updateGenderError(genderValidationError)
        updateExperienceLevelError(experienceLevelValidationError)
        return nameValidationError == null && lastnameValidationError == null && usernameValidationError == null &&
                ageValidationError == null && heightValidationError == null && weightValidationError == null &&
                genderValidationError == null && experienceLevelValidationError == null
    }

    fun validateAndUpdateEmail(): Boolean {
        val emailValidationError = validateEmail()
        updateEmailError(emailValidationError)
        return emailValidationError == null
    }

    fun validateAndUpdatePassword(): Boolean {
        val passwordValidationError = validatePassword()
        updatePasswordError(passwordValidationError)
        return passwordValidationError == null
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
        iconNumber: String,
        injuries: String,
        experienceLevel: String,
        routineType: String
    ) {
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
            putString("iconNumber", iconNumber)
            putString("injuries", injuries)
            putString("experienceLevel", experienceLevel)
            putString("routineType", routineType)
            apply()
        }
    }

    fun getUserSession(): User {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "user_session",
            Context.MODE_PRIVATE
        )
        val userInjuriesString = sharedPreferences.getString("injuries", "[]")
        val userInjuriesList = userInjuriesString
            ?.removeSurrounding("[", "]")
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() }
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
            injuries = userInjuriesList!!,
            iconNumber = sharedPreferences.getString("iconNumber", "")!!,
            experienceLevel = sharedPreferences.getString("experienceLevel", "")!!,
            routineType = sharedPreferences.getString("routineType", "")!!,
            progressLevel = sharedPreferences.getString("score", "")!!,
            userLevel = sharedPreferences.getString("level", "")!!
        )
    }
    suspend fun isUserLoggedIn(): Boolean {
        return try {
            val sharedPreferences = getApplication<Application>().getSharedPreferences(
                "user_session",
                Context.MODE_PRIVATE
            )
            if (sharedPreferences.getString("token", "")!!.isEmpty()) {
                Log.d("isUserLoggedIn", "Token is empty")
                return false
            } else {
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
                    Log.e(
                        "DeleteAccount",
                        "Delete account failed: ${response.errorBody()?.string()}"
                    )
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
                val response = apiService.updateEmail(
                    "Bearer ${getUserSession().token}",
                    UpdateEmailRequest(newEmail)
                )
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
    fun updatePassword(oldPassword: String, newPassword: String, passwordConfirmation: String) {
        viewModelScope.launch {
            val response = apiService.updatePassword(
                "Bearer ${getUserSession().token}",
                UpdatePasswordRequest(oldPassword, newPassword, passwordConfirmation)
            )
            try {
                if (response.isSuccessful) {
                    Log.d("UpdatePassword", "Update password success: ${response.message()}")
                } else {
                    Log.e(
                        "UpdateEmail",
                        "Update password failed: ${response.errorBody()?.string()}"
                    )
                    Log.d("API Response", response.errorBody().toString())
                    Log.d("API Response", response.message())
                }
            } catch (e: Exception) {
                Log.e("UpdateEmail", "Error: ${e.localizedMessage}")
                Log.e("UpdateEmail", "Error: ${e.stackTrace}")
                Log.e("UpdateEmail", "Error: ${e.cause}")
                Log.e("UpdateEmail", "Error: ${e.suppressed}")
            }
        }
        cleanTextFields()
    }
    fun getUserInformation() {
        viewModelScope.launch {
            try {
                val response = apiService.getAccountSettings("Bearer ${getUserSession().token}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        saveUserSession(
                            getUserSession().token,
                            body.user.personal_name,
                            body.user.last_name,
                            body.user.age.toString(),
                            body.user.height.toString(),
                            body.user.weight.toString(),
                            body.user.gender_id.toString(),
                            body.user.email,
                            body.user.username,
                            body.user.icon_number,
                            body.user_injuries.toString(),
                            body.user_training_information.experience_level_id.toString(),
                            body.user_training_information.routine_type_id.toString()
                        )
                    }
                } else {
                    Log.e(
                        "GetInformation",
                        "Get information failed: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("GetInformation", "Error: ${e.localizedMessage}")
            }
        }
    }
    private suspend fun getImageProfile(): Bitmap? {
        return try {
            val response = apiService.getIcon(
                "Bearer ${getUserSession().token}",
                getUserSession().iconNumber
            )
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.byteStream()?.let { inputStream: InputStream? ->
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
    fun updateAccount() {
        viewModelScope.launch {
            val request = UserRequest(
                getUserSession().name,
                getUserSession().lastname,
                getUserSession().age.toInt(),
                getUserSession().height.toFloat(),
                getUserSession().weight.toFloat(),
                getUserSession().username,
                getUserSession().gender.toInt(),
                getUserSession().experienceLevel.toInt(),
                getUserSession().injuries.map { it ?: 0 },
                getUserSession().routineType.toInt()
            )
            try {
                val response = apiService.updateAccount("Bearer ${getUserSession().token}", request)
                if (response.isSuccessful) {
                    Log.d("UpdateAccount", "Update account success: ${response.message()}")
                } else {
                    Log.e(
                        "UpdateAccount",
                        "Update account failed: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("UpdateAccount", "Error: ${e.localizedMessage}")
            }
        }
    }
    fun updateIcon(iconNumber: String) {
        val updateIconRequest = UpdateIconRequest(iconNumber)
        viewModelScope.launch {
            try {
                val response =
                    apiService.updateIcon("Bearer ${getUserSession().token}", updateIconRequest)
                if (response.isSuccessful) {
                    Log.d("UpdateIcon", "Update icon success: ${response.message()}")
                } else {
                    Log.e("UpdateIcon", "Update icon failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("UpdateIcon", "Error: ${e.localizedMessage}")
            }
        }
    }
    fun getLevel() {
        viewModelScope.launch {
            try {
                val response = apiService.getScoreLevel("Bearer ${getUserSession().token}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val puntosExp = body.score
                        _user.value.experienceLevel = puntosExp.toString()
                        incrementLevelProgress()
                        Log.d("GetLevel", "Level: $puntosExp")
                    }
                } else {
                    Log.e("GetLevel", "Get level failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GetLevel", "Error: ${e.localizedMessage}")
                Log.e("Este es el error.", "Este es el error.")
            }
        }
    }
    fun getDatesWhenUserExercised() {
        viewModelScope.launch {
            try {
                val response = apiService.getDates("Bearer ${getUserSession().token}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("GetDates", "Dates: $body")
                        val dateFormatter = DateTimeFormatter.ISO_DATE
                        _exerciseDates.value =
                            body.exercise_dates.map { LocalDate.parse(it, dateFormatter) }
                    }
                } else {
                    Log.e("GetDates", "Get dates failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GetDates", "Error: ${e.localizedMessage}")
            }
        }
    }
    fun getUserMedals() {
        viewModelScope.launch {
            try {
                val response = apiService.getUserMedals("Bearer ${getUserSession().token}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("GetMedals", "Medals: $body")
                        _user.value.userMedals = body.userMedals
                    }
                } else {
                    Log.e("GetMedals", "Get medals failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GetMedals", "Error: ${e.localizedMessage}")
            }
        }
    }

    fun showRanking(){
        viewModelScope.launch {
            try {
                val response = apiService.getRanking("Bearer ${getUserSession().token}", 1,25)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _ranking.value = body
                        Log.d("GetRanking", "Ranking: $body")
                    }
                    } else {
                    Log.e("GetRanking", "Get ranking failed: ${response.errorBody()?.string()}")
                }
            }catch (e: Exception) {
                Log.e("GetRanking", "Error: ${e.localizedMessage}")
            }
        }
    }

    private val _exercises = MutableStateFlow<RoutineResponse?>(null)
    var exercises: StateFlow<RoutineResponse?> = _exercises
    private val _rutineCreated = MutableStateFlow(false)
    var rutineCreated: StateFlow<Boolean> = _rutineCreated


    fun getExercises(){
        viewModelScope.launch {
            try {
                val response = apiService.createRoutine("Bearer ${getUserSession().token}")
                Log.d("GetExercises", "Response: ${response.body()?.creada}")
                if( response.body()?.creada == "Rutina ya creada."){
                    _rutineCreated.value = true
                    _exercises.value = null
                }
                if (response.isSuccessful) {
                        val body = response.body()
                        _exercises.value = body

                } else {
                    Log.e("GetExercises", "Get exercises failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GetExercises", "Error: ${e.localizedMessage}")
            }
        }
    }
    fun sendRoutine(requestBody: RequestBody, token: String){
        viewModelScope.launch {
            try {
                val response = apiService.saveRoutine("Bearer $token", requestBody)
                if (response.isSuccessful) {
                    Log.d("SubmitRoutine", "Submit routine success: ${response.message()}")
                } else {
                    Log.e("SubmitRoutine", "Submit routine failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SubmitRoutine", "Error: ${e.localizedMessage}")
                Log.e("SubmitRoutine", "Error: ${e.stackTrace}")
            }
        }
    }

    private val _myExercises = MutableStateFlow<MyExerciseResponse?>(null)
    var myExercises: StateFlow<MyExerciseResponse?> = _myExercises

    fun getMyExercises(){
        viewModelScope.launch {
            try {
                val response = apiService.getMyRoutine("Bearer ${getUserSession().token}")
                Log.d("GetMyExercises", "Response: ${response.body()}")
                Log.d("GetMyExercises", "Response body: ${response.body()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("GetMyExercises", "My exercises: $body")
                    _myExercises.value = body
                    Log.d("GetMyExercises", "My exercises: ${_myExercises.value!!.selectedExercises}")
                }else{
                    Log.e("GetMyExercises", "Get my exercises failed: ${response.errorBody()?.string()}")
                }
            }catch (e: Exception) {
                Log.e("GetMyExercises", "Error: ${e.localizedMessage}")
            }
        }
    }

    private val _currentExercise = mutableStateOf(ShowExercise(
        exercise = Exercise(
            id = 0,
            name = "",
            description = "",
            video_url = "",
            image_path = "",
            experience_level_id = 0,
            warning= "",
        ),
        sets = 0,
        reps = 0
    ))
    val currentExercise: State<ShowExercise> = _currentExercise

    fun showExercise(token: String, exerciseId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getMyExercises("Bearer $token", exerciseId)
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("GetExercise", "Exercise: $body")
                    if (body != null) {
                        _currentExercise.value.exercise = body.exercise
                        _currentExercise.value.sets = body.sets
                        _currentExercise.value.reps = body.reps
                    }
                } else {
                    Log.e("GetExercise", "Get exercise failed: ${response.errorBody()?.string()}")
                }
            }catch (e: Exception) {
                Log.e("GetExercise", "Error: ${e.localizedMessage}")
            }
        }
    }

    private fun cleanTextFields(){
        oldPassword = ""
        newPassword = ""
        passwordConfirmation = ""
    }
    init {
        getDatesWhenUserExercised()
        getLevel()
        getUserMedals()
    }
}


