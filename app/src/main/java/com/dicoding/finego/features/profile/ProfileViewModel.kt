package com.dicoding.finego.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.finego.helper.Repository
import com.dicoding.finego.helper.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository) : ViewModel() {
    private val _profileState = MutableStateFlow<Result<Profile>>(Result.Loading)
    val profileState: StateFlow<Result<Profile>> = _profileState

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _profileState.value = Result.Loading
            val result = repository.getUserProfile(userId)
            _profileState.value = result
        }
    }
}