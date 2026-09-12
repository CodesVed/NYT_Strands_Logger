package com.example.strandslogger.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.strandslogger.data.model.Solve
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun  SolveCard(solve: Solve) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
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
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }

        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
            text = solve.dateLogged.toString(),
            fontSize = 14.sp
        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, top = 14.dp),
            text = "\"${solve.theme}\"",
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = solve.glyphSequence,
                fontSize = 18.sp,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.clickable(
                    onClick = {}
                ),
                text = "Details >",
                fontSize = 14.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SolveCardPreview() {
    SolveCard(
        solve = Solve(
            puzzleNumber = 1,
            theme = "Oceans",
            totalWords = 7,
            hintsUsed = 0,
            glyphSequence = "🔵🔵🔵🟡",
            rawShareText = ""
        )
    )
}