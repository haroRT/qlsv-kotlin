package com.kma.qlsv.ui.screen.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kma.qlsv.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val user: FirebaseUser? = null
)

@HiltViewModel
class SignupVM @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.register(email, password)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        user = result.getOrNull(),
                        isSuccess = true,
                        isLoading = false
                    )
                }
            }
            else{
                _uiState.update {
                    it.copy(
                        isSuccess = false,
                        isLoading = false
                    )
                }
            }
        }
    }
}