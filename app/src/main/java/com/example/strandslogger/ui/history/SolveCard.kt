package com.example.strandslogger.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toString
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.strandslogger.data.model.Solve
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun  SolveCard(
    viewModel: HistoryViewModel,
    solve: Solve
) {
    var solveDeleteConfirmation by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Strands #${solve.puzzleNumber}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )

            FilterChip(
                modifier = Modifier.padding(horizontal = 10.dp),
                selected = true,
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.Star,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = if (solve.isPerfect) "Perfect" else "Hint Solve",
                        fontSize = 14.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor =
                        if (solve.isPerfect) Color.Green.copy(alpha = 0.45f)
                        else Color.Yellow.copy(alpha = 0.5f),
                ),
                onClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                modifier = Modifier.weight(1f).size(28.dp),
                onClick = {
                    solveDeleteConfirmation = true
                }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        }

        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
            text = solve.formattedPuzzleDate,
            fontSize = 14.sp
        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, top = 14.dp),
            text = "\"${solve.theme}\"",
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic
        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = solve.glyphSequence,
            fontSize = 18.sp,
        )
    }

    if (solveDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { solveDeleteConfirmation = false },
            title = { Text(text = "Delete solve for Strands #${solve.puzzleNumber}?") },
            text = { Text(text = "This will recalculate your streak and remove solve telemetry.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSolve(solve)
                    solveDeleteConfirmation = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { solveDeleteConfirmation = false }) { Text("Cancel") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SolveCardPreview() {
    SolveCard(
        viewModel = viewModel(),
        solve = Solve(
            puzzleNumber = 1,
            theme = "Oceans",
            totalWords = 7,
            hintsUsed = 0,
            glyphSequence = "🔵🔵🔵🟡",
            rawShareText = "",
            puzzleDateEpochDay = 23
        )
    )
}