package com.example.test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.BudgetDao
import com.example.test.data.dao.CategoryDao
import com.example.test.data.dao.TransactionDao
import com.example.test.model.Budget
import com.example.test.util.CurrencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetManagerViewModel @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val currencyFormatter: CurrencyFormatter
) : ViewModel() {

    val budgets = budgetDao.getAllBudgets().asLiveData()

    fun deleteBudget(id: Int) = viewModelScope.launch {
        budgetDao.deleteById(id)
    }

    suspend fun getCategoryName(id: Int?): String {
        return (if (id == null) "Overall" else categoryDao.getCategoryNameById(id)).toString()
    }

    suspend fun getRemainingBudget(budget: Budget): Double {
        val totalExpenses = transactionDao.getTotalExpensesForCategoryInDateRange(budget.categoryId, budget.startDate, budget.endDate) ?: 0.0
        return budget.amountLimit - totalExpenses
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
}