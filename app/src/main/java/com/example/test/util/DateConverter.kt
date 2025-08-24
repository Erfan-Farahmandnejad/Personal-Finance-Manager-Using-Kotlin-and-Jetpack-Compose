package com.example.test.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * DateConverter: Core utility class for Persian/Gregorian calendar conversions
 * 
 * This class implements the mathematical algorithms needed for converting between
 * Gregorian and Persian (Shamsi) calendars. The implementation is based on 
 * astronomical calculations and the official Iranian calendar system.
 * 
 * The Persian calendar differs from the Gregorian calendar in several key ways:
 * 1. It starts in 622 CE (the migration of Prophet Muhammad from Mecca to Medina)
 * 2. The year begins on the spring equinox (around March 21)
 * 3. The first six months have 31 days, the next five have 30 days, 
 *    and the last month has 29 days (30 in leap years)
 * 4. It has a different leap year calculation algorithm based on a 33-year cycle
 *
 * Key features:
 * - Accurate date conversion between Gregorian and Persian calendars
 * - Support for Persian month and weekday names (both Persian and English)
 * - Leap year determination for the Persian calendar
 * - Formatting utilities for display in the application
 */
object DateConverter {
    // Persian calendar months (Farsi names)
    val PERSIAN_MONTH_NAMES_FA = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    
    // Persian calendar months (English transliteration)
    val PERSIAN_MONTH_NAMES_EN = arrayOf(
        "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
    )
    
