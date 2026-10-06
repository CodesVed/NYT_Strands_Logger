package com.example.strandslogger.ui.history

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.strandslogger.data.model.Solve

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    solves: List<Solve>,
    onSolveClick: (Int) -> Unit
) {
    LazyColumn {
        items(solves.sortedByDescending { it.puzzleNumber }) { solve ->
            SolveCard(
                viewModel = viewModel,
                solve = solve,
                onClick = { onSolveClick(solve.puzzleNumber) }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HistoryPreview() {
    HistoryScreen(
        viewModel = viewModel(),
        solves = emptyList(),
        onSolveClick = {}
    )
}