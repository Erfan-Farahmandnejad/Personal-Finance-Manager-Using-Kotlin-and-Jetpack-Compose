package com.example.test.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.test.model.UserPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1")
    fun getPreferences(): Flow<UserPreferences?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(prefs: UserPreferences)
}
