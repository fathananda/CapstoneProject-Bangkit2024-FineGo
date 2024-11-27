package com.dicoding.finego.api

import com.dicoding.finego.RegisterRequest
import com.dicoding.finego.RegisterResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}
