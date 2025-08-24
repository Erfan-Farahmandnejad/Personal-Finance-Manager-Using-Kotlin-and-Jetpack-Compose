package com.example.test.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.test.util.DateConverter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.test.viewmodel.SettingsViewModel

@Composable
fun DateDisplay(
    date: LocalDate,
    calendarType: String = "GREGORIAN",
    modifier: Modifier = Modifier
) {
    val formattedDate = DateConverter.formatPersianDate(date, calendarType)
    Text(
        text = formattedDate,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
fun DateDisplayWithMonthName(
    date: LocalDate,
    calendarType: String = "GREGORIAN",
    modifier: Modifier = Modifier
) {
    if (calendarType == "PERSIAN") {
        val persianDate = DateConverter.gregorianToPersian(date)
        val monthName = DateConverter.getPersianMonthNameFa(persianDate.month)
        Text(
            text = "$monthName ${persianDate.day}، ${persianDate.year}",
            modifier = modifier,
            textAlign = TextAlign.Center
        )
    } else {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
        Text(
            text = date.format(formatter),
            modifier = modifier,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StringDateDisplay(
    dateString: String,
    calendarType: String = "GREGORIAN",
    modifier: Modifier = Modifier
) {
    // Try to parse the date string (expected format: YYYY-MM-DD)
    val date = try {
        val parts = dateString.split("-")
        if (parts.size == 3) {
            LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
    
    Text(
        text = if (date != null) {
            DateConverter.formatPersianDate(date, calendarType)
        } else {
            dateString // Fallback to original string if parsing fails
        },
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

/**
 * Formats a date string (YYYY-MM-DD) according to the specified calendar type
 */
fun formatDateString(dateString: String, calendarType: String): String {
    // Try to parse the date string
    val date = try {
        val parts = dateString.split("-")
        if (parts.size == 3) {
            LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            return dateString // Return original if format doesn't match
        }
    } catch (e: Exception) {
        return dateString // Return original on any parsing error
    }
    
    return DateConverter.formatPersianDate(date, calendarType)
}