package com.example.test.data.dao

import androidx.room.*
import com.example.test.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Int)
} 