package com.example.test.view.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.ui.components.DateDisplay
import com.example.test.ui.components.DateDisplayWithMonthName
import com.example.test.ui.components.CalendarPicker
import com.example.test.viewmodel.TransactionViewModel
import com.example.test.viewmodel.SettingsViewModel
import androidx.compose.runtime.livedata.observeAsState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showCalendar by remember { mutableStateOf(false) }
    val calendarType by settingsViewModel.calendarType.observeAsState("GREGORIAN")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        DateDisplayWithMonthName(
            date = selectedDate,
            calendarType = calendarType,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )


        Button(
            onClick = { showCalendar = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            DateDisplay(
                date = selectedDate,
                calendarType = calendarType
            )
        }

        if (showCalendar) {
            AlertDialog(
                onDismissRequest = { showCalendar = false },
                title = { Text("Select Date") },
                text = {
                    CalendarPicker(
                        selectedDate = selectedDate,
                        onDateSelected = { 
                            selectedDate = it
                            showCalendar = false
                        },
                        calendarType = calendarType
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showCalendar = false }) {
                        Text("Close")
                    }
                }
            )
        }

    }
} 