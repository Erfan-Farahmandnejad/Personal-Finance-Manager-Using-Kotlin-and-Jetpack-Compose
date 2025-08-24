package com.example.test.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import com.example.test.util.DateConverter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPicker(
    // Currently selected date to highlight in calendar
    selectedDate: LocalDate,
    // Callback function when user selects a new date
    onDateSelected: (LocalDate) -> Unit,
    // Calendar system to use - can be GREGORIAN (default) or PERSIAN
    calendarType: String = "GREGORIAN",
    // Optional modifier for customizing layout
    modifier: Modifier = Modifier
) {
    // Track which month is currently being displayed in the calendar
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    Column(modifier = modifier) {
        // Month and Year header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month button
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("<")
            }
            
            // Display month name and year according to selected calendar system
            if (calendarType == "PERSIAN") {
                // Convert month to Persian calendar
                val persianDate = DateConverter.gregorianToPersian(currentMonth.atDay(1))
                // Get Persian month name in Farsi
                val monthName = DateConverter.getPersianMonthNameFa(persianDate.month)
                Text(
                    text = "$monthName ${persianDate.year}",
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                // Use standard Java time formatting for Gregorian calendar
                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Next month button
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text(">")
            }
        }

        // Days of week header row
        Row(modifier = Modifier.fillMaxWidth()) {
            if (calendarType == "PERSIAN") {
                // Persian calendar starts with Saturday - use abbreviated Persian weekday names
                val persianDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
                persianDays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                // Use standard weekday names for Gregorian calendar
                DayOfWeek.values().forEach { dayOfWeek ->
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Calculate calendar grid parameters
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfMonth = if (calendarType == "PERSIAN") {
            // Persian calendar starts with Saturday (value 1 in our system)
            // We need to convert from Gregorian day of week to Persian day of week
            // In Gregorian: Monday=1, Tuesday=2, ..., Sunday=7
            // In Persian: Saturday=1, Sunday=2, ..., Friday=7
            val gregorianDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1-7 (Monday-Sunday)
            
            // Convert to Persian day of week (Saturday=1, Sunday=2, ..., Friday=7)
            // Gregorian: (Mon=1, Tue=2, Wed=3, Thu=4, Fri=5, Sat=6, Sun=7)
            // Persian:   (Sat=1, Sun=2, Mon=3, Tue=4, Wed=5, Thu=6, Fri=7)
            // Formula: ((gregorianDayOfWeek + 1) % 7) + 1
            val persianDayOfWeek = ((gregorianDayOfWeek + 1) % 7) + 1
            persianDayOfWeek
        } else {
            // For Gregorian calendar, use standard day of week value
            currentMonth.atDay(1).dayOfWeek.value
        }
        
        // Generate list of dates to display (42 cells = 6 rows x 7 days)
        // null values represent empty cells at start/end of calendar
        val days = List(42) { index ->
            if (index < firstDayOfMonth - 1 || index >= firstDayOfMonth - 1 + daysInMonth) {
                null
            } else {
                currentMonth.atDay(index - firstDayOfMonth + 2)
            }
        }

        // Calendar days grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(days) { date ->
                if (date != null) {
                    // Check if this date is the currently selected date
                    val isSelected = date == selectedDate
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (calendarType == "PERSIAN") {
                            // Convert date to Persian calendar for display
                            val persianDate = DateConverter.gregorianToPersian(date)
                            Text(
                                text = persianDate.day.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            // Use standard day of month for Gregorian calendar
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    // Empty box for days outside current month
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
