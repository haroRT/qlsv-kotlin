package com.kma.qlsv.ui.screen.search_subject

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kma.qlsv.data.SubjectModel
import com.kma.qlsv.repository.normal.SubjectRepository
import com.kma.qlsv.ui.screen.student_change.STATUS
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
    val listSubject: List<SubjectModel> = emptyList(),
    val listSearch: List<SubjectModel> = emptyList()
)

@HiltViewModel
class SearchSubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
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
                    listSearch = it.listSubject.filter {
                        it.id.contains(keyword, ignoreCase = true) ||
                        it.name.contains(keyword, ignoreCase = true)
                    }
                )
            }
        }
    }

    fun getAllSubject(){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    status = STATUS.LOADING
                )
            }
            subjectRepository.getAllSubjects()
                .onSuccess {  list ->
                    _uiState.update {
                        it.copy(
                            status = STATUS.SUCCESS,
                            listSubject = list
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            status = STATUS.FAILURE,
                        )
                    }

                }
        }
    }

    fun deleteSubject(context: Context, subject: SubjectModel) {
        viewModelScope.launch(Dispatchers.IO) {
            subjectRepository.deleteSubject(subjectId = subject.id)
                .onSuccess {
                    getAllSubject()
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Xoá môn học thành công", Toast.LENGTH_LONG).show()
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
}