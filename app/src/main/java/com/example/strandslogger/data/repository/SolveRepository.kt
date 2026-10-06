package com.example.strandslogger.data.repository

import com.example.strandslogger.data.local.SolveDao
import com.example.strandslogger.data.model.Solve
import kotlinx.coroutines.flow.Flow

class SolveRepository(private val solveDao: SolveDao) {

    fun getAllSolves(): Flow<List<Solve>> {
        return solveDao.getAllSolves()
    }

    suspend fun getAllSolvesOnce(): List<Solve> {
        return solveDao.getAllSolvesOnce()
    }

    fun getSolve(puzzleNumber: Int): Flow<Solve?> {
        return solveDao.getByPuzzleNumber(puzzleNumber)
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

    suspend fun updateNotes(puzzleNumber: Int, notes: String?) {
        return solveDao.updateNotes(puzzleNumber, notes)
    }
}