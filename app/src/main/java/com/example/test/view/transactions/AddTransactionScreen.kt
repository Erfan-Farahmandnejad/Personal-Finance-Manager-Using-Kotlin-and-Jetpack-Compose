package com.example.test.view.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.model.Account
import com.example.test.model.Category
import com.example.test.ui.components.DropdownMenuBox
import com.example.test.ui.components.StringDateDisplay
import com.example.test.ui.components.CalendarPicker
import com.example.test.viewmodel.AddTransactionViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.launch
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import com.example.test.viewmodel.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest
import com.example.test.viewmodel.SettingsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.test.util.DateConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Int? = null,
    onSave: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val categories = viewModel.categories.observeAsState(initial = emptyList()).value
    val accounts = viewModel.accounts.observeAsState(initial = emptyList()).value
    val currency by settingsViewModel.currency.observeAsState("USD")
    val calendarType by settingsViewModel.calendarType.observeAsState("GREGORIAN")
    
    var showDatePicker by remember { mutableStateOf(false) }
    val categoryOptions = categories.map { category -> category.id }
    val budgetAlerts = viewModel.budgetAlerts.value
    val context = LocalContext.current
    
    // Set today's date based on the selected calendar type
    LaunchedEffect(calendarType) {
        if (transactionId == null) {  // Only set for new transactions
            viewModel.setTodayDate(calendarType)
        }
    }
    
    LaunchedEffect(transactionId) {
        transactionId?.let {
            viewModel.loadTransaction(it)
        }
    }
    
    // Observe transaction saved event and refresh dashboard
    LaunchedEffect(Unit) {
        viewModel.transactionSaved.collectLatest { saved ->
            if (saved) {
                // Refresh dashboard data
                dashboardViewModel.refreshDashboard()
            }
        }
    }

    LaunchedEffect(budgetAlerts) {
        if (budgetAlerts.isNotEmpty()) {
            val alert = budgetAlerts.first()
            val message = alert.message
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
            viewModel.clearBudgetAlerts()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId == null) "Create Transaction" else "Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
                            text = if (transactionId == null) "Create Transaction" else "Edit Transaction",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        DropdownMenuBox(
                            selectedItem = viewModel.type,
                            options = listOf("expense", "income", "transfer"),
                            label = "Type",
                            labelFor = { it },
                            onSelected = { viewModel.type = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (viewModel.type != "transfer") {
                            DropdownMenuBox(
                                selectedItem = viewModel.categoryId,
                                options = categoryOptions,
                                label = "Category",
                                labelFor = { id: Int? ->
                                    if (id == null) "Select Category"
                                    else categories.find { cat -> cat.id == id }?.name ?: "Unknown"
                                },
                                onSelected = { viewModel.categoryId = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (viewModel.type == "transfer") {
                            DropdownMenuBox(
                                selectedItem = viewModel.fromAccountId,
                                options = accounts.map { it.id },
                                label = "From Account",
                                labelFor = { id -> accounts.find { account -> account.id == id }?.name ?: "Unknown" },
                                onSelected = { viewModel.fromAccountId = it },
                                modifier = Modifier.fillMaxWidth()
                            )

                            DropdownMenuBox(
                                selectedItem = viewModel.toAccountId,
                                options = accounts.map { it.id },
                                label = "To Account",
                                labelFor = { id -> accounts.find { account -> account.id == id }?.name ?: "Unknown" },
                                onSelected = { viewModel.toAccountId = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            DropdownMenuBox(
                                selectedItem = viewModel.accountId,
                                options = accounts.map { it.id },
                                label = "Account",
                                labelFor = { id -> accounts.find { account -> account.id == id }?.name ?: "Unknown" },
                                onSelected = { viewModel.accountId = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = viewModel.amount,
                            onValueChange = { viewModel.amount = it },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Add currency conversion preview
                        val amountValue = viewModel.amount.toDoubleOrNull() ?: 0.0
                        val showPreview = viewModel.amount.isNotBlank() && amountValue > 0
                        
                        if (showPreview) {
                            val convertedAmount = dashboardViewModel.formatWithDualCurrency(amountValue, currency)
                            Text(
                                text = "Equivalent: $convertedAmount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        // Date field with Calendar picker
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showDatePicker = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Date:")
                                StringDateDisplay(
                                    dateString = viewModel.date,
                                    calendarType = calendarType
                                )
                            }
                        }

                        OutlinedTextField(
                            value = viewModel.note,
                            onValueChange = { viewModel.note = it },
                            label = { Text("Note") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.save(onSave)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (transactionId == null) "Create Transaction" else "Update Transaction")
                        }
                    }
                }
            }
            
            // Date picker dialog
            if (showDatePicker) {
                val date = try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    
                    if (calendarType == "PERSIAN") {
                        // For Persian calendar, we need to convert stored Persian date to Gregorian
                        val parts = viewModel.date.split("-")
                        if (parts.size == 3) {
                            val persianYear = parts[0].toInt()
                            val persianMonth = parts[1].toInt()
                            val persianDay = parts[2].toInt()
                            
                            val persianDate = DateConverter.PersianDate(persianYear, persianMonth, persianDay)
                            DateConverter.persianToGregorian(persianDate)
                        } else {
                            LocalDate.now()
                        }
                    } else {
                        // Regular Gregorian date parsing
                        LocalDate.parse(viewModel.date, formatter)
                    }
                } catch (e: Exception) {
                    LocalDate.now()
                }
                
                AlertDialog(
                    onDismissRequest = { showDatePicker = false },
                    title = { Text("Select Date") },
                    text = {
                        CalendarPicker(
                            selectedDate = date,
                            onDateSelected = { selectedDate -> 
                                if (calendarType == "PERSIAN") {
                                    // Convert selected Gregorian date to Persian format
                                    val persianDate = DateConverter.gregorianToPersian(selectedDate)
                                    // Store in YYYY-MM-DD format
                                    viewModel.date = "${persianDate.year}-${persianDate.month.toString().padStart(2, '0')}-${persianDate.day.toString().padStart(2, '0')}"
                                } else {
                                    // For Gregorian, store date in YYYY-MM-DD format
                                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    viewModel.date = selectedDate.format(formatter)
                                }
                                showDatePicker = false
                            },
                            calendarType = calendarType
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}
