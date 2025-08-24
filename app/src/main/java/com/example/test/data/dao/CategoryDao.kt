package com.example.test.data.dao


import androidx.room.*
import com.example.test.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name")
    fun getCategoriesByType(type: String): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getCategoryByName(name: String): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT name FROM categories WHERE id = :id")
    suspend fun getCategoryNameById(id: Int): String?
}
