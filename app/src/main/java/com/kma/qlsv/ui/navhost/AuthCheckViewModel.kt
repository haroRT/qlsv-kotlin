package com.kma.qlsv.ui.navhost

import androidx.lifecycle.ViewModel
import com.kma.qlsv.repository.auth.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthCheckViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {
    
    fun isUserLoggedIn(): Boolean {
        return authRepository.getCurrentUser() != null
    }
}