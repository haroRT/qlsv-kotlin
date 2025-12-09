package com.kma.qlsv.ui.screen.student_change

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.repository.normal.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class TYPE {
    CREATE, UPDATE
}

enum class STATUS {
    SUCCESS, FAILURE, LOADING, IDLE
}

data class UiState(
    val status: STATUS = STATUS.IDLE,
    val student: StudentModel? = null,
    val type: TYPE = TYPE.CREATE
)

@HiltViewModel
class StudentChangeViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun handleStudent() {
        _uiState.update {
            it.copy(
                status = STATUS.LOADING
            )
        }
        if (_uiState.value.type == TYPE.CREATE) {
            createStudent(_uiState.value.student!!)
        } else {
            updateStudent(_uiState.value.student!!)
        }
    }
    fun validateUsername(username: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_]{3,20}$")
        return regex.matches(username)
    }

    private fun createStudent(student: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!validateUsername(student.name)){
                 withContext(Dispatchers.Main){
                     Toast.makeText(context, "name chứa ký tự không hợp lệ", Toast.LENGTH_SHORT).show()
                 }
                return@launch
            }
            studentRepository.addStudent(student)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            status = STATUS.SUCCESS
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            status = STATUS.FAILURE
                        )
                    }
                }
        }
    }

    private fun updateStudent(student: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            studentRepository.updateStudent(student)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            status = STATUS.SUCCESS
                        )
                    }
                }
                .onFailure { e->
                    _uiState.update {
                        it.copy(
                            status = STATUS.FAILURE
                        )
                    }
                }
        }
    }

    fun initData(student: StudentModel?) {
        if (student != null) {
            _uiState.update {
                it.copy(
                    type = TYPE.UPDATE,
                    student = student
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    type = TYPE.CREATE,
                    student = StudentModel()
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(
                student = it.student?.copy(
                    name = name
                )
            )
        }
    }

    fun updateBirthDay(birthday: String) {
        _uiState.update {
            it.copy(
                student = it.student?.copy(
                    birthday = birthday
                )
            )
        }
    }

    fun updateAddress(address: String) {
        _uiState.update {
            it.copy(
                student = it.student?.copy(
                    address = address
                )
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                student = it.student?.copy(
                    email = email
                )
            )
        }
    }

    fun getType(): TYPE {
        return _uiState.value.type
    }

}