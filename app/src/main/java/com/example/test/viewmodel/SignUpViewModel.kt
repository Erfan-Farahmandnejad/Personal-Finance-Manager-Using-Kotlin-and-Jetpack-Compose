package com.example.test.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun signUp(onSuccess: () -> Unit) {
        if (username.isBlank()) {
            errorMessage = "Username is required"
            return
        }
        
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = repository.signUp(username, email, password)
            isLoading = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                errorMessage = it.message
            }
        }
    }
}
