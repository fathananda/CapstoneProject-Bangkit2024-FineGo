package com.dicoding.finego.features.budgetplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.finego.helper.Repository
import com.dicoding.finego.helper.Result
import kotlinx.coroutines.launch

class BudgetPlanningViewModel(private val repository: Repository) : ViewModel() {
    private val _budgetPlan = MutableLiveData<Result<BudgetPlanResponse>>()
    val budgetPlan: LiveData<Result<BudgetPlanResponse>> = _budgetPlan

    fun fetchBudgetPlan(userId: String) {
        _budgetPlan.value = Result.Loading
        viewModelScope.launch {
            val result = repository.getBudgetPlan(userId)
            _budgetPlan.postValue(result)
        }
    }
}