package com.example.test.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.test.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class AppDataStore @Inject constructor(
    private val context: Context
) {

    companion object {
        private val START_DAY_KEY = intPreferencesKey("start_day_of_month")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val SELECTED_CURRENCY_KEY = stringPreferencesKey("selected_currency")
    }

    suspend fun saveSettings(preferences: UserPreferences) {
        context.dataStore.edit { prefs ->
            prefs[START_DAY_KEY] = preferences.startDayOfMonth
            prefs[NOTIFICATIONS_ENABLED_KEY] = preferences.notificationsEnabled
            prefs[SELECTED_CURRENCY_KEY] = preferences.selectedCurrency
        }
    }

    fun readSettings(): Flow<UserPreferences> {
        return context.dataStore.data.map { prefs ->
            UserPreferences(
                id = 1,
                startDayOfMonth = prefs[START_DAY_KEY] ?: 1,
                notificationsEnabled = prefs[NOTIFICATIONS_ENABLED_KEY] ?: true,
                selectedCurrency = prefs[SELECTED_CURRENCY_KEY] ?: "USD"
            )
        }
    }
}