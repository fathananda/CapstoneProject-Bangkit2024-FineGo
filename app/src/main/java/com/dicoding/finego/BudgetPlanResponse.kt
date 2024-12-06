package com.dicoding.finego

data class BudgetPlanResponse(
    val status: String,
    val recommendations: Recommendations
)

data class Recommendations(
    val date: String,
    val monthly_limit: Double,
    val savings_rate: String,
    val budget_plan: BudgetPlan
)

data class BudgetPlan(
    val electricity_bill: Int,
    val food_expenses: Int,
    val internet_cost: Int,
    val housing_cost: Int,
    val debt: Int,
    val transportation_expenses: Int,
    val water_bill: Int
)
