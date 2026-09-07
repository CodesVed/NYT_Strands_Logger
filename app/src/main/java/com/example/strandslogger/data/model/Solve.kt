package com.example.strandslogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solves")
data class Solve(
    @PrimaryKey val puzzleNumber: Int,
    val theme: String,
    val dateLogged: Long,
    val totalWords: Int,
    val hintsUsed: Int,
    val rawShareText: String
) {
    val isPerfect: Boolean = hintsUsed == 0
}