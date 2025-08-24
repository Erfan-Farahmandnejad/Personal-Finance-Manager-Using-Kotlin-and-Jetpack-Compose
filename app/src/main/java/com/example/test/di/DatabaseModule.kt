package com.example.test.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.test.data.AppDatabase
import com.example.test.data.MIGRATION_1_2
import com.example.test.data.MIGRATION_2_3
import com.example.test.data.MIGRATION_3_4
import com.example.test.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        // We're removing the database deletion code that was causing data loss
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
        .fallbackToDestructiveMigration() // Keep this to handle schema changes gracefully
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Initialize database with default data if needed
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Run operations when database is opened
            }
        })
        .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideBudgetDao(database: AppDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    @Provides
    fun providePreferencesDao(database: AppDatabase): PreferencesDao = database.preferencesDao()

    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao = database.settingsDao()
} 