package com.example.trackingfitness.conection

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class User(
    val id: Int,
    val personal_name: String,
    val last_name: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val email: String,
    val email_verified_at: String,
    val username: String,
    val gender_id: Int,
    val experience_level_id: Int,
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
    val email: String
)

data class ForgotPasswordResponse(
    val message: String
)


interface UserService {

    @POST("/api/register")
    suspend fun register(@Body user: UserItem): Response<RegisterResponse>

    @POST("/api/login")
    suspend fun loginUser(@Body loginRequestUser: LoginRequestUser): Response<LoginResponseUser>

    @POST("/api/auth/forget-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<ForgotPasswordResponse>

}