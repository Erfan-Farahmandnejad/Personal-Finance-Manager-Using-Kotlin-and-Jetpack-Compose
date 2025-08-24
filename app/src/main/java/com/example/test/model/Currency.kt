package com.example.test.model

data class Currency(
    val code: String,
    val symbol: String,
    val name: String,      // "US Dollar", "Iranian Rial"
    val exchangeRate: Double = 1.0  // Exchange rate relative to USD
)

object Currencies {
    val USD = Currency("USD", "$", "US Dollar")
    val IRR = Currency("IRR", "ریال", "Iranian Rial", 100000.0) // 1 USD = 100000 IRR (10x more than Toman)
    val EUR = Currency("EUR", "€", "Euro", 0.92)
    val GBP = Currency("GBP", "£", "British Pound", 0.78)
    val JPY = Currency("JPY", "¥", "Japanese Yen", 150.0)
    val AUD = Currency("AUD", "A$", "Australian Dollar", 1.5)

    val allCurrencies = listOf(USD, IRR, EUR, GBP, JPY, AUD)
    
    fun getCurrencyByCode(code: String): Currency {
        return allCurrencies.find { it.code == code } ?: USD
    }
}