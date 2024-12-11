package com.dicoding.finego.auth.login

data class LoginResponse (
    val status: String,
    val message: String,
    val token: String
)