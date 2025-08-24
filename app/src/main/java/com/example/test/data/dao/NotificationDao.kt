package com.example.test.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object interface for Notification entities
 * Provides methods to perform database operations on notification records
 */
@Dao
interface NotificationDao {
    /**
     * Retrieves all notifications ordered by date (newest first)
     * Returns a Flow that emits updates whenever the underlying data changes
     */
    @Query("SELECT * FROM notifications ORDER BY date DESC")
    fun getAll(): Flow<List<Notification>>

    /**
     * Inserts a new notification into the database
     * @param notification The notification entity to save
     */
    @Insert
    suspend fun insert(notification: Notification)

    /**
     * Marks a notification as read by updating its read flag
     * @param id The unique identifier of the notification to update
     */
    @Query("UPDATE notifications SET read = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    /**
     * Deletes all notifications from the database
     * Used to clear notification history
     */
    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
