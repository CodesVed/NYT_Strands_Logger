package com.example.strandslogger.data.parser

import java.time.LocalDate

private val ANCHOR_PUZZLE_NUMBER = 424
private val ANCHOR_DATE: LocalDate = LocalDate.of(2025, 5, 1)

fun puzzleDateFor (puzzleNumber: Int): LocalDate =
    ANCHOR_DATE.plusDays((puzzleNumber - ANCHOR_PUZZLE_NUMBER).toLong())