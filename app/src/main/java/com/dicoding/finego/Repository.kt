package com.dicoding.finego


import com.dicoding.finego.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val apiService: ApiService) {
    suspend fun getUserProfile(userId: String): Result<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    val profileData = response.body()?.data?.profileData
                    if (profileData != null) {
                        Result.Success(
                            Profile(
                                name = profileData.name,
                                email = profileData.email,
                                birthdate = profileData.birthdate,
                                province = profileData.province
                            )
                        )
                    } else {
                        Result.Error("Data profil tidak ditemukan.")
                    }
                } else {
                    Result.Error("Gagal memuat profil: ${response.message()}")
                }
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "Terjadi kesalahan.")
            }
        }
    }



    suspend fun getTransactions(userId: String): Result<TransactionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTransactions(userId)
                if (response.isSuccessful) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.message())
                }
            }
            catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "An unexpected error occurred")
            }

        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Result.Success(responseBody)
                } else {
                    Result.Error("Login berhasil, tetapi token tidak ditemukan.")
                }
            } else {
                Result.Error("Login gagal: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }


    suspend fun getMonthlyReport(userId: String): Result<MonthlyReportResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMonthlyReport(userId)
                if (response.isSuccessful) {
                    val financeReport = response.body()
                    if (financeReport != null) {
                        Result.Success(financeReport)
                    } else {
                        Result.Error("Data laporan keuangan tidak ditemukan.")
                    }
                } else {
                    Result.Error("Gagal memuat laporan keuangan: ${response.message()}")
                }
            } catch (e: Exception) {
                Result.Error(e.localizedMessage ?: "Terjadi kesalahan.")
            }
        }
    }





    suspend fun getBudgetPlan(userId: String): Result<BudgetPlanResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBudgetPlan(userId)
                if (response.isSuccessful) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(response.message())
                }
            } catch (e: Exception) {
                Result.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }


}
