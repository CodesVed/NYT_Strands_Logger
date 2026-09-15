package com.example.strandslogger.data.parser

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.strandslogger.data.model.Solve

private val OPEN_QUOTES = setOf('"', '“')
private val CLOSE_QUOTES = setOf('"', '”')

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun parse(rawText: String): Solve? {
    val lines = rawText.trim().lines().map { it.trim() }
    if (lines.size < 3) return null

    val numberMatch = Regex("""Strands #(\d+)""").find(lines[0]) ?: return null
    val puzzleNumber = numberMatch.groupValues[1].toInt()

    val themeLine = lines[1]
    if (themeLine.length < 2 ||
        themeLine.first() !in OPEN_QUOTES ||
        themeLine.last() !in CLOSE_QUOTES
    ) return null
    val theme = themeLine.drop(1).dropLast(1)

    val emojiLines = lines.drop(2).joinToString("")
    val glyphs = emojiLines.codePoints().mapToObj { Character.toString(it) }.toList()
    val totalWords = glyphs.count { it == "🔵" || it == "🟡" }
    val hintsUsed = glyphs.count { it == "💡" }

    if (totalWords == 0) return null

    return Solve(
        puzzleNumber = puzzleNumber,
        theme = theme,
        totalWords = totalWords,
        hintsUsed = hintsUsed,
        glyphSequence = emojiLines,
        rawShareText = rawText,
        puzzleDateEpochDay = puzzleDateFor(puzzleNumber).toEpochDay()
    )
}