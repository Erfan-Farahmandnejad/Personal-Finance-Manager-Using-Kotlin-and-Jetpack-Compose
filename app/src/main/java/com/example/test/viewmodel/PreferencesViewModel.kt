package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.PreferencesDao
import com.example.test.data.datastore.AppDataStore
import com.example.test.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val dataStore: AppDataStore,
    private val preferencesDao: PreferencesDao
) : ViewModel() {

    val preferences = dataStore.readSettings().asLiveData()

    fun save(startDay: Int, notificationsEnabled: Boolean, selectedCurrency: String) {
        viewModelScope.launch {

            dataStore.saveSettings(
                UserPreferences(
                    startDayOfMonth = startDay,
                    notificationsEnabled = notificationsEnabled,
                    selectedCurrency = selectedCurrency
                )
            )
            

            preferencesDao.savePreferences(
                UserPreferences(
                    id = 1,
                    startDayOfMonth = startDay,
                    notificationsEnabled = notificationsEnabled,
                    selectedCurrency = selectedCurrency
                )
            )
        }
    }
}