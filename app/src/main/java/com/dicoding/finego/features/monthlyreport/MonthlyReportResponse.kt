package com.dicoding.finego.features.monthlyreport

import com.google.gson.annotations.SerializedName


data class MonthlyReportResponse(
    @SerializedName("finance_report") val financeReport: FinanceReport
)

data class FinanceReport(
    @SerializedName("date") val date: String,
    @SerializedName("finance_report") val financeStatus: String,
    @SerializedName("highest_expenses") val highestExpenses: List<Expense>
)

data class Expense(
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: Int
)
