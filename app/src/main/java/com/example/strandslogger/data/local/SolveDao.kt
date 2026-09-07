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
}