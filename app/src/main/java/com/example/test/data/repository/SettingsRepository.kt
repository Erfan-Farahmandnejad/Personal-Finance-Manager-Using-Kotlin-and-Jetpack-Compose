package com.example.test.data.repository

import com.example.test.data.datastore.AppDataStore
import com.example.test.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val dataStore: AppDataStore
) {
    fun getSettings(): Flow<UserPreferences> {
        return dataStore.readSettings()
    }

    suspend fun saveSettings(startDay: Int, notifications: Boolean) {
        dataStore.saveSettings(UserPreferences(1, startDay, notifications))
    }
}
