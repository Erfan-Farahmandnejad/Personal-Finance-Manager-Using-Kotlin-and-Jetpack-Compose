package com.example.test.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val startDayOfMonth: Int = 1,
    val notificationsEnabled: Boolean = true,
    val selectedCurrency: String = "USD"
)