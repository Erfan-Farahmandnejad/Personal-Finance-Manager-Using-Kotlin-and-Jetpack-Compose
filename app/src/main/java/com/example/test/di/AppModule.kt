package com.example.test.di

import android.content.Context
import com.example.test.data.api.CurrencyExchangeService
import com.example.test.data.repository.*
import com.example.test.util.CurrencyFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideCurrencyExchangeService(): CurrencyExchangeService {
        return CurrencyExchangeService()
    }
    
    @Provides
    @Singleton
    fun provideCurrencyFormatter(currencyExchangeService: CurrencyExchangeService): CurrencyFormatter {
        return CurrencyFormatter(currencyExchangeService)
    }
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
} 