package com.dicoding.finego.api

import com.dicoding.finego.LoginRequest
import com.dicoding.finego.LoginResponse
import com.dicoding.finego.RegisterRequest
import com.dicoding.finego.RegisterResponse
import com.dicoding.finego.TransactionRequest
import com.dicoding.finego.TransactionResponse
import com.dicoding.finego.UserInputRequest
import com.dicoding.finego.UserProfileResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/auth/register")
    fun register(@Body request: RegisterRequest
    ): Call<RegisterResponse>


    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest
    ): Response<LoginResponse>


    @POST("/user/{id}/input-profile")
    fun inputUserProfile(
        @Path("id") userId: String,
        @Body request: UserInputRequest
    ): Call<Void>


    @GET("/user/{id}/profile")
    suspend fun getUserProfile(
        @Path("id") userId: String
    ): Response<UserProfileResponse>


    @POST("/user/{id}/transactions")
    suspend fun addTransaction(
        @Path("id") userId: String,
        @Body transactionRequest: TransactionRequest
    ): Response<Unit>

    @GET("/user/{id}/transactions")
    suspend fun getTransactions(
        @Path("id") userId: String
    ): Response<TransactionResponse>
}
