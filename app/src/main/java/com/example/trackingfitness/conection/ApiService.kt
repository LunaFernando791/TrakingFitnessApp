package com.example.trackingfitness.conection

import com.example.trackingfitness.viewModel.Medal
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Clases para manejar las respuestas y las solicitudes de la API.
data class User(
    val personal_name: String,
    val last_name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val gender_id: Int,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val experience_level_id: Int,
    val routine_type_id: Int,
    val username: String,
    val injuries: List<Int>,
)

data class UserResponse(
    val personal_name: String,
    val last_name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val gender_id: Int,
    val email: String,
    val username: String,
    val icon_number: String,
)

data class UserFriendInformation(
    val id: Int,
    val personal_name: String,
    val last_name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val username: String,
    val gender_id: String,
    val icon_number: String,
)

data class UserTrainingInformation(
    val user_id: Int,
    val routine_type_id: Int,
    val experience_level_id: Int,
)

data class UserRanking(
    val id: Int,
    val username: String,
    val score: Int,
    val icon_number: String,
    val icon_url: String,
)

data class FriendInformation(
    val id: Int,
    val username: String,
    val icon_url: String,
)

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val video_url: String,
    val image_path: String,
    val experience_level_id: Int,
    val warning: String,
)

data class MyExercise(
    val exercise_id: Int,
    val status: String,
    val exercise_name: String,
    val image_path: String,
    val description: String,
)

data class MyExerciseResponse(
    val selectedExercises: List<MyExercise>,
    val completed : String,
)
data class Sets(
    val name: String,
    val reps: List<Int>,
    val sets: List<Int>,
)

data class FriendRequest(
    val id: Int,
    val sender_id: Int,
    val sender_username: String,
    val icon_url: String,
)
data class RegisterResponse(
    val success: Boolean,
    val message: String
)
data class LoginResponseUser(
    val access_token: String?,
    val token_type: String?,
    val user: UserResponse,
    val user_injuries: List<Int>,
    val user_training_information: UserTrainingInformation,
    val message: String?
)
data class LoginRequestUser(
    val email: String,
    val password: String
)
data class ForgotPasswordRequest(
    val email: String,
)
data class ForgotPasswordResponse(
    val message: String
)
data class ValidateOTPRequest(
    val email: String,
    val otp: String
)
data class ValidateOTPResponse(
    val message: String
)
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val password: String,
)
data class ResetPasswordResponse(
    val message: String
)

data class RankingResponse(
    val topUsers: List<UserRanking>,
    val userPosition: Int,
    val currentPage: Int,
    val totalPages: Int,
)

data class UpdateEmailRequest(
    val email: String
)
data class UpdateEmailResponse(
    val message: String
)

data class UpdatePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)
data class UpdatePasswordResponse(
    val message: String
)
data class ResponseTokenValid(
    val valid: Boolean
)
data class UserRequest(
    val personal_name: String,
    val last_name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val username: String,
    val gender_id: Int,
    val experience_level_id: Int,
    val injuries: List<Int>,
    val routine_type_id: Int
)

data class ResponseUpdateAccount(
    val user: UserResponse,
    val user_training_information: UserTrainingInformation,
    val user_injuries: List<Int>,
)

data class ImageResponse(
    val id: Int,
    val image_url: String
)

data class UpdateIconRequest(
    val icon_number: String
)
data class UpdateIconResponse(
    val message: String
)

data class ResponseUserScoreLevel(
    val score: Int
)

data class FriendRequestResponse(
    val friendsRequestCount: Int,
)

data class FriendsListResponse(
    val friends: Map<String, FriendInformation>,
    val friendRequests: List<FriendRequest>,
    val availableUsers: List<FriendInformation>,
)

data class FriendProfileResponse(
    val user: UserFriendInformation,
    val icon_number: String,
    val exercise_dates: List<String>,
    val score: Int,
    val userMedals: List<Int>,
)

data class SendFriendRequestResponse(
    val success: String
)

data class GetDatesResponse(
    val exercise_dates: List<String>,
)


data class AllMedalResponse(
    val medals: List<Medal>,
)

data class UserMedalsResponse(
    val userMedals: List<Int>,
)
//data class RoutineResponse(
//    val routineType: String,
//    val exercises: List<Exercise>,
//    val routineSets: Sets,
//    val exercisesList: List<Exercise>,
//    val created: String,
//    val completed: String,
//)
data class RoutineResponse(
    val routineType: String,
    val exercises: List<Exercise>,
    val routineSets: Sets,
    val exercisesList: List<Exercise>,
    val created: String,
    val completed: String,
)

data class ExerciseResponse(
    val message: String,
)

data class ExerciseRequest(
    val id: Int,
    val sets: Int,
    val reps: Int
)
data class CurrentExerciseResponse(
    val exercise: Exercise,
    val sets: Int,
    val reps: Int,
)


interface UserService { // Interfaz para definir las operaciones del servicio.

    @POST("/api/register")
    suspend fun register(@Body user: User): Response<RegisterResponse>
    // Ruta para registrar al usuario.
    @POST("/api/login")
    suspend fun loginUser(@Body loginRequestUser: LoginRequestUser): Response<LoginResponseUser>
    // Ruta para iniciar sesión.
    @POST("/api/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>
    // Ruta para cerrar sesión.
    @GET("/api/token/valid")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): Response<ResponseTokenValid>
    // Ruta para validar el token al iniciar sesión.
    @GET("/api/icon/{filename}")
    suspend fun getIcon(
        @Header("Authorization") token: String,
        @Path("filename") id: String
    ): Response<ResponseBody>
    // Ruta para obtener un icono por su ID.
    @GET("/api/edit-icon")
    suspend fun getImages(
    ): Response<List<ImageResponse>>
    // Ruta que trae todas las imagenes del servidor, para que el usuario las seleccione.
    @PUT("/api/user/update-icon")
    suspend fun updateIcon(
        @Header("Authorization") token: String,
        @Body updateIconRequest: UpdateIconRequest,
    ): Response<UpdateIconResponse>
    // Ruta para actualizar el icono del usuario.


