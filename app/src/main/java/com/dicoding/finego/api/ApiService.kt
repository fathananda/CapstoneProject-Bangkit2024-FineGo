package com.dicoding.finego.api

import com.dicoding.finego.features.budgetplan.BudgetPlanResponse
import com.dicoding.finego.auth.login.LoginRequest
import com.dicoding.finego.auth.login.LoginResponse
import com.dicoding.finego.features.monthlyreport.MonthlyReportResponse
import com.dicoding.finego.auth.register.RegisterRequest
import com.dicoding.finego.auth.register.RegisterResponse
import com.dicoding.finego.features.transactiontracking.TransactionRequest
import com.dicoding.finego.features.transactiontracking.TransactionResponse
import com.dicoding.finego.features.profile.UserInputRequest
import com.dicoding.finego.features.profile.UserProfileResponse
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

    @GET("/user/{id}/monthly-report")
    suspend fun getMonthlyReport(@Path("id") userId: String
    ): Response<MonthlyReportResponse>

    @GET("/user/{id}/budget-plan")
    suspend fun getBudgetPlan(@Path("id") userId: String
    ): Response<BudgetPlanResponse>
}
