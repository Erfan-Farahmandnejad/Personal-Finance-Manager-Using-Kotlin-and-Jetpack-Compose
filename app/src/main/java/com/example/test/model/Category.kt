package com.example.test.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: CategoryType,
    val color: String = "#000000"
)

enum class CategoryType {
    EXPENSE, INCOME
}

