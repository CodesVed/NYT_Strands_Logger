package com.example.strandslogger.navigation

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHost(context: Context) {
    val navController = rememberNavController()

    val database = AppDatabase.getInstance(context)
    val solveRepository = SolveRepository(database.getSolveDao())


    Scaffold(
        topBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val titleText: String? = when (backStackEntry?.destination?.route) {
                History::class.qualifiedName -> "Solve History"
                AddEntry::class.qualifiedName -> "Log Today's Solve"
                else -> null
            }

            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.goToHistory()     //TODO: to be replaced by Home
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    Text(text = titleText.toString())
                },
                actions = {
                    Row {
                        IconButton(onClick = { navController.navigate(History) }) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = { navController.navigate(AddEntry) }) {
                            Icon(
                                imageVector = Icons.Default.PostAdd,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Navigation.Main,
            modifier = Modifier.padding(innerPadding)
        ) {
            navigation<Navigation.Main>(startDestination = History) {

                composable<History> { backStackEntry ->
                    val historyViewModel: HistoryViewModel = viewModel(
                        viewModelStoreOwner = backStackEntry,
                        factory = HistoryViewModelFactory(solveRepository)
                    )
                    val solves by historyViewModel.solves.collectAsState(emptyList())

                    HistoryScreen(
                        solves = solves
                    )
                }

                composable<AddEntry> { backStackEntry ->
                    val addEntryViewModel: AddEntryViewModel = viewModel(
                        viewModelStoreOwner = backStackEntry,
                        factory = AddEntryViewModelFactory(solveRepository)
                    )

                    AddEntryScreen(
                        viewModel = addEntryViewModel,
                        onSaveCompleted = { navController.goToHistory() }
                    )
                }
            }
        }
    }
}

fun NavHostController.goToHistory() {
    navigate(History) {
        popUpTo(History) { inclusive = false }
        launchSingleTop = true
    }
}