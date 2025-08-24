package com.example.test.view.budgeting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.model.Budget
import com.example.test.viewmodel.BudgetManagerViewModel
import com.example.test.viewmodel.SettingsViewModel
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import com.example.compose.AppNavigationDrawer
import com.example.test.navigation.Screen
import com.example.test.view.dashboard.DrawerItem
import com.example.test.ui.components.StringDateDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagerScreen(
    // Initialize the viewModel using Hilt dependency injection
    viewModel: BudgetManagerViewModel = hiltViewModel(),
    // Settings viewModel to get currency and calendar type
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    // Callback function for when the user wants to add a new budget
    onAddBudget: () -> Unit,
    // Navigation callback function
    onNavigate: (String) -> Unit = {}
) {
    // Observe budgets list from the viewModel using LiveData
    val budgets by viewModel.budgets.observeAsState(emptyList())
    // Get user's preferred calendar type from settings
    val calendarType by settingsViewModel.calendarType.observeAsState("GREGORIAN")
    // Get user's preferred currency from settings
    val currency by settingsViewModel.currency.observeAsState("USD")

    // Initialize the navigation drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Create a coroutine scope for handling drawer animations
    val scope = rememberCoroutineScope()
    
    // Define navigation drawer menu items
    val drawerItems = listOf(
        DrawerItem("Dashboard", Icons.Default.Home, "dashboard"),
        DrawerItem("Transactions", Icons.AutoMirrored.Filled.List, "transactions"),
        DrawerItem("Categories", Icons.Default.AddCircle, "categories"),
        DrawerItem("Accounts", Icons.Default.AccountBox, "accounts"),
        DrawerItem("Budgets", Icons.Default.ShoppingCart, "budgets"),
        DrawerItem("Reports", Icons.Default.Info, "reports"),
        DrawerItem("Notifications", Icons.Default.Notifications, "notifications"),
        DrawerItem("Settings", Icons.Default.Settings, "settings")
    )

    // Set up the navigation drawer with items and handlers
    AppNavigationDrawer(
        drawerState = drawerState,
        drawerItems = drawerItems,
        onItemClick = { route ->
            scope.launch {
                drawerState.close()
                onNavigate(route)
            }
        },
        currentRoute = "budgets"
    ) {
        // Main screen scaffold with top app bar
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Budgets") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Scrollable list of budgets
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 88.dp // Extra padding for FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Screen title header
                    item {
                        Text(
                            text = "Budget Manager",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Show message when no budgets exist
                    if (budgets.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No budgets yet. Add your first budget!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // List of budget items
                    items(budgets) { budget ->
                        // Get category name for this budget
                        val catName = runBlocking { viewModel.getCategoryName(budget.categoryId) }
                        // Calculate remaining budget amount
                        val remainingBudget = runBlocking { viewModel.getRemainingBudget(budget) }
                        // Calculate total expenses for this budget
                        val totalExpenses = budget.amountLimit - remainingBudget
                        // Calculate progress percentage
                        val progress = totalExpenses / budget.amountLimit
                        // Change progress color based on percentage used
                        val progressColor = when {
                            progress >= 1.0 -> MaterialTheme.colorScheme.error // Red for over budget
                            progress >= 0.8 -> MaterialTheme.colorScheme.tertiary // Warning color
                            else -> MaterialTheme.colorScheme.primary // Normal color
                        }

                        // Budget card for each item
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Budget header with category name and action buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        // Display category name
                                        Text(
                                            text = "Category: $catName",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        
                                        // Display budget date range
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Period: ",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            // Format start date according to selected calendar system
                                            StringDateDisplay(
                                                dateString = budget.startDate,
                                                calendarType = calendarType,
                                                modifier = Modifier
                                            )
                                            Text(
                                                text = " to ",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            // Format end date according to selected calendar system
                                            StringDateDisplay(
                                                dateString = budget.endDate,
                                                calendarType = calendarType,
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                    // Edit and delete buttons
                                    Row {
                                        IconButton(onClick = { onNavigate(Screen.EditBudget.createRoute(budget.id)) }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit Budget",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = { viewModel.deleteBudget(budget.id) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Budget",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { progress.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = progressColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Budget Limit:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = viewModel.formatWithDualCurrency(budget.amountLimit, currency),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Expenses:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = viewModel.formatWithDualCurrency(totalExpenses, currency),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Remaining:",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = viewModel.formatWithDualCurrency(remainingBudget, currency),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (remainingBudget >= 0) MaterialTheme.colorScheme.primary 
                                                   else MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = onAddBudget,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Budget")
                }
            }
        }
    }
}
