package com.example.strandslogger.ui.showDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strandslogger.data.model.Solve
import com.example.strandslogger.data.repository.SolveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolveDetailViewModel(
    private val repository: SolveRepository,
    private val puzzleNumber: Int
): ViewModel() {

    val solve: StateFlow<Solve?> = repository.getSolve(puzzleNumber)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _noteDraft = MutableStateFlow("")
    val noteDraft: StateFlow<String?> = _noteDraft.asStateFlow()

    private var seeded = false

    init {
        viewModelScope.launch {
            solve.collect { s->
                if (!seeded && s!=null) {
                    _noteDraft.value = s.notes.orEmpty()
                    seeded = true
                }
            }
        }
    }

    fun onNoteChanged(text: String) {
        _noteDraft.value = text
    }

    fun onSaveNoteClicked() {
        viewModelScope.launch {
            repository.updateNotes(puzzleNumber, _noteDraft.value.ifBlank { null })
        }
    }
}

class SolveDetailViewModelFactory(
    private val repository: SolveRepository,
    private val puzzleNumber: Int
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolveDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolveDetailViewModel(repository, puzzleNumber) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel class: ${modelClass.name}")
    }

}