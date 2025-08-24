package com.example.test.view.budgeting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.model.Category
import com.example.test.ui.components.CalendarPicker
import com.example.test.ui.components.DropdownMenuBox
import com.example.test.ui.components.StringDateDisplay
import com.example.test.viewmodel.AddEditBudgetViewModel
import com.example.test.viewmodel.SettingsViewModel
import androidx.compose.runtime.livedata.observeAsState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEditBudgetScreen(
    // ViewModel for budget creation and editing
    viewModel: AddEditBudgetViewModel = hiltViewModel(),
    // Access user settings for calendar and currency preferences
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    // Budget ID parameter - null for new budget, non-null for editing existing budget
    budgetId: Int? = null,
    // Callback function when save operation completes
    onSave: () -> Unit
) {
    // Get all categories from database for dropdown menu
    val categoriesState = viewModel.categories.observeAsState(initial = emptyList<Category>())
    val categories by categoriesState
    // Create list of category IDs including null (for "overall" budget)
    val categoryOptions: List<Int?> = listOf<Int?>(null) + categories.map { category -> category.id }
    // Get user's preferred calendar type (Persian or Gregorian)
    val calendarType by settingsViewModel.calendarType.observeAsState("GREGORIAN")
    
    // Control visibility of date picker dialogs
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // When screen opens with existing budgetId, load that budget's data
    LaunchedEffect(budgetId) {
        budgetId?.let { viewModel.loadBudget(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (budgetId == null) "Create Budget" else "Edit Budget",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Category dropdown menu - allows selecting specific category or "Overall"
                        DropdownMenuBox(
                            selectedItem = viewModel.categoryId,
                            options = categoryOptions,
                            label = "Category",
                            labelFor = { id: Int? ->
                                if (id == null) "Overall"  // null represents overall budget (not category-specific)
                                else categories.find { cat -> cat.id == id }?.name ?: "Unknown"
                            },
                            onSelected = { viewModel.categoryId = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = viewModel.amountLimit,
                            onValueChange = { viewModel.amountLimit = it },
                            label = { Text("Monthly Limit") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Start date field with Calendar picker
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showStartDatePicker = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Start Date:")
                                StringDateDisplay(
                                    dateString = viewModel.startDate,
                                    calendarType = calendarType
                                )
                            }
                        }

                        // End date field with Calendar picker
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showEndDatePicker = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "End Date:")
                                StringDateDisplay(
                                    dateString = viewModel.endDate,
                                    calendarType = calendarType
                                )
                            }
                        }

                        TextField(
                            value = viewModel.repeat,
                            onValueChange = { viewModel.repeat = it },
                            label = { Text("Repeat for Future Months (1 = No Repeat)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Info text about repeat functionality
                        Text(
                            text = "If you set a value greater than 1, the budget will be repeated for that many months.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // Display error message if any
                        viewModel.errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.saveBudget(onSave) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (budgetId == null) "Create Budget" else "Update Budget")
                        }
                    }
                }
            }
        }
        
        // Start date calendar picker dialog
        if (showStartDatePicker) {
            val date = try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                LocalDate.parse(viewModel.startDate, formatter)
            } catch (e: Exception) {
                LocalDate.now()
            }
            
            AlertDialog(
                onDismissRequest = { showStartDatePicker = false },
                title = { Text("Select Start Date") },
                text = {
                    CalendarPicker(
                        selectedDate = date,
                        onDateSelected = { 
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            viewModel.startDate = it.format(formatter)
                            showStartDatePicker = false
                        },
                        calendarType = calendarType
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("Close")
                    }
                }
            )
        }
        
        // End date calendar picker dialog
        if (showEndDatePicker) {
            val date = try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                LocalDate.parse(viewModel.endDate, formatter)
            } catch (e: Exception) {
                LocalDate.now()
            }
            
            AlertDialog(
                onDismissRequest = { showEndDatePicker = false },
                title = { Text("Select End Date") },
                text = {
                    CalendarPicker(
                        selectedDate = date,
                        onDateSelected = { 
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            viewModel.endDate = it.format(formatter)
                            showEndDatePicker = false
                        },
                        calendarType = calendarType
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showEndDatePicker = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}