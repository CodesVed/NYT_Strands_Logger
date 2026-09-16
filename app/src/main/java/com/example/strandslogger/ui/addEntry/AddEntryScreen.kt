package com.example.strandslogger.ui.addEntry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

private val StrandsBlue = Color(0xFF1450A3)
private val SpangramGold = Color(0xFFB8860B)

@Composable
fun AddEntryScreen(
    viewModel: AddEntryViewModel,
    onSaveCompleted: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current
    var showDuplicateDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddEntryEvent.SaveSuccess -> onSaveCompleted()
                is AddEntryEvent.DuplicateFound -> showDuplicateDialog = true
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
        // ---- Share Text Input card ----
        ElevatedCard {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Share Text Input", style = MaterialTheme.typography.labelLarge)
                    TextButton(onClick = {
                        scope.launch {
                            val pastedText = clipboard.getClipEntry()?.clipData?.getItemAt(0)?.text?.toString() ?: ""
                            viewModel.onTextChanged(pastedText)
                        }
                    }) {
                        Text("Paste Clipboard")
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.rawText,
                    onValueChange = viewModel::onTextChanged,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    placeholder = { Text("Paste your Strands result here…") }
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when {
                            state.parsedPreview != null -> "✓ Format recognized"
                            state.parseError -> "Format not recognized"
                            else -> ""
                        },
                        color = if (state.parsedPreview != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = { viewModel.onTextChanged("") }) { Text("Clear") }
                }
            }
        }

        // ---- Parsed Result Preview card ----
        state.parsedPreview?.let { solve ->
            ElevatedCard {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Parsed Result Preview", style = MaterialTheme.typography.titleMedium)
                        if (solve.isPerfect) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Perfect", color = Color.White) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF1B5E20))
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoBlock("Date", solve.puzzleDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")), Modifier.weight(1f))
                        InfoBlock("Puzzle Edition", "Strands #${solve.puzzleNumber}", Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Theme Title", "\"${solve.theme}\"")

                    Spacer(Modifier.height(12.dp))
                    InfoBlock(
                        "Hints Detected",
                        if (solve.hintsUsed == 0) "0 Hints (No Lightbulbs)" else "${solve.hintsUsed} Hints"
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Glyph Sequence", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        extractGlyphs(solve.rawShareText).forEach { glyph ->
                            GlyphDot(
                                color = when (glyph) {
                                    "🟡" -> SpangramGold
//                                    "💡" -> HintColor // Maps the lightbulb icon accurately if needed
                                    else -> StrandsBlue
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            onClick = viewModel::onSaveClicked,
            enabled = state.parsedPreview != null && !state.isSaving,
        ) {
            Icon(imageVector = Icons.Outlined.Save, contentDescription = "Save")
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = if (state.isSaving) "Saving…" else "Save Solve")
        }
        OutlinedButton(
            onClick = onSaveCompleted,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Cancel")
        }

        if (showDuplicateDialog) {
            AlertDialog(
                onDismissRequest = { showDuplicateDialog = false },
                title = { Text("Already Logged") },
                text = { Text("This puzzle number is already in your history.") },
                confirmButton = {
                    TextButton(onClick = { showDuplicateDialog = false }) { Text("OK") }
                }
            )
        }
    }
}

private fun extractGlyphs(rawShareText: String): List<String> {
    val targetGlyphs = setOf("🔵", "🟡", "💡")
    return rawShareText.lines()
        .drop(2)
        .joinToString("")
        .codePoints()
        .mapToObj { Character.toString(it) }
        .filter { it in targetGlyphs }
        .collect(Collectors.toList())
}

@Composable
private fun InfoBlock(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun GlyphDot(color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Preview(showBackground = true)
@Composable
fun AddEntryPreview() {
    AddEntryScreen(
        viewModel = viewModel(),
        onSaveCompleted = {}
    )
}

