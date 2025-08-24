package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.AccountDao
import com.example.test.data.dao.CategoryDao
import com.example.test.data.dao.TransactionDao
import com.example.test.model.Transaction
import com.example.test.util.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val currencyFormatter: CurrencyFormatter
) : ViewModel() {

    val transactions = transactionDao.getAllTransactions().asLiveData()
    
    // Event to notify that a transaction was deleted
    private val _transactionDeleted = MutableStateFlow(false)

    fun deleteTransaction(id: Int, onDashboardRefreshNeeded: () -> Unit = {}) = viewModelScope.launch {
        val transaction = transactionDao.getTransactionById(id)
        
        transaction?.let {
            val amountInUSD = it.amount

            when (it.type) {
                "expense" -> {
                    it.fromAccountId?.let { accountId ->
                        val account = accountDao.getAccountById(accountId)
                        account?.let { acc ->
                            val newBalance = acc.balance + amountInUSD
                            accountDao.insert(acc.copy(balance = newBalance))
                        }
                    }
                }
                "income" -> {
                    it.toAccountId?.let { accountId ->
                        val account = accountDao.getAccountById(accountId)
                        account?.let { acc ->
                            val newBalance = acc.balance - amountInUSD
                            accountDao.insert(acc.copy(balance = newBalance))
                        }
                    }
                }
                "transfer" -> {
                    it.fromAccountId?.let { accountId ->
                        val account = accountDao.getAccountById(accountId)
                        account?.let { acc ->
                            val newBalance = acc.balance + amountInUSD
                            accountDao.insert(acc.copy(balance = newBalance))
                        }
                    }
                    it.toAccountId?.let { accountId ->
                        val account = accountDao.getAccountById(accountId)
                        account?.let { acc ->
                            val newBalance = acc.balance - amountInUSD
                            accountDao.insert(acc.copy(balance = newBalance))
                        }
                    }
                }
                else -> {
                    // No action needed
                }
            }
        }

        transactionDao.deleteById(id)
        
        // Notify that dashboard needs to be refreshed
        _transactionDeleted.value = !_transactionDeleted.value
        onDashboardRefreshNeeded()
    }

    suspend fun getCategoryName(id: Int?): String {
        return if (id == null) "Uncategorized" else categoryDao.getCategoryNameById(id) ?: "Unknown"
    }

    suspend fun getAccountName(id: Int?): String {
        return if (id == null) "N/A" else accountDao.getAccountById(id)?.name ?: "Unknown"
    }

    fun formatWithDualCurrency(amount: Double, currencyCode: String): String {
        return try {
            currencyFormatter.formatWithDualCurrency(amount, currencyCode)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to simple formatting
            CurrencyFormatter.formatAmount(amount, currencyCode)
        }
    }

    suspend fun getTransactionDetails(transaction: Transaction): Map<String, String> {
        val categoryName = getCategoryName(transaction.categoryId)
        val fromAccountName = getAccountName(transaction.fromAccountId)
        val toAccountName = getAccountName(transaction.toAccountId)

        return mapOf(
            "Category" to categoryName,
            "From Account" to fromAccountName,
            "To Account" to toAccountName,
            "Date" to transaction.date,
            "Note" to transaction.note
        )
    }
}
