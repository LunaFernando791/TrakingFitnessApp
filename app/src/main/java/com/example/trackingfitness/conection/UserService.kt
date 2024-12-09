package com.example.trackingfitness.conection

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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
    val experience_level_id: Int
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

interface UserService {

    @POST("/api/register")
    suspend fun register(@Body user: User): Response<RegisterResponse>

    @POST("/api/login")
    suspend fun loginUser(@Body loginRequestUser: LoginRequestUser): Response<LoginResponseUser>

    @POST("/api/auth/forget-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("/api/auth/validate-otp")
    suspend fun validateOTP(@Body validateOTPRequest: ValidateOTPRequest): Response<ValidateOTPResponse>

    @POST("/api/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>


}