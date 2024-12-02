package com.dicoding.finego


import com.dicoding.finego.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val apiService: ApiService) {
    suspend fun getUserProfile(userId: String): Result<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserProfile(userId).execute()
                if (response.isSuccessful) {
                    val profile = response.body()?.data
                    if (profile != null) {
                        Result.Success(profile)
                    } else {
                        Result.Error("Data profil tidak ditemukan.")
                    }
                } else {
                    Result.Error("Gagal memuat profil: ${response.errorBody()?.string()}")
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
}
