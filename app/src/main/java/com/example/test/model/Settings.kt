package com.example.test.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,
    val firstDayOfMonth: Int = 1, // 1-31
    val currency: String = "USD",
    val notificationsEnabled: Boolean = true,
    val calendarType: String = "GREGORIAN" // GREGORIAN or PERSIAN
) 