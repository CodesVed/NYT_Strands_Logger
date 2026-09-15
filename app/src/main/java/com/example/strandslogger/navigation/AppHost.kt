package com.example.strandslogger.navigation

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isOnHistory = currentRoute == History::class.qualifiedName

    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalActivity.current

    BackHandler(enabled = true) {
        if (isOnHistory) {
            showExitDialog = true
        } else {
            navController.goToHistory()
        }
    }

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
                        IconButton(onClick = { navController.goToHistory() }) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {
                            navController.navigate(AddEntry)
                            {launchSingleTop = true}
                        }) {
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
                        viewModel = historyViewModel,
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

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text(text = "Exit App?") },
                text = { Text(text = "Are you sure you want to exit Daily Perfect Logger?") },
                confirmButton = {
                    TextButton(onClick = {activity?.finish()}) {
                        Text(text = "Exit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }
}

fun NavHostController.goToHistory() {
    navigate(History) {
        popUpTo(History) { inclusive = false }
        launchSingleTop = true
    }
}