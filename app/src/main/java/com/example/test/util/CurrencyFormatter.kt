package com.example.test.util

import com.example.test.data.api.CurrencyExchangeService
import com.example.test.model.Currencies
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyFormatter @Inject constructor(
    private val currencyExchangeService: CurrencyExchangeService
) {
    // Primary currency is the one user prefers to see
    private var primaryCurrency = "USD"
    // Secondary currency is the alternative currency to show
    private var secondaryCurrency = "IRR"
    
    companion object {
        fun formatAmount(amount: Double, currencyCode: String): String {
            val format = NumberFormat.getCurrencyInstance()
            try {
                format.currency = Currency.getInstance(currencyCode)
                if (amount < 0.01 && currencyCode == "USD") {
                    format.minimumFractionDigits = 5
                    format.maximumFractionDigits = 5
                }
            } catch (e: Exception) {
                // Fallback to USD if currency code is invalid
                format.currency = Currency.getInstance("USD")
            }
            return format.format(amount)
        }
    }
    
    fun setPrimaryCurrency(currency: String) {
        primaryCurrency = currency
        // Set secondary currency (if primary is USD, secondary is IRR, otherwise USD)
        secondaryCurrency = if (currency == "USD") "IRR" else "USD"
    }
    
    fun getPrimaryCurrency(): String = primaryCurrency
    fun getSecondaryCurrency(): String = secondaryCurrency
    
    fun formatAmount(amount: Double, currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance()
        try {
            format.currency = Currency.getInstance(currencyCode)
            // Use 5 decimal places for very small USD values
            if (amount < 0.01 && currencyCode == "USD") {
                format.minimumFractionDigits = 5
                format.maximumFractionDigits = 5
            }
        } catch (e: Exception) {
            // Fallback to USD if currency code is invalid
            format.currency = Currency.getInstance("USD")
        }
        return format.format(amount)
    }
    
    fun formatAmountCompact(amount: Double, currencyCode: String): String {
        val currency = Currencies.getCurrencyByCode(currencyCode)
        
        return when (currencyCode) {
            "IRR" -> {
                when {
                    amount >= 1_000_000 -> String.format("%.1f میلیون ریال", amount / 1_000_000)
                    amount >= 1_000 -> String.format("%.1f هزار ریال", amount / 1_000)
                    else -> String.format("%.2f ریال", amount)
                }
            }
            "USD" -> {
                when {
                    amount >= 1_000_000 -> String.format("%s%.1fM", currency.symbol, amount / 1_000_000)
                    amount >= 1_000 -> String.format("%s%.1fK", currency.symbol, amount / 1_000)
                    amount < 0.01 -> String.format("%s%.5f", currency.symbol, amount) // Show 5 decimal places for very small values
                    else -> String.format("%s%.2f", currency.symbol, amount)
                }
            }
            else -> {
                when {
                    amount >= 1_000_000 -> String.format("%s%.1fM", currency.symbol, amount / 1_000_000)
                    amount >= 1_000 -> String.format("%s%.1fK", currency.symbol, amount / 1_000)
                    else -> String.format("%s%.2f", currency.symbol, amount)
                }
            }
        }
    }
    
    // Format with both currencies
    fun formatWithDualCurrency(amount: Double, currencyCode: String): String {
        val secondaryCurrencyCode = if (currencyCode == primaryCurrency) secondaryCurrency else primaryCurrency
        
        return try {
            runBlocking {
                val convertedAmount = currencyExchangeService.convertCurrency(amount, currencyCode, secondaryCurrencyCode)
                // Special case for Rial to USD conversions to show more decimal places
                if (currencyCode == "IRR" && secondaryCurrencyCode == "USD" || 
                    currencyCode != "USD" && secondaryCurrencyCode == "USD") {
                    "${formatAmount(amount, currencyCode)} (${formatAmountCompact(convertedAmount, secondaryCurrencyCode)})"
                } else {
                    "${formatAmount(amount, currencyCode)} (${formatAmountCompact(convertedAmount, secondaryCurrencyCode)})"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            formatAmount(amount, currencyCode) // Fallback to just showing the primary currency
        }
    }
    
    // For dashboard where we want to show both currencies but with the primary one more prominent
    fun formatForDashboard(amount: Double, currencyCode: String): Pair<String, String> {
        val secondaryCurrencyCode = if (currencyCode == primaryCurrency) secondaryCurrency else primaryCurrency
        
        return try {
            runBlocking {
                val convertedAmount = currencyExchangeService.convertCurrency(amount, currencyCode, secondaryCurrencyCode)
                Pair(
                    formatAmount(amount, currencyCode),
                    "(${formatAmountCompact(convertedAmount, secondaryCurrencyCode)})"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(formatAmount(amount, currencyCode), "") // Fallback to just showing the primary currency
        }
    }

    fun formatAmountWithDualCurrency(amount: Double, primaryCurrency: String): Pair<String, String> {
        return formatForDashboard(amount, primaryCurrency)
    }
    

    fun getLastUpdateTime(): String {
        return currencyExchangeService.getLatestUpdateTime()
    }
}