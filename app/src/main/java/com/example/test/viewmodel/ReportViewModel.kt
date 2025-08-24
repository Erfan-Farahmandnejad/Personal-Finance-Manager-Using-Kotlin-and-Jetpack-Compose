package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.TransactionDao
import com.example.test.data.dao.CategoryDao
import com.example.test.data.dao.AccountDao
import com.example.test.model.Category
import com.example.test.util.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val currencyFormatter: CurrencyFormatter
) : ViewModel() {

    private val transactionsFlow = transactionDao.getAllTransactions()
    private val categoriesFlow = categoryDao.getAllCategories()
    private val accountsFlow = accountDao.getAllAccounts()

    val summary = combine(
        transactionsFlow,
        accountsFlow
    ) { transactions, accounts ->
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val net = accounts.sumOf { it.balance }  // Net balance is the sum of all account balances
        Triple(income, expense, net)
    }.asLiveData()

    val categoryBreakdown = transactionsFlow
        .map { txs ->
            val expensesByCategory = txs.filter { it.type == "expense" }
                .groupBy { it.categoryId ?: -1 }
                .mapValues { entry -> entry.value.sumOf { tx -> tx.amount } }

            val incomeByCategory = txs.filter { it.type == "income" }
                .groupBy { it.categoryId ?: -1 }
                .mapValues { entry -> entry.value.sumOf { tx -> tx.amount } }

            val expenseEntries = expensesByCategory.map { (categoryId, amount) ->
                Triple(categoryId, amount, "expense")
            }
            val incomeEntries = incomeByCategory.map { (categoryId, amount) ->
                Triple(categoryId, amount, "income")
            }
            expenseEntries + incomeEntries
        }
        .combine(categoriesFlow) { categoryEntries, categories ->
            categoryEntries.map { (categoryId, amount, type) ->
                val category = when {
                    categoryId == -1 -> null
                    else -> categories.find { it.id == categoryId }
                }
                Triple(
                    category?.name ?: "Uncategorized",
                    amount,
                    type
                )
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    val monthlySpending = transactionsFlow.map { txs ->
        txs.filter { it.type == "expense" }
            .groupBy { it.date.substring(0, 7) }
            .mapValues { it.value.sumOf { tx -> tx.amount } }
    }.asLiveData()


    fun formatCurrency(amount: Double, currencyCode: String): String {
        return currencyFormatter.formatAmount(amount, currencyCode)
    }
}
