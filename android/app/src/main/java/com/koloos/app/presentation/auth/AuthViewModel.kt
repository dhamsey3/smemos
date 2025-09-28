package com.koloos.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koloos.app.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun requestOtp(phone: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { repository.requestOtp(phone) }
                .onSuccess { otp ->
                    _uiState.value = _uiState.value.copy(isLoading = false, otp = otp, phone = phone)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }

    fun verifyOtp(name: String) {
        val phone = _uiState.value.phone ?: return
        val otp = _uiState.value.otp ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { repository.verifyOtp(phone, name, otp) }
                .onSuccess { merchant ->
                    _uiState.value = _uiState.value.copy(isLoading = false, merchantName = merchant.name)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
                }
        }
    }
}

data class AuthUiState(
    val phone: String? = null,
    val otp: String? = null,
    val merchantName: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)
