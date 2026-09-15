package com.example.strandslogger.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Entity(tableName = "solves")
data class Solve(
    @PrimaryKey val puzzleNumber: Int,
    val theme: String,
    val totalWords: Int,
    val hintsUsed: Int,
    val glyphSequence: String,
    val rawShareText: String,
    @ColumnInfo(defaultValue = "0")
    val puzzleDateEpochDay: Long
) {
    val isPerfect: Boolean get() = hintsUsed == 0

    val puzzleDate: LocalDate get() = LocalDate.ofEpochDay(puzzleDateEpochDay)
    val formattedPuzzleDate: String
        get() = puzzleDate.format(uiDateFormatter)

    companion object {
        private val uiDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    }
}

