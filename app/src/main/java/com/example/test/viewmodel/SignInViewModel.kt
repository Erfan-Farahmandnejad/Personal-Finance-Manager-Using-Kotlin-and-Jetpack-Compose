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
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            val result = repository.signIn(email, password)
            isLoading = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                errorMessage = it.message
            }
        }
    }
}
