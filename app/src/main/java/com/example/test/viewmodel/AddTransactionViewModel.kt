package com.example.test.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.AccountDao
import com.example.test.data.dao.CategoryDao
import com.example.test.data.dao.TransactionDao
import com.example.test.model.Account
import com.example.test.model.CategoryType
import com.example.test.model.Transaction
import com.example.test.service.BudgetNotificationService
import com.example.test.util.DateConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val budgetNotificationService: BudgetNotificationService
) : ViewModel() {

    private val _type = mutableStateOf("expense")
    private val _typeFlow = MutableStateFlow("expense")
    private val _budgetAlerts = mutableStateOf<List<BudgetNotificationService.BudgetAlert>>(emptyList())

    var type: String
        get() = _type.value
        set(value) {
            _type.value = value
            _typeFlow.value = value
            categoryId = null // Reset categoryId when type changes
        }

    var categoryId: Int? by mutableStateOf(null)
    var amount: String by mutableStateOf("")
    var date: String by mutableStateOf(LocalDate.now().toString())
    var fromAccountId: Int? by mutableStateOf(null)
    var toAccountId: Int? by mutableStateOf(null)
    var accountId: Int? by mutableStateOf(null) // For income/expense
    var note: String by mutableStateOf("")
    private var currentTransactionId: Int? = null

    // Event to notify that a transaction was saved
    private val _transactionSaved = MutableStateFlow(false)
    val transactionSaved: Flow<Boolean> = _transactionSaved

    init {
        _typeFlow.value = _type.value
    }

    /**
     * Sets today's date according to the calendar type
     * @param calendarType The calendar type ("GREGORIAN" or "PERSIAN")
     */
    fun setTodayDate(calendarType: String) {
        val today = LocalDate.now()
        date = if (calendarType == "PERSIAN") {
            // Get today's date in Persian calendar formatted as YYYY-MM-DD
            val persianDate = DateConverter.gregorianToPersian(today)
            // Format it as YYYY-MM-DD
            "${persianDate.year}-${persianDate.month.toString().padStart(2, '0')}-${persianDate.day.toString().padStart(2, '0')}"
        } else {
            // Use Gregorian date directly
            today.toString()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val categories = _typeFlow.flatMapLatest { currentType ->
        categoryDao.getAllCategories().map { allCategories ->
            when (currentType) {
                "income" -> allCategories.filter { it.type == CategoryType.INCOME }
                "expense" -> allCategories.filter { it.type == CategoryType.EXPENSE }
                else -> emptyList()
            }
        }
    }.asLiveData()

    val accounts = accountDao.getAllAccounts().asLiveData()
    val budgetAlerts = _budgetAlerts

    fun loadTransaction(transactionId: Int) {
        viewModelScope.launch {
            val transaction = transactionDao.getTransactionById(transactionId)
            transaction?.let {
                currentTransactionId = it.id
                type = it.type
                categoryId = it.categoryId
                amount = it.amount.toString()
                date = it.date
                fromAccountId = it.fromAccountId
                toAccountId = it.toAccountId
                accountId = when (it.type) {
                    "expense" -> it.fromAccountId
                    "income" -> it.toAccountId
                    "transfer" -> null
                    else -> null
                }
                note = it.note
            }
        }
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            val amountValue = amount.toDoubleOrNull()
            if (amountValue == null) {
                return@launch
            }

            // Get current account balances
            val fromAccount = fromAccountId?.let { accountDao.getAccountById(it) }
            val toAccount = toAccountId?.let { accountDao.getAccountById(it) }
            val account = accountId?.let { accountDao.getAccountById(it) }

            // Convert amount to USD for storage (since all amounts are stored in USD)
            // For IRR (Rial), divide by 1,000,000 to get USD correctly
            val amountInUSD = amountValue / (if (type == "IRR") 1000000.0 else 1.0)

            // Update account balances based on transaction type
            when (type) {
                "expense" -> {
                    account?.let {
                        val newBalance = it.balance - amountInUSD
                        accountDao.insert(it.copy(balance = newBalance))
                    }
                }
                "income" -> {
                    account?.let {
                        val newBalance = it.balance + amountInUSD
                        accountDao.insert(it.copy(balance = newBalance))
                    }
                }
                "transfer" -> {
                    fromAccount?.let {
                        val newBalance = it.balance - amountInUSD
                        accountDao.insert(it.copy(balance = newBalance))
                    }
                    toAccount?.let {
                        val newBalance = it.balance + amountInUSD
                        accountDao.insert(it.copy(balance = newBalance))
                    }
                }
            }

            val transaction = Transaction(
                id = currentTransactionId ?: 0,
                type = type,
                categoryId = categoryId,
                amount = amountInUSD,  // Store amount in USD
                date = date,
                fromAccountId = when (type) {
                    "expense" -> accountId
                    "transfer" -> fromAccountId
                    else -> null
                },
                toAccountId = when (type) {
                    "income" -> accountId
                    "transfer" -> toAccountId
                    else -> null
                },
                note = note
            )

            transactionDao.insert(transaction)

            // Emit event that transaction was saved
            _transactionSaved.value = true

            // Check budget thresholds only for the budget associated with the current transaction's category
            if (type == "expense") {
                val alerts = budgetNotificationService.checkBudgetThresholds(categoryId)
                if (alerts.isNotEmpty()) {
                    _budgetAlerts.value = alerts
                }
            }

            onDone()
        }
    }

    fun clearBudgetAlerts() {
        _budgetAlerts.value = emptyList()
    }
}
