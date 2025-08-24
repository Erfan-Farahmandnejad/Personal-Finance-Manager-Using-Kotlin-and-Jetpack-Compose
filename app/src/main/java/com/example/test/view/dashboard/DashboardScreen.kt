package com.example.test.view.dashboard

// Import Android annotation for suppressing lint warnings
import android.annotation.SuppressLint
// Import Compose UI foundation components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// Import lazy column for scrollable lists
import androidx.compose.foundation.lazy.LazyColumn
// Import Material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
// Import Material 3 components
import androidx.compose.material3.*
// Import Compose runtime components
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
// Import UI layout components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import Hilt view model support
import androidx.hilt.navigation.compose.hiltViewModel
// Import app-specific view model
import com.example.test.viewmodel.DashboardViewModel
// Import lazy items for displaying lists
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
// Import custom navigation drawer component
import com.example.compose.AppNavigationDrawer
// Import transaction model
import com.example.test.model.Transaction
// Import for coroutine operations
import kotlinx.coroutines.launch
// Import for currency formatting
import com.example.test.util.CurrencyFormatter
// Import settings view model
import com.example.test.viewmodel.SettingsViewModel
//import com.example.test.view.AppNavigationDrawer

// Data class representing an item in the navigation drawer
// Contains title text, icon, and navigation route
data class DrawerItem(
    val title: String,     // Display text for the navigation item
    val icon: ImageVector, // Icon to display next to the text
    val route: String      // Navigation route identifier
)

// Main dashboard screen that displays financial summary and recent transactions
// Uses experimental Material 3 API for advanced UI components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),            // View model for dashboard data
    settingsViewModel: SettingsViewModel = hiltViewModel(),     // View model for user settings
    onAddTransaction: () -> Unit,                               // Callback for adding transactions
    onNavigate: (String) -> Unit = {}                           // Callback for navigation
) {
    // Get financial totals - income, expenses, and net amount
    val totalsState = viewModel.totals.observeAsState(Triple(0.0, 0.0, 0.0))
    val totals by totalsState
    // Get list of recent transactions to display
    val recent by viewModel.recentTransactions.observeAsState(emptyList<Transaction>())
    // Get user's preferred currency from settings
    val currency by settingsViewModel.currency.observeAsState("USD")

    // Initialize drawer state (closed by default)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Create coroutine scope for handling animations and async operations
    val scope = rememberCoroutineScope()

    // Define navigation items for the drawer menu
    val drawerItems = listOf(
        DrawerItem("Dashboard", Icons.Default.Home, "dashboard"),           // Home/dashboard screen
        DrawerItem("Transactions", Icons.AutoMirrored.Filled.List, "transactions"), // Transaction list
        DrawerItem("Categories", Icons.Default.AddCircle, "categories"),    // Category management
        DrawerItem("Accounts", Icons.Default.AccountBox, "accounts"),       // Account management
        DrawerItem("Budgets", Icons.Default.ShoppingCart, "budgets"),       // Budget management
        DrawerItem("Reports", Icons.Default.Info, "reports"),               // Financial reports
        DrawerItem("Notifications", Icons.Default.Notifications, "notifications"), // Notifications
        DrawerItem("Settings", Icons.Default.Settings, "settings")          // App settings
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
        currentRoute = "dashboard"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
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
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SummaryCard(
                            label = "Income",
                            amount = totals.first,
                            color = MaterialTheme.colorScheme.primary,
                            currency = currency,
                            viewModel = viewModel
                        )
                        SummaryCard(
                            label = "Expenses",
                            amount = totals.second,
                            color = MaterialTheme.colorScheme.error,
                            currency = currency,
                            viewModel = viewModel
                        )
                        SummaryCard(
                            label = "Net",
                            amount = totals.third,
                            color = MaterialTheme.colorScheme.secondary,
                            currency = currency,
                            viewModel = viewModel
                        )
                    }

                    if (recent.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Recent Transactions",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(recent) { t ->
                                        TransactionItem(transaction = t, currency = currency, viewModel = viewModel)
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f, fill = false))
                    }

                    Button(
                        onClick = onAddTransaction,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Transaction")
                    }
                }
            }
        }
    }
}

// Component that displays a financial summary card (income, expenses, or net)
// Suppresses the default locale lint warning for text capitalization
@SuppressLint("DefaultLocale")
@Composable
fun SummaryCard(
    label: String,                  // Card title (Income, Expenses, or Net)
    amount: Double,                 // Monetary amount to display
    color: Color,                   // Color theme for the card
    currency: String,               // Currency code (USD, IRR, etc.)
    viewModel: DashboardViewModel   // View model for formatting amounts
) {
    Card(
        modifier = Modifier
            .size(width = 110.dp, height = 110.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color.copy(alpha = 0.1f))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            val (mainAmount, secondaryAmount) = viewModel.formatForDashboard(amount, currency)
            
            Text(
                text = mainAmount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = secondaryAmount,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Component that displays a single transaction item in the list
// Suppresses the default locale lint warning for text capitalization
@SuppressLint("DefaultLocale")
@Composable
fun TransactionItem(
    transaction: Transaction,       // Transaction data to display
    currency: String,               // Currency code for formatting
    viewModel: DashboardViewModel   // View model for formatting amounts
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.type.capitalize(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val (mainAmount, secondaryAmount) = viewModel.formatForDashboard(transaction.amount, currency)
                
                Text(
                    text = mainAmount,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (transaction.type == "expense") Color.Red else Color.Green,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = secondaryAmount,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (transaction.type == "expense") 
                        Color.Red.copy(alpha = 0.7f) 
                    else 
                        Color.Green.copy(alpha = 0.7f)
                )
            }
        }
    }
}

