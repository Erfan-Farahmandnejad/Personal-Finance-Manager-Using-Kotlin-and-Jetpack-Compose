package com.example.test.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.test.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object interface for Transaction entities
 * Provides methods to perform database operations on transaction records
 */
@Dao
interface TransactionDao {
    /**
     * Retrieves all transactions ordered by date (newest first)
     * Returns a Flow that emits updates whenever the underlying data changes
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Retrieves a specific transaction by its ID
     * @param id The unique identifier of the transaction
     * @return The transaction if found, null otherwise
     */
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    /**
     * Calculates the total amount spent in a specific category within a date range
     * @param categoryId The category to filter by (can be null)
     * @param startDate Beginning of the date range in string format
     * @param endDate End of the date range in string format
     * @return Sum of expense transactions amount or null if no matching transactions
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpensesForCategoryInDateRange(categoryId: Int?, startDate: String, endDate: String): Double?

    /**
     * Inserts a new transaction or replaces an existing one
     * @param transaction The transaction entity to save
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    /**
     * Deletes a transaction from the database
     * @param transaction The transaction entity to delete
     */
    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * Deletes a transaction by its ID
     * @param id The unique identifier of the transaction to delete
     */
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Int)
}