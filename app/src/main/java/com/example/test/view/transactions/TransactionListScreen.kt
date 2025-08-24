package com.example.test.view.transactions

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.viewmodel.DashboardViewModel
import com.example.test.viewmodel.TransactionListViewModel
import kotlinx.coroutines.runBlocking
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.compose.AppNavigationDrawer
import com.example.test.navigation.Screen
import com.example.test.util.CurrencyFormatter
import com.example.test.view.dashboard.DrawerItem
import com.example.test.viewmodel.SettingsViewModel
import com.example.test.ui.components.StringDateDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    onAddTransaction: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val transactions by viewModel.transactions.observeAsState(emptyList())
    val currency by settingsViewModel.currency.observeAsState("USD")
    val calendarType by settingsViewModel.calendarType.observeAsState("GREGORIAN")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
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

    AppNavigationDrawer(
        drawerState = drawerState,
        drawerItems = drawerItems,
        onItemClick = { route ->
            scope.launch {
                drawerState.close()
                onNavigate(route)
            }
        },
        currentRoute = "transactions"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Transactions") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddTransaction,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                "Transactions",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }

                    if (transactions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions yet. Add your first transaction!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(transactions) { t ->
                        val category = runBlocking { viewModel.getCategoryName(t.categoryId) }
                        val from = runBlocking { viewModel.getAccountName(t.fromAccountId) }
                        val to = runBlocking { viewModel.getAccountName(t.toAccountId) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = t.type.capitalize(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when (t.type) {
                                            "expense" -> "From: $from"
                                            "income" -> "To: $to"
                                            "transfer" -> "From: $from to $to"
                                            else -> ""
                                        },
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (t.categoryId != null) {
                                        Text(
                                            text = "Category: $category",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Date: ",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        StringDateDisplay(
                                            dateString = t.date,
                                            calendarType = calendarType,
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    val formattedAmount = viewModel.formatWithDualCurrency(t.amount, currency)
                                    Text(
                                        text = formattedAmount,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = when (t.type) {
                                            "expense" -> MaterialTheme.colorScheme.error
                                            "income" -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                onNavigate(Screen.EditTransaction.createRoute(t.id))
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.deleteTransaction(t.id) {
                                                    dashboardViewModel.refreshDashboard()
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
