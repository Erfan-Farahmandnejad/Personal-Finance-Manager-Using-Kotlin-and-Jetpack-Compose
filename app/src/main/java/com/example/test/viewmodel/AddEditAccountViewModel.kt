package com.example.test.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.AccountDao
import com.example.test.model.Account
import com.example.test.model.AccountType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAccountViewModel @Inject constructor(
    private val accountDao: AccountDao
) : ViewModel() {

    var name by mutableStateOf("")
    var balance by mutableStateOf("")
    var type by mutableStateOf(AccountType.BANK)
    private var currentAccountId: Int? = null

    fun loadAccount(accountId: Int) {
        viewModelScope.launch {
            val account = accountDao.getAccountById(accountId)
            account?.let {
                currentAccountId = it.id
                name = it.name
                balance = it.balance.toString()
                type = it.type
            }
        }
    }

    fun saveAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            val account = Account(
                id = currentAccountId ?: 0,
                name = name,
                balance = balance.toDoubleOrNull() ?: 0.0,
                type = type
            )
            accountDao.insert(account)
            onDone()
        }
    }
}
