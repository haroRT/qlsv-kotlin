package com.kma.qlsv.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.qlsv.repository.normal.StudentRepository
import com.kma.qlsv.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val isAdmin: Boolean = false,
    val totalStudent: Int = 0,
    val email: String = "",
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun getCountStudent() {
        viewModelScope.launch(Dispatchers.IO) {
            studentRepository.getAllStudents()
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(totalStudent = list.size)
                    }
                }
        }
    }

    fun getUserCurrent() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isAdmin = authRepository.isAdmin(),
                    email = authRepository.getCurrentUser()?.email ?: ""
                )
            }
        }
    }

}