    @DELETE("/api/account-settings")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): Response<Unit>
    //Ruta para eliminar la cuenta.
    @GET("/api/account-settings")
    suspend fun getAccountSettings(
        @Header("Authorization") token: String
    ): Response<ResponseUpdateAccount>
    // Ruta para obtener la configuración de la cuenta.
    @PUT("/api/account-settings/email")
    suspend fun updateEmail(
        @Header("Authorization") token: String,
        @Body updateEmailRequest: UpdateEmailRequest
    ): Response<UpdateEmailResponse>
    // Ruta para actualizar el correo electrónico.
    @PUT("/api/account-settings/password")
    suspend fun updatePassword(
        @Header("Authorization") token: String,
        @Body updatePasswordRequest: UpdatePasswordRequest
    ): Response<UpdatePasswordResponse>
    // Ruta para actualizar la contraseña.
    @PUT("/api/account-settings")
    suspend fun updateAccount(
        @Header("Authorization") token: String,
        @Body userRequest: UserRequest
    ): Response<ResponseUpdateAccount>
    //Ruta para actualizar la cuenta.
    @GET("/api/retrieve/score")
    suspend fun getScoreLevel(
        @Header("Authorization") token: String
    ):Response<ResponseUserScoreLevel>
    //RUTA QUE OBTIENE EL NIVEL DE EXPERIENCIA DEL USUARIO
    @GET("/api/friendsRequest")
    suspend fun getFriendsRequest(
        @Header("Authorization") token: String
    ): Response<FriendRequestResponse>
    //RUTA QUE OBTIENE EL NUMERO DE SOLICITUDES DE AMISTAD DEL USUARIO
    @GET("/api/friends")
    suspend fun getFriends(
        @Header("Authorization") token: String
    ): Response<FriendsListResponse>
    /*RUTA QUE OBTIENE LO RELACIONADO A AMIGOS*/
    @POST("/api/friends/accept/{id}")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String,
        @Path("id") friendId: Int
    ): Response<Unit>
    //RUTA QUE ACEPTA UNA SOLICITUD DE AMISTAD
    @POST("/api/friends/reject/{id}")
    suspend fun declineFriendRequest(
        @Header("Authorization") token: String,
        @Path("id") friendId: Int
    ): Response<FriendRequestResponse>
    //RUTA QUE RECHAZA UNA SOLICITUD DE AMISTAD
    @GET("/api/user/{username}")
    suspend fun getFriendUser(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): Response<FriendProfileResponse>
    //RUTA QUE OBTIENE UN USUARIO POR SU NOMBRE DE USUARIO
    @POST("/api/friends/request/{id}")
    suspend fun sendFriendRequest(
        @Header("Authorization") token: String,
        @Path("id") friendId: Int
    ): Response<SendFriendRequestResponse>
    //RUTA QUE ENVIA UNA SOLICITUD DE AMISTAD
    @GET("/api/dates")
    suspend fun getDates(
        @Header("Authorization") token: String
    ): Response<GetDatesResponse>
    //RUTA QUE OBTIENE LAS FECHAS DE EJERCICIO DEL USUARIO
    @GET("/api/retrieve/allMedals")
    suspend fun getMedals(
    ): Response<AllMedalResponse>
    //RUTA QUE OBTIENE TODAS LAS MEDALLAS DE LA PÁGINA
    @GET("/api/retrieve/medals")
    suspend fun getUserMedals(
        @Header("Authorization") token: String
    ): Response<UserMedalsResponse>
    //RUTA QUE OBTIENE TODAS LAS MEDALLAS DEL USUARIO
    @GET("/api/scores")
    suspend fun getRanking(
        @Header("Authorization") token: String,
        @Query ("page") page: Int,
        @Query ("limit") limit: Int
    ): Response<RankingResponse>
    @GET("/api/routine/create")
    suspend fun createRoutine(
        @Header("Authorization") token: String
    ): Response<RoutineResponse>
    //RUTA QUE CREA UNA RUTINA
    @POST("/api/routine/save")
    suspend fun saveRoutine(
        @Header("Authorization") token: String,
        @Body body: RequestBody
    ): Response<ExerciseResponse>
    //RUTA QUE OBTIENE LA RUTINA DEL USUARIO
    @GET("/api/routine/show")
    suspend fun getMyRoutine(
        @Header("Authorization") token: String
    ): Response<MyExerciseResponse>
    //RUTA QUE OBTIENE LA RUTINA DEL USUARIO
    @GET("/api/exercise/{id}")
    suspend fun getMyExercises(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<CurrentExerciseResponse>
    //Route to get one exercise in specific
    @POST("/api/routine/continue")
    suspend fun continueRoutine(
        @Header("Authorization") token: String
    ): Response<MyExerciseResponse>


    @POST("/api/auth/forget-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<ForgotPasswordResponse>
    // Ruta de olvido de contraseña.
    @POST("/api/auth/validate-otp")
    suspend fun validateOTP(@Body validateOTPRequest: ValidateOTPRequest): Response<ValidateOTPResponse>
    // Ruta del código de validación.
    @POST("/api/auth/reset-password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Response<ResetPasswordResponse>
    // Ruta del cambio de contraseña.

}