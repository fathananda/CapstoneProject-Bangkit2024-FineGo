package com.dicoding.finego.features.transactiontracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.finego.helper.Repository
import com.dicoding.finego.helper.Result
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: Repository) : ViewModel() {
    private val _transactions = MutableLiveData<Result<TransactionResponse>>()
    val transactions: LiveData<Result<TransactionResponse>> = _transactions

    fun fetchTransactions(userId: String) {
        viewModelScope.launch {
            _transactions.value = Result.Loading
            _transactions.value = repository.getTransactions(userId)
        }
    }
}

