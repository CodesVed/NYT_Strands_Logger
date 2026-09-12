package com.example.strandslogger.data.parser

import com.example.strandslogger.data.model.Solve

fun parse(rawText: String): Solve? {
    val lines = rawText.trim().lines().map { it.trim() }
    if (lines.size < 3) return null

    val numberMatch = Regex("""Strands #(\d+)""").find(lines[0]) ?: return null
    val puzzleNumber = numberMatch.groupValues[1].toInt()

    val themeLines = lines[1]
    if (!themeLines.startsWith("\"") || !themeLines.endsWith("\"")) return null
    val theme = themeLines.removeSurrounding("\"")

    val emojiLines = lines.drop(2).joinToString("")
    val totalWords = emojiLines.windowed(2).count { it == "🔵" || it == "🟡" }
    val hintsUsed = emojiLines.windowed(2).count { it == "💡" }

    if (totalWords == 0) return null

    return Solve(
        puzzleNumber = puzzleNumber,
        theme = theme,
        totalWords = totalWords,
        hintsUsed = hintsUsed,
        glyphSequence = emojiLines,
        rawShareText = rawText
    )
}