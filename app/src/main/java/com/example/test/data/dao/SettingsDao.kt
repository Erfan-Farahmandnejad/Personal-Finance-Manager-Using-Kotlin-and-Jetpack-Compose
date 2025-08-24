package com.example.test.data.dao

import androidx.room.*
import com.example.test.model.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<Settings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: Settings)

    @Update
    suspend fun update(settings: Settings)
} 