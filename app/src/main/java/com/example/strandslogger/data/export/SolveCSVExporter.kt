package com.example.strandslogger.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.strandslogger.data.model.Solve
import java.io.File
import java.time.format.DateTimeFormatter

object SolveCSVExporter {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    fun export(context: Context, solves: List<Solve>): Uri {
        val header = "Puzzle Number, Date, Theme, Total Words, Hints Used, Raw Share Text"
        val rows = solves.sortedBy { it.puzzleNumber }.joinToString("\n")  { solve ->
            listOf(
                solve.puzzleNumber.toString(),
                solve.puzzleDate.format(dateFormatter),
                escape(solve.theme),
                solve.totalWords.toString(),
                solve.hintsUsed.toString(),
                escape(solve.rawShareText)
            ).joinToString(",")
        }

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "strands_logger_export.csv")
        file.writeText("$header\n$rows")

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun escape(value: String): String {
        val needsQuoting = value.contains(",") || value.contains("\"") || value.contains("\n")
        val escaped = value.replace("\"", "\"\"")
        return if (needsQuoting) "\"$escaped\"" else escaped
    }
}