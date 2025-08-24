package com.example.test.data.api

import com.example.test.model.Currencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class CurrencyRate(
    val value: Double,
    val change: Double,
    val timestamp: Long,
    val date: String
)

@Singleton
class CurrencyExchangeService @Inject constructor() {
    private val apiKey = "free7Ki9ALdjmCKZEUJR4ZIqzlF0LEv8"
    private val baseUrl = "https://api.navasan.tech/latest/?api_key=$apiKey"
    
    // Cache for exchange rates to avoid excessive API calls
    private var currencyRates: Map<String, CurrencyRate> = emptyMap()
    private var lastFetchTime: Long = 0
    private val cacheValidityPeriod = 1 * 60 * 60 * 1000 // 1 hour in milliseconds
    
    // Fixed conversion rates (fallback values)
    private val fallbackRates = mapOf(
        Pair("USD_IRR", 8285000.0), // 10x more than Toman
        Pair("IRR_USD", 0.0000012), // 10x less than Toman
        Pair("USD_EUR", 0.92),
        Pair("EUR_USD", 1.09),
        Pair("USD_GBP", 0.78),
        Pair("GBP_USD", 1.28),
        Pair("USD_JPY", 150.0),
        Pair("JPY_USD", 0.0067),
        Pair("USD_AUD", 1.5),
        Pair("AUD_USD", 0.67)
    )
    
    suspend fun getExchangeRate(from: String, to: String): Double {
        if (shouldRefreshRates()) {
            fetchExchangeRates()
        }
        
        // Both are the same currency
        if (from == to) return 1.0
        
        // Direct conversion to/from USD
        val key = "${from}_${to}"
        if (fallbackRates.containsKey(key)) {
            return fallbackRates[key] ?: 1.0
        }
        
        // Special case for IRR to USD conversion - multiply by 10 to fix the rate
        if (from == "IRR" && to == "USD") {
            val baseRate = fallbackRates["IRR_USD"] ?: 0.0000012
            return baseRate // No need to multiply by 10, the base rate is already correct
        }
        
        // Need to convert via USD
        val fromToUSD = getExchangeRate(from, "USD")
        val usdToTarget = getExchangeRate("USD", to)
        return fromToUSD * usdToTarget
    }
    
    suspend fun convertCurrency(amount: Double, from: String, to: String): Double {
        val rate = getExchangeRate(from, to)
        return amount * rate
    }
    
    private fun shouldRefreshRates(): Boolean {
        val currentTime = System.currentTimeMillis()
        return currencyRates.isEmpty() || 
               (currentTime - lastFetchTime) > cacheValidityPeriod
    }
    
    private suspend fun fetchExchangeRates() {
        try {
            withContext(Dispatchers.IO) {
                val response = URL(baseUrl).readText()
                val jsonObject = JSONObject(response)
                
                val ratesMap = mutableMapOf<String, CurrencyRate>()
                
                jsonObject.keys().forEach { key ->
                    if (jsonObject.has(key)) {
                        try {
                            val rateObj = jsonObject.getJSONObject(key)
                            
                            // Safely parse the value which could be either Integer or String
                            val valueStr = rateObj.getString("value")
                            val value = valueStr.toDouble()
                            
                            // Safely get change value which could be Integer or Double
                            val change = when {
                                rateObj.has("change") -> {
                                    try {
                                        rateObj.getDouble("change")
                                    } catch (e: Exception) {
                                        rateObj.getInt("change").toDouble()
                                    }
                                }
                                else -> 0.0
                            }
                            
                            val rate = CurrencyRate(
                                value = value,
                                change = change,
                                timestamp = rateObj.getLong("timestamp"),
                                date = rateObj.getString("date")
                            )
                            ratesMap[key] = rate
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                
                if (ratesMap.isNotEmpty()) {
                    currencyRates = ratesMap
                    lastFetchTime = System.currentTimeMillis()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getLatestUpdateTime(): String {
        return currencyRates["usd_sell"]?.date ?: "Not available"
    }
} 