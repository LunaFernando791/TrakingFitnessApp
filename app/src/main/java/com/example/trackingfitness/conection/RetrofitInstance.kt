package com.example.trackingfitness.conection

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.13:8000/api/register/"  // URL DEL SERVIDOR

    val api: UserService by lazy { // INSTANCIA DEL SERVICIO
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserService::class.java)
    }
}
