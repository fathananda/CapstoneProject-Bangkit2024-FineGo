package com.dicoding.finego

data class UserInputRequest(
    val profile: Profile,
    val expense: Expense
)

data class Profile(
    val name: String,
    val email: String,
    val birthDate: String,  // Format: "YYYY-MM-DD"
    val province: String,
    val income: Int,
    val savings: Int
)

data class Expense(
    val foodExpenses: Int,
    val transportationExpenses: Int,
    val housingCost: Int,
    val electricityBill: Int,
    val waterBill: Int,
    val internetCost: Int,
    val debt: Int
)
