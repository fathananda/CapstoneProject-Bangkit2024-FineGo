package com.dicoding.finego.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.finego.helper.Repository
import com.dicoding.finego.features.budgetplan.BudgetPlanningViewModel
import com.dicoding.finego.features.monthlyreport.MonthlyReportViewModel
import com.dicoding.finego.features.profile.ProfileViewModel
import com.dicoding.finego.features.transactiontracking.TransactionViewModel

class ViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(MonthlyReportViewModel::class.java)) {
            return MonthlyReportViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(BudgetPlanningViewModel::class.java)) {
            return BudgetPlanningViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}