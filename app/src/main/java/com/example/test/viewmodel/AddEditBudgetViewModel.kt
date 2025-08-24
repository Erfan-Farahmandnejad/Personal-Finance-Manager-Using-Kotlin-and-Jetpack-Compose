package com.example.test.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.test.data.dao.BudgetDao
import com.example.test.data.dao.CategoryDao
import com.example.test.data.dao.SettingsDao
import com.example.test.model.Budget
import com.example.test.model.CategoryType
import com.example.test.util.DateConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val settingsDao: SettingsDao
) : ViewModel() {

    var categoryId: Int? by mutableStateOf(null)
    var amountLimit: String by mutableStateOf("")
    var startDate: String by mutableStateOf("")
    var endDate: String by mutableStateOf("")
    var repeat: String by mutableStateOf("1")
    var errorMessage: String? by mutableStateOf(null)
    private var currentBudgetId: Int? = null

    val categories = categoryDao.getCategoriesByType(CategoryType.EXPENSE.toString()).asLiveData()

    init {
        viewModelScope.launch {
            val settings = settingsDao.getSettings().first()
            val startDay = settings?.firstDayOfMonth ?: 1
            val calendarType = settings?.calendarType ?: "GREGORIAN"
            startDate = getCurrentStartDate(startDay, calendarType)
            endDate = getCurrentEndDate(startDay, calendarType)
        }
    }

    fun loadBudget(budgetId: Int) {
        viewModelScope.launch {
            val budget = budgetDao.getBudgetById(budgetId)
            budget?.let {
                currentBudgetId = it.id
                categoryId = it.categoryId
                amountLimit = it.amountLimit.toString()
                startDate = it.startDate
                endDate = it.endDate
                repeat = it.repeat.toString()
            }
        }
    }

    fun saveBudget(onDone: () -> Unit) {
        viewModelScope.launch {
            val repeatCount = repeat.toIntOrNull() ?: 1
            
            // Validate input
            if (amountLimit.toDoubleOrNull() == null) {
                errorMessage = "Please enter a valid amount"
                return@launch
            }
            
            // Check for overlapping budgets
            if (currentBudgetId == null) {
                val currentCategoryId = categoryId
                val overlappingBudgets = budgetDao.getOverlappingBudgets(currentCategoryId, startDate, endDate)
                if (overlappingBudgets.isNotEmpty()) {
                    val categoryName = if (currentCategoryId == null) "Overall" 
                        else categoryDao.getCategoryNameById(currentCategoryId) ?: "Unknown"
                    errorMessage = "A budget already exists for $categoryName that overlaps with the selected date range"
                    return@launch
                }
            }
            
            // Create the main budget
            val mainBudget = Budget(
                id = currentBudgetId ?: 0,
                categoryId = categoryId,
                amountLimit = amountLimit.toDoubleOrNull() ?: 0.0,
                startDate = startDate,
                endDate = endDate,
                repeat = repeatCount
            )
            budgetDao.insert(mainBudget)
            
            // If repeat is greater than 1, create additional budgets for future months
            if (repeatCount > 1 && currentBudgetId == null) { // Only create repeats for new budgets
                val settings = settingsDao.getSettings().first()
                val calendarType = settings?.calendarType ?: "GREGORIAN"
                createRepeatedBudgets(mainBudget, repeatCount, calendarType)
            }
            
            onDone()
        }
    }
    
    private suspend fun createRepeatedBudgets(mainBudget: Budget, repeatCount: Int, calendarType: String) {
        // Parse the dates
        val startDateParts = mainBudget.startDate.split("-")
        val endDateParts = mainBudget.endDate.split("-")
        
        if (startDateParts.size != 3 || endDateParts.size != 3) {
            return // Invalid date format
        }
        
        val startYear = startDateParts[0].toInt()
        val startMonth = startDateParts[1].toInt()
        val startDay = startDateParts[2].toInt()
        
        val endYear = endDateParts[0].toInt()
        val endMonth = endDateParts[1].toInt()
        val endDay = endDateParts[2].toInt()
        
        // Create copies for future months (skip the first one as it's the main budget)
        for (i in 1 until repeatCount) {
            // Calculate new start and end dates by adding i months
            val newStartDate = addMonthsToDate(startYear, startMonth, startDay, i, calendarType)
            val newEndDate = addMonthsToDate(endYear, endMonth, endDay, i, calendarType)
            
            // Create and save the new budget
            val newBudget = Budget(
                id = 0, // Auto-generate a new ID
                categoryId = mainBudget.categoryId,
                amountLimit = mainBudget.amountLimit,
                startDate = newStartDate,
                endDate = newEndDate,
                repeat = 1 // Don't repeat the repeats
            )
            
            // Check for overlaps before inserting
            val overlappingBudgets = budgetDao.getOverlappingBudgets(
                newBudget.categoryId, newBudget.startDate, newBudget.endDate
            )
            if (overlappingBudgets.isEmpty()) {
                budgetDao.insert(newBudget)
            }
        }
    }
    
    private fun addMonthsToDate(year: Int, month: Int, day: Int, monthsToAdd: Int, calendarType: String): String {
        if (calendarType == "PERSIAN") {
            // For Persian calendar
            // First convert the date to a Persian date
            val gregorianDate = LocalDate.of(year, month, day)
            val persianDate = DateConverter.gregorianToPersian(gregorianDate)
            
            // Add months to Persian date
            var newPersianYear = persianDate.year
            var newPersianMonth = persianDate.month + monthsToAdd
            
            // Adjust year if month goes beyond 12
            while (newPersianMonth > 12) {
                newPersianMonth -= 12
                newPersianYear++
            }
            
            // Handle day overflow for Persian calendar
            // Persian months 1-6 have 31 days, 7-11 have 30 days, 12th month has 29 days (30 in leap years)
            val maxPersianDay = when {
                newPersianMonth <= 6 -> 31
                newPersianMonth <= 11 -> 30
                DateConverter.isPersianLeapYear(newPersianYear) -> 30
                else -> 29
            }
            
            val newPersianDay = minOf(persianDate.day, maxPersianDay)
            
            // Convert back to Gregorian for storage
            val newPersianDate = DateConverter.PersianDate(newPersianYear, newPersianMonth, newPersianDay)
            val newGregorianDate = DateConverter.persianToGregorian(newPersianDate)
            
            return "${newGregorianDate.year}-${newGregorianDate.monthValue.toString().padStart(2, '0')}-${newGregorianDate.dayOfMonth.toString().padStart(2, '0')}"
        } else {
            // Gregorian calendar logic with improved handling for high day values
            try {
                // Create initial date
                val initialDate = LocalDate.of(year, month, day)
                
                // Try adding months directly
                val targetDate = initialDate.plusMonths(monthsToAdd.toLong())
                return "${targetDate.year}-${targetDate.monthValue.toString().padStart(2, '0')}-${targetDate.dayOfMonth.toString().padStart(2, '0')}"
            } catch (e: Exception) {
                // Handle overflow cases (like Feb 30)
                var newYear = year
                var newMonth = month + monthsToAdd
                
                // Adjust year if month goes beyond 12
                while (newMonth > 12) {
                    newMonth -= 12
                    newYear++
                }
                
                // Get length of the target month
                val targetMonthLength = YearMonth.of(newYear, newMonth).lengthOfMonth()
                
                // If original day doesn't exist in target month, use last day of target month
                val newDay = minOf(day, targetMonthLength)
                
                return "$newYear-${newMonth.toString().padStart(2, '0')}-${newDay.toString().padStart(2, '0')}"
            }
        }
    }
    
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun getCurrentStartDate(startDay: Int, calendarType: String): String {
        val now = LocalDate.now()
        
        if (calendarType == "PERSIAN") {
            // Convert to Persian calendar
            val persianDate = DateConverter.gregorianToPersian(now)
            
            // Create start date with the exact startDay for current or previous month
            val startDate = if (persianDate.day >= startDay) {
                // Current month, exact startDay
                DateConverter.PersianDate(persianDate.year, persianDate.month, startDay)
            } else {
                // Previous month, exact startDay
                if (persianDate.month == 1) {
                    // If it's first month, go to previous year's last month
                    DateConverter.PersianDate(persianDate.year - 1, 12, startDay)
                } else {
                    DateConverter.PersianDate(persianDate.year, persianDate.month - 1, startDay)
                }
            }
            
            // Convert back to Gregorian for storage
            val gregorianDate = DateConverter.persianToGregorian(startDate)
            return "${gregorianDate.year}-${gregorianDate.monthValue.toString().padStart(2, '0')}-${gregorianDate.dayOfMonth.toString().padStart(2, '0')}"
        } else {
            // Gregorian calendar logic with improved handling
            val currentMonth = YearMonth.from(now)
            
            val startDate = if (now.dayOfMonth >= startDay) {
                // If current day is >= startDay, use startDay of current month
                try {
                    now.withDayOfMonth(startDay)
                } catch (e: Exception) {
                    // Handle cases where startDay is invalid for current month (e.g., Feb 29 in non-leap year)
                    now.withDayOfMonth(currentMonth.lengthOfMonth())
                }
            } else {
                // If current day is < startDay, use startDay of previous month
                val previousMonth = currentMonth.minusMonths(1)
                try {
                    now.minusMonths(1).withDayOfMonth(startDay)
                } catch (e: Exception) {
                    // Handle invalid day for previous month
                    now.minusMonths(1).withDayOfMonth(previousMonth.lengthOfMonth())
                }
            }
            
            return "${startDate.year}-${startDate.monthValue.toString().padStart(2, '0')}-${startDate.dayOfMonth.toString().padStart(2, '0')}"
        }
    }

    private fun getCurrentEndDate(startDay: Int, calendarType: String): String {
        val now = LocalDate.now()
        
        if (calendarType == "PERSIAN") {
            // Convert to Persian calendar
            val persianDate = DateConverter.gregorianToPersian(now)
            
            // Calculate the end date (startDay - 1) of the next month
            val endDay = if (startDay > 1) startDay - 1 else {
                // If startDay is 1, end day should be the last day of the month
                when {
                    persianDate.month <= 6 -> 31  // First 6 months have 31 days
                    persianDate.month <= 11 -> 30 // Next 5 months have 30 days
                    DateConverter.isPersianLeapYear(persianDate.year) -> 30 // Last month in leap year
                    else -> 29 // Last month in regular year
                }
            }
            
            // Determine which month to use for end date
            val endDate = if (persianDate.day >= startDay) {
                // If current day is >= startDay, end date is in next month
                if (persianDate.month == 12) {
                    // If it's last month, go to next year's first month
                    DateConverter.PersianDate(persianDate.year + 1, 1, endDay)
                } else {
                    DateConverter.PersianDate(persianDate.year, persianDate.month + 1, endDay)
                }
            } else {
                // If current day is < startDay, end date is in current month
                DateConverter.PersianDate(persianDate.year, persianDate.month, endDay)
            }
            
            // Convert back to Gregorian for storage
            val gregorianEndDate = DateConverter.persianToGregorian(endDate)
            return "${gregorianEndDate.year}-${gregorianEndDate.monthValue.toString().padStart(2, '0')}-${gregorianEndDate.dayOfMonth.toString().padStart(2, '0')}"
        } else {
            // Gregorian calendar logic with improved handling of days > 28
            val currentMonth = YearMonth.from(now)
            
            if (now.dayOfMonth >= startDay) {
                // If today's date is after or equal to start day
                // End date should be (startDay - 1) of next month
                val endDay = if (startDay > 1) startDay - 1 else currentMonth.plusMonths(1).lengthOfMonth()
                
                try {
                    // Try to get the day in next month
                    val nextMonth = currentMonth.plusMonths(1)
                    val endDate = LocalDate.of(nextMonth.year, nextMonth.monthValue, 
                                              Math.min(endDay, nextMonth.lengthOfMonth()))
                    return "${endDate.year}-${endDate.monthValue.toString().padStart(2, '0')}-${endDate.dayOfMonth.toString().padStart(2, '0')}"
                } catch (e: Exception) {
                    // If there's any issue, use the last day of next month
                    val endOfNextMonth = currentMonth.plusMonths(1).atEndOfMonth()
                    return "${endOfNextMonth.year}-${endOfNextMonth.monthValue.toString().padStart(2, '0')}-${endOfNextMonth.dayOfMonth.toString().padStart(2, '0')}"
                }
            } else {
                // If today's date is before start day
                // End date should be (startDay - 1) of current month
                val endDay = if (startDay > 1) startDay - 1 else currentMonth.lengthOfMonth()
                
                try {
                    val endDate = LocalDate.of(currentMonth.year, currentMonth.monthValue, 
                                              Math.min(endDay, currentMonth.lengthOfMonth()))
                    return "${endDate.year}-${endDate.monthValue.toString().padStart(2, '0')}-${endDate.dayOfMonth.toString().padStart(2, '0')}"
                } catch (e: Exception) {
                    // If there's any issue, use the last day of current month
                    val endOfMonth = currentMonth.atEndOfMonth()
                    return "${endOfMonth.year}-${endOfMonth.monthValue.toString().padStart(2, '0')}-${endOfMonth.dayOfMonth.toString().padStart(2, '0')}"
                }
            }
        }
    }
}