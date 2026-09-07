package com.example.strandslogger.data.repository

import com.example.strandslogger.data.local.SolveDao
import com.example.strandslogger.data.model.Solve
import kotlinx.coroutines.flow.Flow

class SolveRepository(private val solveDao: SolveDao) {

    fun getAllSolves(): Flow<List<Solve>> {
        return solveDao.getAllSolves()
    }

    suspend fun addSolve(solve: Solve) {
        solveDao.add(solve)
    }

    suspend fun editSolve(solve: Solve) {
        solveDao.update(solve)
    }

    suspend fun deleteSolve(solve: Solve) {
        solveDao.delete(solve.puzzleNumber)
    }

    suspend fun existsByPuzzleNumber(puzzleNumber: Int): Boolean {
        return solveDao.solveExistsByPuzzleNumber(puzzleNumber)
    }
}