    // Persian weekday names in Farsi (week starts with Saturday in Persian calendar)
    val PERSIAN_WEEKDAY_NAMES_FA = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه"
    )
    
    // Persian weekday names in English
    val PERSIAN_WEEKDAY_NAMES_EN = arrayOf(
        "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    )

    /**
     * Data class to represent a date in the Persian calendar system
     * This provides a clean abstraction for working with Persian dates
     * without having to modify the standard Java/Kotlin date classes
     */
    data class PersianDate(
        val year: Int,
        val month: Int,
        val day: Int
    )

    /**
     * Converts a Gregorian date to a Persian date
     * 
     * The algorithm uses Julian Day Number (JDN) as an intermediate value.
     * JDN is the number of days since noon on January 1, 4713 BCE (Julian calendar).
     * This makes it easier to convert between different calendar systems.
     * 
     * @param date The LocalDate (Gregorian) to convert
     * @return PersianDate representing the equivalent date in Persian calendar
     */
    fun gregorianToPersian(date: LocalDate): PersianDate {
        // The Persian calendar epoch corresponds to March 21, 622 CE in the Gregorian calendar
        // This is represented as a Julian Day Number (JDN)
        val persianEpochInJDN = 1948320    // Julian Day Number of 1st day of Persian year 1 (March 21, 622 CE)
        
        // Extract Gregorian date components
        val gregorianYear = date.year
        val gregorianMonth = date.monthValue
        val gregorianDay = date.dayOfMonth
        
        // Convert input date to Julian Day Number
        val jdn = calculateJDN(gregorianYear, gregorianMonth, gregorianDay)
        var daysPassed = jdn - persianEpochInJDN
        
        // Calculate Persian year by counting how many years have passed
        // We start with year 1 and keep adding years until we find the correct one
        var persianYear = 1
        var daysInPersianYear = 365
        
        while (daysPassed > daysInPersianYear) {
            persianYear++
            // Account for leap years which have 366 days
            daysInPersianYear = if (isPersianLeapYear(persianYear)) 366 else 365
            if (daysPassed > daysInPersianYear) {
                daysPassed -= daysInPersianYear
            }
        }
        
        // Calculate the month and day based on remaining days
        // In Persian calendar: months 1-6 have 31 days, months 7-11 have 30 days,
        // and month 12 has 29 days (or 30 in a leap year)
        var daysInMonth = 0
        var persianMonth = 1
        var tempDaysPassed = daysPassed
        
        while (persianMonth <= 12) {
            daysInMonth = if (persianMonth <= 6) 31 else if (persianMonth <= 11) 30 else if (isPersianLeapYear(persianYear)) 30 else 29
            
            if (tempDaysPassed <= daysInMonth)
                break
                
            tempDaysPassed -= daysInMonth
            persianMonth++
        }
        
        val persianDay = tempDaysPassed
        
        return PersianDate(persianYear, persianMonth, persianDay)
    }

    /**
     * Converts a Persian date to a Gregorian date
     * 
     * This method performs the inverse of the gregorianToPersian function.
     * It calculates how many days have passed since the Persian calendar epoch,
     * then converts this to a Julian Day Number, and finally to a Gregorian date.
     *
     * @param persianDate The Persian date to convert
     * @return LocalDate representing the equivalent date in Gregorian calendar
     */
    fun persianToGregorian(persianDate: PersianDate): LocalDate {
        val persianYear = persianDate.year
        val persianMonth = persianDate.month
        val persianDay = persianDate.day
        
        // Start with the Persian epoch as JDN
        val persianEpochInJDN = 1948320
        
        var jdn = persianEpochInJDN
        
        // Add days for all previous years
        for (y in 1 until persianYear) {
            jdn += if (isPersianLeapYear(y)) 366 else 365
        }
        
        // Add days for previous months in the current year
        // Apply Persian calendar rules for days in each month
        for (m in 1 until persianMonth) {
            jdn += if (m <= 6) 31 else if (m <= 11) 30 else if (isPersianLeapYear(persianYear)) 30 else 29
        }
        
        // Add days of current month
        jdn += persianDay
        
        // Convert JDN to Gregorian date
        return jdnToGregorian(jdn)
    }
    
    /**
     * Calculate Julian Day Number for a Gregorian date
     *
     * This implements an algorithm to convert a date in the Gregorian 
     * calendar to a Julian Day Number (JDN). The algorithm is derived from
     * astronomical calculations and is widely used for calendar conversions.
     * 
     * @param year Gregorian year
     * @param month Gregorian month (1-12)
     * @param day Day of month
     * @return Julian Day Number
     */
    private fun calculateJDN(year: Int, month: Int, day: Int): Int {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        
        // This formula accounts for leap years in the Gregorian calendar
        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
    }
    
    /**
     * Convert Julian Day Number to Gregorian date
     * 
     * This function inverts the calculateJDN function, converting a
     * Julian Day Number back to a date in the Gregorian calendar.
     *
     * @param jdn Julian Day Number to convert
     * @return The corresponding date in the Gregorian calendar as LocalDate
     */
    private fun jdnToGregorian(jdn: Int): LocalDate {
        val j = jdn + 32044
        val g = j / 146097
        val dg = j % 146097
        val c = (dg / 36524 + 1) * 3 / 4
        val dc = dg - c * 36524
        val b = dc / 1461
        val db = dc % 1461
        val a = (db / 365 + 1) * 3 / 4
        val da = db - a * 365
        val y = g * 400 + c * 100 + b * 4 + a
        val m = (da * 5 + 308) / 153 - 2
        val d = da - (m + 4) * 153 / 5 + 122
        
        val year = y - 4800 + (m + 2) / 12
        val month = (m + 2) % 12 + 1
        val day = d + 1
        
        return LocalDate.of(year, month, day)
    }
    
    /**
     * Check if a Persian year is a leap year
     * 
     * The Persian calendar has a unique leap year system based on a 33-year cycle.
     * In each 33-year cycle, years 1, 5, 9, 13, 17, 22, 26, and 30 are leap years.
     * This gives a more accurate average year length of 365.2424... days,
     * which is very close to the actual solar year (365.2422... days).
     * 
     * This is more accurate than the Gregorian leap year system which
     * has an average year length of 365.2425 days.
     *
     * @param year Persian year to check
     * @return true if the year is a leap year in the Persian calendar
     */
    fun isPersianLeapYear(year: Int): Boolean {
        // Persian leap year algorithm uses a 33-year cycle
        val remainder = year % 33
        return remainder == 1 || remainder == 5 || remainder == 9 || remainder == 13 || 
               remainder == 17 || remainder == 22 || remainder == 26 || remainder == 30
    }
    
    /**
     * Format a Gregorian date based on the calendar type
     * 
     * This utility function formats a date in either Persian or Gregorian
     * format depending on the specified calendar type.
     * 
     * @param date The Gregorian date to format
     * @param calendarType The calendar system to use ("PERSIAN" or "GREGORIAN")
     * @return A string representation of the date in format YYYY/MM/DD
     */
    fun formatPersianDate(date: LocalDate, calendarType: String): String {
        return if (calendarType == "PERSIAN") {
            val persian = gregorianToPersian(date)
            "${persian.year}/${persian.month.toString().padStart(2, '0')}/${persian.day.toString().padStart(2, '0')}"
        } else {
            "${date.year}/${date.monthValue.toString().padStart(2, '0')}/${date.dayOfMonth.toString().padStart(2, '0')}"
        }
    }
    
    /**
     * Get Persian month name in English
     * 
     * @param month Month number (1-12)
     * @return Name of the month in English transliteration
     */
    fun getPersianMonthName(month: Int): String {
        return if (month in 1..12) PERSIAN_MONTH_NAMES_EN[month - 1] else ""
    }
    
    /**
     * Get Persian month name in Farsi
     * 
     * @param month Month number (1-12)
     * @return Name of the month in Persian script
     */
    fun getPersianMonthNameFa(month: Int): String {
        return if (month in 1..12) PERSIAN_MONTH_NAMES_FA[month - 1] else ""
    }

    /**
     * Get Persian weekday name in English
     * 
     * @param dayOfWeek Day of week (1 = Saturday through 7 = Friday)
     * @return Name of the weekday in English
     */
    fun getPersianWeekDay(dayOfWeek: Int): String {
        return if (dayOfWeek in 1..7) PERSIAN_WEEKDAY_NAMES_EN[dayOfWeek - 1] else ""
    }
}