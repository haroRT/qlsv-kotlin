package com.kma.qlsv.ui.screen.subject_change

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.qlsv.data.SubjectModel
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
    val subject: SubjectModel? = null,
    val type: TYPE = TYPE.CREATE
)

@HiltViewModel
class SubjectChangeViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun handleSubject() {
        _uiState.update {
            it.copy(
                status = STATUS.LOADING
            )
        }
        if (_uiState.value.type == TYPE.CREATE) {
            createSubject(_uiState.value.subject!!)
        } else {
            updateSubject(_uiState.value.subject!!)
        }
    }

    private fun createSubject(subject: SubjectModel) {
        viewModelScope.launch(Dispatchers.IO) {
            subjectRepository.addSubject(subject)
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

    private fun updateSubject(subject: SubjectModel) {
        viewModelScope.launch(Dispatchers.IO) {
            subjectRepository.updateSubject(subject)
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

    fun initData(subject: SubjectModel?) {
        if (subject != null) {
            _uiState.update {
                it.copy(
                    type = TYPE.UPDATE,
                    subject = subject
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    type = TYPE.CREATE,
                    subject = SubjectModel()
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(
                subject = it.subject?.copy(
                    name = name
                )
            )
        }
    }

    fun updateCreditDay(credit: Int) {
        _uiState.update {
            it.copy(
                subject = it.subject?.copy(
                    credit = credit
                )
            )
        }
    }

    fun updateDescription(description: String) {
        _uiState.update {
            it.copy(
                subject = it.subject?.copy(
                    description = description
                )
            )
        }
    }

    fun getType(): TYPE {
        return _uiState.value.type
    }

}