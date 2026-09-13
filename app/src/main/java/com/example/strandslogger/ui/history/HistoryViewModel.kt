package com.example.strandslogger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strandslogger.data.model.Solve
import com.example.strandslogger.data.repository.SolveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
private val solveRepository: SolveRepository
): ViewModel() {

    val solves: Flow<List<Solve>> = solveRepository.getAllSolves()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun deleteSolve(solve: Solve) {
        viewModelScope.launch {
            solveRepository.deleteSolve(solve)
        }
    }

    fun editSolve(solve: Solve) {
        viewModelScope.launch {
            solveRepository.editSolve(solve)
        }
    }
}

class HistoryViewModelFactory(
    private val solveRepository: SolveRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(solveRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}