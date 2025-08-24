package com.example.test.data

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.test.data.dao.*
import com.example.test.model.*

/**
 * Migration script from database version 1 to 2
 * Updates the budgets table to use explicit start and end dates instead of month-based tracking
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create a temporary budgets table with the new schema including start and end date fields
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS budgets_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                categoryId INTEGER,
                amountLimit REAL NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL
            )
        """)

        // Convert existing month-based budget data to use explicit start/end dates
        // Start date is set to the 1st day of the month
        // End date is calculated as the 1st day of the next month
        database.execSQL("""
            INSERT INTO budgets_temp (id, categoryId, amountLimit, startDate, endDate)
            SELECT id, categoryId, amountLimit, 
                   month || '-01' as startDate,
                   substr(month, 1, 4) || '-' || 
                   CASE 
                       WHEN cast(substr(month, 6, 2) as INTEGER) = 12 THEN '01'
                       ELSE substr(cast(cast(substr(month, 6, 2) as INTEGER) + 1 as TEXT), 1, 2)
                   END || '-01' as endDate
            FROM budgets
        """)

        // Remove the old table structure
        database.execSQL("DROP TABLE budgets")

        // Rename the temporary table to the original name
        database.execSQL("ALTER TABLE budgets_temp RENAME TO budgets")
    }
}

/**
 * Migration script from database version 2 to 3
 * Adds the settings table to store user application preferences
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the settings table with default values
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS settings (
                id INTEGER PRIMARY KEY NOT NULL,
                firstDayOfMonth INTEGER NOT NULL DEFAULT 1,
                currency TEXT NOT NULL DEFAULT 'USD',
                notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                calendarType TEXT NOT NULL DEFAULT 'GREGORIAN'
            )
        """)

        // Insert default settings record
        database.execSQL("""
            INSERT INTO settings (id, firstDayOfMonth, currency, notificationsEnabled, calendarType)
            VALUES (1, 1, 'USD', 1, 'GREGORIAN')
        """)
    }
}

/**
 * Migration script from database version 3 to 4
 * Adds users table and updates user_preferences table structure
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    @SuppressLint("Range")
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create users table for authentication and user management
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                username TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL
            )
        """)
        
        // Check if user_preferences table already exists in the database
        val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='user_preferences'")
        val tableExists = cursor.count > 0
        cursor.close()
        
        if (tableExists) {
            // If table exists, check if the selectedCurrency column exists
            val columnCursor = database.query("PRAGMA table_info(user_preferences)")
            var selectedCurrencyExists = false
            while (columnCursor.moveToNext()) {
                val columnName = columnCursor.getString(columnCursor.getColumnIndex("name"))
                if (columnName == "selectedCurrency") {
                    selectedCurrencyExists = true
                    break
                }
            }
            columnCursor.close()
            
            if (!selectedCurrencyExists) {
                // Add the missing selectedCurrency column to existing table
                database.execSQL("""
                    ALTER TABLE user_preferences 
                    ADD COLUMN selectedCurrency TEXT NOT NULL DEFAULT 'USD'
                """)
            }
        } else {
            // Create user_preferences table with all required columns if it doesn't exist
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS user_preferences (
                    id INTEGER PRIMARY KEY NOT NULL,
                    startDayOfMonth INTEGER NOT NULL DEFAULT 1,
                    notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                    selectedCurrency TEXT NOT NULL DEFAULT 'USD'
                )
            """)
        }
    }
}

/**
 * Main Room database class that defines the application's database structure
 * Contains all entity definitions and provides access to DAOs
 */
@Database(
    entities = [
        User::class,
        Account::class,
        Category::class,
        Transaction::class,
        Budget::class,
        Settings::class,
        Notification::class,
        UserPreferences::class
    ],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    // Data Access Objects (DAOs) for interacting with each entity in the database
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun settingsDao(): SettingsDao
    abstract fun notificationDao(): NotificationDao
    abstract fun preferencesDao(): PreferencesDao
}