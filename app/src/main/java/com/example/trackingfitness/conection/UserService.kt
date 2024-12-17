package com.example.trackingfitness.conection

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

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
    val username: String,
    val experience_level_id: Int,
    val routine_type_id: Int,
    val injuries: List<Int>
)
data class RegisterResponse(
    val success: Boolean,
    val message: String
)
data class LoginResponseUser(
    val access_token: String?,
    val token_type: String?,
    val user: User,
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

data class UpdateEmailRequest(
    val email: String
)
data class UpdateEmailResponse(
    val message: String
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
    @DELETE("/api/account-settings")
    suspend fun deleteAccount(
        @Header("Authorization") token: String
    ): Response<Unit>
    @PUT("/api/account-settings/email")
    suspend fun updateEmail(
        @Header("Authorization") token: String,
        @Body updateEmailRequest: UpdateEmailRequest
    ): Response<UpdateEmailResponse>


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