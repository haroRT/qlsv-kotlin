package com.kma.qlsv.ui.screen.search

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.repository.normal.StudentRepository
import com.kma.qlsv.ui.screen.student_change.STATUS
import com.kma.qlsv.utils.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UiState(
    val status: STATUS = STATUS.IDLE,
    val listStudent: List<StudentModel> = emptyList(),
    val listSearch: List<StudentModel> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val firebaseAuth: FirebaseAuth,
    private val pref: SharedPref
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun searchByIdAndName(keyword: String) {
        if (keyword.isEmpty()) {
            _uiState.update {
                it.copy(
                    listSearch = emptyList()
                )
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    listSearch = it.listStudent.filter {
                        it.id.contains(keyword, ignoreCase = true) ||
                                it.name.contains(keyword, ignoreCase = true)
                    }
                )
            }
        }
    }

    fun getAllStudent() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(status = STATUS.LOADING) }
            if (pref.isAdmin) {
                studentRepository.getAllStudents()
                    .onSuccess { list ->
                        _uiState.update {
                            it.copy(
                                status = STATUS.SUCCESS,
                                listStudent = list
                            )
                        }
                    }
                    .onFailure { e ->
                        Log.d("hao", "getAllStudent: ${e.message}")
                        _uiState.update {
                            it.copy(
                                status = STATUS.FAILURE,
                            )
                        }
                    }
            } else {
                firebaseAuth.currentUser?.email?.let {
                    studentRepository.getStudentByEmail(it)
                        .onSuccess { list ->
                            _uiState.update {
                                it.copy(
                                    status = STATUS.SUCCESS,
                                    listStudent = list
                                )
                            }
                        }
                        .onFailure { e ->
                            Log.d("hao", "getAllStudent: ${e.message}")
                            _uiState.update {
                                it.copy(
                                    status = STATUS.FAILURE,
                                )
                            }
                        }
                }
            }
        }
    }

    fun deleteStudent(context: Context, student: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            studentRepository.deleteStudent(student.id)
                .onSuccess {
                    getAllStudent()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Xoá sinh viên thành công", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            status = STATUS.FAILURE
                        )
                    }
                }
        }
    }

    fun isAdmin() = pref.isAdmin
}