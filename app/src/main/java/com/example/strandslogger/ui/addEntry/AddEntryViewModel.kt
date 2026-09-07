package com.example.strandslogger.ui.addEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strandslogger.data.model.Solve
import com.example.strandslogger.data.parser.parse
import com.example.strandslogger.data.repository.SolveRepository
import com.example.strandslogger.ui.history.HistoryViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEntryUiState(
    val rawText: String = "",
    val parsedPreview: Solve? = null,
    val parseError: Boolean = false,
    val isSaving: Boolean = false
)

sealed interface AddEntryEvent {
    data object DuplicateFound: AddEntryEvent
    data object SaveSuccess: AddEntryEvent
}

class AddEntryViewModel(
    private val solveRepository: SolveRepository
): ViewModel() {

    val solves = solveRepository.getAllSolves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _uiState = MutableStateFlow(AddEntryUiState())
    val uiState: StateFlow<AddEntryUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddEntryEvent>()
    val events = _events.receiveAsFlow()

    fun onTextChanged(newText: String) {
        val parsed = parse(newText)
        _uiState.update {
            it.copy(
                rawText = newText,
                parsedPreview = parsed,
                parseError = newText.isNotBlank() && parsed == null
            )
        }
    }

    fun onSaveClicked() {
        val solve = _uiState.value.parsedPreview ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val alreadyExists = solveRepository.existsByPuzzleNumber(solve.puzzleNumber)
            if (alreadyExists) {
                _events.send(AddEntryEvent.DuplicateFound)
            } else {
                solveRepository.addSolve(solve)
                _events.send(AddEntryEvent.SaveSuccess)
            }

            _uiState.update { it.copy(isSaving = false) }
        }
    }
}

class AddEntryViewModelFactory(
    private val solveRepository: SolveRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HistoryViewModel(solveRepository) as T
    }
}