package com.example.strandslogger.ui.stats

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.strandslogger.data.export.SolveCSVExporter
import com.example.strandslogger.data.model.Solve
import com.example.strandslogger.data.repository.SolveRepository
import com.example.strandslogger.data.stats.StreakCalculator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

data class StatsUiState(
    val totalLogged: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val loggingSince: LocalDate? = null,
    val averageWordsPerPuzzle: Double? = null,
    val biggestPuzzle: Solve? = null,
    val mostActiveWeekDay: DayOfWeek? = null,
    val isExporting: Boolean = false
)

sealed interface StatsEvent {
    data class ExportReady(val uri: Uri): StatsEvent
    data object ExportFailed: StatsEvent
}

class StatsViewModel(
    private val solveRepository: SolveRepository,
    private val appContext: Context
): ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val _events = Channel<StatsEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            solveRepository.getAllSolves().collect { solves ->
                val dates = solves.map { it.puzzleDate }

                _uiState.update {
                    it.copy(
                        totalLogged = solves.size,
                        currentStreak = StreakCalculator.currentStreak(dates),
                        longestStreak = StreakCalculator.longestStreak(dates),
                        loggingSince = dates.minOrNull(),
                        averageWordsPerPuzzle = if (solves.isEmpty()) null else solves.map { it.totalWords }.average(),
                        biggestPuzzle = solves.maxByOrNull { it.totalWords },
                        mostActiveWeekDay = StreakCalculator.mostActiveWeekday(dates)
                    )
                }
            }
        }
    }

    fun onExportClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }

            try {
                val solves = solveRepository.getAllSolvesOnce()
                val uri = SolveCSVExporter.export(appContext, solves)
                _events.send(StatsEvent.ExportReady(uri))
            } catch (e: Exception) {
                _events.send(StatsEvent.ExportFailed)
            } finally {
                _uiState.update { it.copy(isExporting = false) }
            }
        }
    }
}

class StatsViewModelFactory(
    private val solveRepository: SolveRepository,
    context: Context
): ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(solveRepository, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}