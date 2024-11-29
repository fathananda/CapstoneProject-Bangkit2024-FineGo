package com.dicoding.finego.api

import com.dicoding.finego.RegisterRequest
import com.dicoding.finego.RegisterResponse
import com.dicoding.finego.UserInputRequest
import com.dicoding.finego.UserProfileResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>


    @POST("/user/{id}/input-profile")
    fun inputUserProfile(
        @Path("id") userId: String,
        @Body request: UserInputRequest
    ): Call<Void>


    @GET("/user/{id}/profile")
    fun getUserProfile(
        @Path("id") userId: String
    ): Call<UserProfileResponse>
}
