package com.example.strandslogger.ui.history

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.strandslogger.data.model.Solve

@Composable
fun HistoryScreen(
    solves: List<Solve>
) {
    LazyColumn {
        items(solves) { solve ->
            SolveCard(solve)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HistoryPreview() {
    HistoryScreen(
        solves = emptyList()
    )
}