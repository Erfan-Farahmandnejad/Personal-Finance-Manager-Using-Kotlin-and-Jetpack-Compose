package com.example.test.data.dao

import androidx.room.*
import com.example.test.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY startDate DESC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Int): Budget?

    @Query("""
        SELECT * FROM budgets 
        WHERE categoryId = :categoryId 
        AND (
            (startDate <= :startDate AND endDate >= :startDate)
            OR (startDate <= :endDate AND endDate >= :endDate)
            OR (startDate >= :startDate AND endDate <= :endDate)
        )
    """)
    suspend fun getOverlappingBudgets(categoryId: Int?, startDate: String, endDate: String): List<Budget>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Int)
}