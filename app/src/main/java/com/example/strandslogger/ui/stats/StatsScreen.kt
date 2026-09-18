package com.example.strandslogger.ui.stats

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextAlign

private val StrandsBlue = Color(0xFF1450A3)
private val SpangramGold = Color(0xFFB8860B)

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is StatsEvent.ExportReady -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, event.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Export Solve History"))
                }
                is StatsEvent.ExportFailed -> { /* wire to a Snackbar once one exists at Scaffold level */ }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = "${state.totalLogged}",
                    style = MaterialTheme.typography.displayMedium,
                    color = StrandsBlue
                )
                Text(
                    text = "Perfect Solves Logged",
                    style = MaterialTheme.typography.labelLarge
                )
                state.loggingSince?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Since ${it.format(DateTimeFormatter.ofPattern("MMMM yyyy"))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Current Streak",
                        value = "${state.currentStreak}",
                        unit = "Days",
                        accent = SpangramGold
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Longest Streak",
                        value = "${state.longestStreak}",
                        unit = "Days",
                        accent = StrandsBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("PERFORMANCE BREAKDOWN", style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(8.dp))
                ElevatedCard {
                    Column {
                        state.averageWordsPerPuzzle?.let {
                            BreakdownRow(
                                leadingIcon = Icons.Outlined.SortByAlpha,
                                label = "Average words per puzzle",
                                value = "%.1f".format(it)
                            )
                            HorizontalDivider()
                        }
                        state.biggestPuzzle?.let {
                            BreakdownRow(
                                leadingIcon = Icons.Outlined.Stars,
                                label = "Biggest puzzle solved",
                                value = "${it.totalWords} words — #${it.puzzleNumber}"
                            )
                            HorizontalDivider()
                        }
                        state.mostActiveWeekDay?.let {
                            BreakdownRow(
                                leadingIcon = Icons.Default.CalendarToday,
                                label = "Most active day",
                                value = it.getDisplayName(TextStyle.FULL, LocalLocale.current.platformLocale)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = viewModel::onExportClicked,
                    enabled = !state.isExporting && state.totalLogged > 0,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(if (state.isExporting) "Preparing export…" else "Export Data (CSV)")
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    unit: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    color = accent
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, leadingIcon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = leadingIcon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}