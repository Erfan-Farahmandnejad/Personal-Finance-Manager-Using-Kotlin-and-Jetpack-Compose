package com.example.test.data.repository

import com.example.test.data.dao.UserDao
import com.example.test.data.datastore.UserDataStore
import com.example.test.model.SignInRequest
import com.example.test.model.SignUpRequest
import com.example.test.model.User
import com.example.test.network.ApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: UserDataStore,
    private val userDao: UserDao
) {
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {

            try {
                val response = apiService.signIn(SignInRequest(email, password))
                dataStore.saveAuthToken(response.token)
                return Result.success(Unit)
            } catch (e: Exception) {

                val user = userDao.getUserByEmail(email)
                if (user != null && user.password == password) {

                    dataStore.saveAuthToken("local_token_${user.id}")
                    return Result.success(Unit)
                }
                return Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(username: String, email: String, password: String): Result<Unit> {
        return try {
            // First save the auth token if API call succeeds
            try {
                val response = apiService.signUp(SignUpRequest(username, email, password))
                dataStore.saveAuthToken(response.token)
                
                // Then try to save to local database, but don't fail if this fails
                try {
                    val user = User(
                        username = username,
                        email = email,
                        password = password
                    )
                    userDao.insert(user)
                } catch (dbException: Exception) {
                    // Log but don't fail the signup if local DB fails
                    // The user is already registered with the API
                }
                
                return Result.success(Unit)
            } catch (apiException: Exception) {
                // API call failed, try local signup
                try {
                    val user = User(
                        username = username,
                        email = email,
                        password = password
                    )
                    userDao.insert(user)
                    dataStore.saveAuthToken("local_token_${user.id}")
                    return Result.success(Unit)
                } catch (dbException: Exception) {
                    // Both API and local DB failed
                    return Result.failure(Exception("Failed to sign up: ${apiException.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}