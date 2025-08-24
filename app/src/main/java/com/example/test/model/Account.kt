package com.example.test.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val balance: Double,
    val type: AccountType // Wallet, Bank, ...
)

enum class AccountType {
    BANK, WALLET, OTHER
}
