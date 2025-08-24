package com.example.test.viewmodel

// Import LiveData components
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
// Import DAO interfaces for database access
import com.example.test.data.dao.AccountDao
import com.example.test.data.dao.BudgetDao
import com.example.test.data.dao.CategoryDao
import com.example.test.data.dao.TransactionDao
// Import currency formatting utility
import com.example.test.util.CurrencyFormatter
// Import Hilt for dependency injection
import dagger.hilt.android.lifecycle.HiltViewModel
// Import Flow components for reactive streams
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
// Import coroutine components
import kotlinx.coroutines.launch
// Import for dependency injection
import javax.inject.Inject

// View model for the dashboard screen that handles financial data aggregation and formatting
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountDao: AccountDao,             // Data access for accounts
    private val transactionDao: TransactionDao,     // Data access for transactions
    private val categoryDao: CategoryDao,           // Data access for categories
    private val budgetDao: BudgetDao,               // Data access for budgets
    internal val currencyFormatter: CurrencyFormatter // Utility for formatting currency amounts
) : ViewModel() {

    // MutableLiveData to trigger refresh of dashboard data
    private val _refreshTrigger = MutableLiveData(0)
    
    // Observe all data sources from Room database
    val accounts = accountDao.getAllAccounts().asLiveData()         // All user accounts
    val transactions = transactionDao.getAllTransactions().asLiveData() // All financial transactions
    val categories = categoryDao.getAllCategories().asLiveData()     // All transaction categories
    val budgets = budgetDao.getAllBudgets().asLiveData()            // All budget entries

    // Calculate financial totals (income, expense, net) and update when refreshTrigger changes
    // Returns a Triple containing (income total, expense total, net balance)
    val totals: LiveData<Triple<Double, Double, Double>> = combine(
        transactionDao.getAllTransactions(),  // Stream of all transactions
        accountDao.getAllAccounts(),          // Stream of all accounts
        _refreshTrigger.asFlow()              // Refresh trigger to force updates
    ) { transactions, accounts, _ ->
        // Sum all income transactions
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        // Sum all expense transactions
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        // Calculate net balance (income minus expenses)
        val net = income - expense
        // Return the three values as a Triple
        Triple(income, expense, net)
    }.asLiveData()

    // Get most recent transactions for the dashboard display
    // Returns the 10 most recent transactions sorted by date (newest first)
    val recentTransactions = combine(
        transactionDao.getAllTransactions(),  // Stream of all transactions
        _refreshTrigger.asFlow()              // Refresh trigger to force updates
    ) { transactions, _ ->
        // Sort by date descending and take only the 10 most recent
        transactions.sortedByDescending { it.date }.take(10)
    }.asLiveData()
    
    // Format monetary amount with both primary and secondary currencies
    // For example: "$100 (10,000,000 IRR)" when USD is primary currency
    fun formatWithDualCurrency(amount: Double, currencyCode: String): String {
        return try {
            // Use the currency formatter to show both currencies
            currencyFormatter.formatWithDualCurrency(amount, currencyCode)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to simple formatting if there's an error
            CurrencyFormatter.formatAmount(amount, currencyCode)
        }
    }

    // Format amount specifically for dashboard cards with main and secondary values
    // Returns a Pair where:
    // - first = main currency amount (e.g., "$100")
    // - second = secondary currency amount (e.g., "10,000,000 IRR")
    fun formatForDashboard(amount: Double, currencyCode: String): Pair<String, String> {
        return try {
            // Use specialized dashboard formatter that separates the two currencies
            currencyFormatter.formatForDashboard(amount, currencyCode)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to simple formatting with empty secondary currency
            Pair(CurrencyFormatter.formatAmount(amount, currencyCode), "")
        }
    }

    // Trigger a refresh of the dashboard data by incrementing the refresh counter
    // This causes all LiveData streams to emit new values
    fun refreshDashboard() {
        _refreshTrigger.value = (_refreshTrigger.value ?: 0) + 1
    }

    // Force a refresh of the dashboard data using a coroutine
    // This is typically called when the user manually requests a refresh
    fun forceRefresh() {
        viewModelScope.launch {
            refreshDashboard()
        }
    }
}
