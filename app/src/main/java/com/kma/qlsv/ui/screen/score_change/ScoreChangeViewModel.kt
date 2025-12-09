package com.kma.qlsv.ui.screen.score_change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.qlsv.data.ScoreModel
import com.kma.qlsv.data.ScoreWithSubject
import com.kma.qlsv.data.StudentModel
import com.kma.qlsv.data.SubjectModel
import com.kma.qlsv.repository.normal.ScoreRepository
import com.kma.qlsv.repository.normal.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val scoreWithSubject: ScoreWithSubject? = null,
    val type: TYPE = TYPE.CREATE,
    val listSubject: List<SubjectModel> = listOf()
)

@HiltViewModel
class ScoreChangeViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val subjectRepository: SubjectRepository
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
            createScore(_uiState.value.scoreWithSubject?.score!!)
        } else {
            updateScore(_uiState.value.scoreWithSubject?.score!!)
        }
    }

    fun getListSubject(){
        viewModelScope.launch(Dispatchers.IO) {
            subjectRepository.getAllSubjects()
                .onSuccess { listSubject->
                    _uiState.update {
                        it.copy(
                            listSubject = listSubject
                        )
                    }
                }
                .onFailure { e ->
                }
        }
    }

    private fun createScore(score: ScoreModel) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.addScore(score)
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

    private fun updateScore(score: ScoreModel) {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.updateScore(score)
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

    fun initData(student: StudentModel?, scoreWithSubject: ScoreWithSubject?) {
        if (scoreWithSubject != null) {
            _uiState.update {
                it.copy(
                    type = TYPE.UPDATE,
                    scoreWithSubject = scoreWithSubject,
                    student = student
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    type = TYPE.CREATE,
                    scoreWithSubject = ScoreWithSubject(),
                    student = student
                )
            }
        }
    }

    fun updateData(name: String, score1: Float, score2: Float, subjectModel: SubjectModel) {
        _uiState.update {
            it.copy(
                scoreWithSubject = it.scoreWithSubject?.copy(
                    score = it.scoreWithSubject.score?.copy(
                        name = name,
                        score1 = score1,
                        score2 = score2,
                        subjectId = subjectModel.id,
                        studentId = it.student?.id ?: ""
                    )
                )
            )
        }
    }

    fun getType(): TYPE {
        return _uiState.value.type
    }

}