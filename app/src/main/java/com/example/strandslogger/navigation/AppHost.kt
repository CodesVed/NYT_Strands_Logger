package com.example.strandslogger.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.strandslogger.data.local.AppDatabase
import com.example.strandslogger.data.repository.SolveRepository
import com.example.strandslogger.navigation.Routes.*
import com.example.strandslogger.ui.addEntry.AddEntryScreen
import com.example.strandslogger.ui.addEntry.AddEntryViewModel
import com.example.strandslogger.ui.addEntry.AddEntryViewModelFactory
import com.example.strandslogger.ui.history.HistoryScreen
import com.example.strandslogger.ui.history.HistoryViewModel
import com.example.strandslogger.ui.history.HistoryViewModelFactory
import kotlin.collections.emptyList

@Composable
fun AppHost(context: Context) {
    val navController = rememberNavController()

    val database = AppDatabase.getInstance(context)
    val solveRepository = SolveRepository(database.getSolveDao())

    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(solveRepository)
    )
    val addEntryViewModel: AddEntryViewModel = viewModel(
        factory = AddEntryViewModelFactory(solveRepository)
    )

    val solves by historyViewModel.solves.collectAsState(emptyList())

    Scaffold(
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Navigation.Main,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation<Navigation.Main>(startDestination = History) {
                composable<History> {
                    HistoryScreen()
                }

                composable<AddEntry> {
                    AddEntryScreen()
                }
            }
        }
    }
}