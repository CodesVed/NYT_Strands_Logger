package com.example.strandslogger.data.model

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
    val rawShareText: String
) {
    val isPerfect: Boolean get() = hintsUsed == 0

    val dateLogged: String? get() {
        val instant = Instant.ofEpochMilli(System.currentTimeMillis())

        val zoneId = ZoneId.systemDefault()
        val zoneDateTime = instant.atZone(zoneId)

        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")
        val formattedDate = zoneDateTime.format(formatter)

        return formattedDate
    }
}

