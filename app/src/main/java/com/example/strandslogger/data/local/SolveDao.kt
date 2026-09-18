package com.example.strandslogger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.strandslogger.data.model.Solve
import kotlinx.coroutines.flow.Flow

@Dao
interface SolveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(solve: Solve)

    @Update
    suspend fun update(solve: Solve)

    @Query("DELETE FROM solves WHERE puzzleNumber = :puzzleNumber")
    suspend fun delete(puzzleNumber: Int)

    @Query("SELECT * FROM solves")
    fun getAllSolves(): Flow<List<Solve>>

    @Query("SELECT EXISTS(SELECT 1 FROM solves WHERE puzzleNumber = :puzzleNumber)")
    suspend fun solveExistsByPuzzleNumber(puzzleNumber: Int): Boolean

    @Query("SELECT MIN(puzzleDateEpochDay) FROM solves")
    suspend fun getFirstLoggedEpochDay(): Long?

    @Query("SELECT AVG(totalWords) FROM solves")
    suspend fun getAverageWordsPerPuzzle(): Double?

    @Query("SELECT * FROM solves ORDER BY totalWords DESC LIMIT 1")
    suspend fun getBiggestPuzzleSolved(): Solve?

    @Query("SELECT * FROM solves ORDER BY puzzleDateEpochDay ASC")
    suspend fun getAllSolvesOnce(): List<Solve>
}