package com.kma.qlsv.ui.screen.student_detail

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.qlsv.data.ScoreModel
import com.kma.qlsv.data.ScoreWithSubject
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.repository.normal.ScoreRepository
import com.kma.qlsv.utils.SharedPref
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
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val student: StudentModel? = null,
    val listScore: List<ScoreWithSubject> = emptyList(),
    val type: TYPE = TYPE.CREATE
)

@HiltViewModel
class StudentDetailViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val pref: SharedPref
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()


    fun getScoreByStudent(){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.student?.let {
                scoreRepository.getScoreByStudent(it)
                    .onSuccess { listScore ->
                        _uiState.update {
                            it.copy(
                                listScore = listScore
                            )
                        }
                    }
            }
        }
    }

    fun deleteScore(context: Context, score: ScoreModel) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.deleteScore(score)
                .onSuccess {
                    getScoreByStudent()
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Xoá sinh điểm thành công", Toast.LENGTH_LONG).show()
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

    fun getType(): TYPE {
        return _uiState.value.type
    }

    fun isAdmin() = pref.isAdmin

}