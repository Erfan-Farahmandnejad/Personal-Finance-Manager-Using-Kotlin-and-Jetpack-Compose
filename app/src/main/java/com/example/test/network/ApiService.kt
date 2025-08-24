package com.example.test.network

import com.example.test.model.Account
import com.example.test.model.AuthResponse
import com.example.test.model.Budget
import com.example.test.model.Notification
import com.example.test.model.SignInRequest
import com.example.test.model.SignUpRequest
import com.example.test.model.Transaction
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Auth
    @POST("auth/signup")
    suspend fun signUp(@Body req: SignUpRequest): AuthResponse

    @POST("auth/signin")
    suspend fun signIn(@Body req: SignInRequest): AuthResponse

    @POST("sign out")
    suspend fun signOut(@Header("Authorization") token: String)


    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: Int)

    // Accounts
    @GET("accounts")
    suspend fun getAccounts(): List<Account>

    @POST("accounts")
    suspend fun createAccount(@Body account: Account)

    @DELETE("account/{id}")
    suspend fun deleteAccount(@Path("id") id: Int)

    // Transactions
    @GET("transactions")
    suspend fun getTransactions(): List<Transaction>

    @POST("transactions")
    suspend fun createTransaction(@Body transaction: Transaction)

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int)

    // Budgets
    @GET("budget")
    suspend fun getBudgets(): List<Budget>

    @POST("budget")
    suspend fun createBudget(@Body budget: Budget)

    @DELETE("budget/{id}")
    suspend fun deleteBudget(@Path("id") id: Int)


    @GET("notifications")
    suspend fun getNotifications(): List<Notification>
}