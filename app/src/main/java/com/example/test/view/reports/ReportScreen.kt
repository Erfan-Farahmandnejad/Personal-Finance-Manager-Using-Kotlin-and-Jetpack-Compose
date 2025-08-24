package com.example.test.view.reports

// Import for border styling
import androidx.compose.foundation.BorderStroke
// Import background styling capabilities 
import androidx.compose.foundation.background
// Import layout components
import androidx.compose.foundation.layout.*
// Import shape components
import androidx.compose.foundation.shape.RoundedCornerShape
// Import Material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
// Import Material 3 components
import androidx.compose.material3.*
// Import Compose runtime components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
// Import UI alignment and styling components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import Hilt view model support
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
// Import report view model
import com.example.test.viewmodel.ReportViewModel
// Import custom components
import com.example.compose.*
// Import locale support
import java.util.Locale
// Import drawer components
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
// Import coroutine support
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
// Import custom navigation drawer
import com.example.compose.AppNavigationDrawer
// Import currency formatter
import com.example.test.util.CurrencyFormatter
// Import drawer item model
import com.example.test.view.dashboard.DrawerItem
// Import settings view model
import com.example.test.viewmodel.SettingsViewModel

// Color scheme constants for the report screen using a GitHub-inspired dark theme
private val backgroundColor = Color(0xFF0D1117)  // Dark background color similar to GitHub dark mode
private val textColor = Color(0xFFC9D1D9)        // Main text color for better readability
private val borderColor = Color(0xFF30363D)      // Border color for cards and separators
private val primaryColor = Color(0xFF58A6FF)     // Primary accent color (blue) for buttons and highlights
private val secondaryColor = Color(0xFF238636)   // Secondary color (green) for positive values
private val errorColor = Color(0xFFF85149)       // Error/negative color (red) for expenses
private val lightText = Color(0xFF8B949E)        // Subdued text color for less important information

// Financial reports screen that displays income/expense summaries and spending breakdowns
// Uses experimental Material 3 API for advanced UI components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),         // View model for report data
    settingsViewModel: SettingsViewModel = hiltViewModel(), // View model for user preferences
    onNavigate: (String) -> Unit = {}                     // Navigation callback
) {
    // Initialize drawer state (closed by default)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Create coroutine scope for animation handling
    val scope = rememberCoroutineScope()
    // Get user's preferred currency from settings
    val currency by settingsViewModel.currency.observeAsState("USD")
    
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
        currentRoute = "reports"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Reports") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Get financial summary data (income, expenses, net balance)
            val summaryState = viewModel.summary.observeAsState()
            val summary by summaryState
            // Get spending breakdown by category
            val categoryBreakdownState = viewModel.categoryBreakdown.observeAsState()
            val categoryBreakdown = categoryBreakdownState.value
            // Get monthly spending trend data for charts
            val monthlySpendingState = viewModel.monthlySpending.observeAsState()
            val monthlySpending by monthlySpendingState

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Financial Report",
                        style = MaterialTheme.typography.headlineMedium,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D) // Foreground component color
                        ),
                        border = BorderStroke(1.dp, borderColor) // GitHub border
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Spending Summary",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            summary?.let { (income, expense, net) ->
                                Text(
                                    "Total Income: ${viewModel.formatCurrency(income, currency)}",
                                    color = textColor
                                )
                                Text(
                                    "Total Expense: ${viewModel.formatCurrency(expense, currency)}",
                                    color = textColor
                                )
                                Text(
                                    "Net Balance: ${viewModel.formatCurrency(net, currency)}",
                                    color = textColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D) // Foreground component color
                        ),
                        border = BorderStroke(1.dp, borderColor) // GitHub border
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Category Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            categoryBreakdown?.forEach { (categoryName, amount, type) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF21262D) // Foreground component color
                                    ),
                                    border = BorderStroke(1.dp, borderColor) // GitHub border
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                categoryName,
                                                color = textColor
                                            )
                                            Text(
                                                type.capitalize(),
                                                color = when (type) {
                                                    "income" -> Color.Green
                                                    "expense" -> errorColor
                                                    else -> textColor
                                                },
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        Text(
                                            viewModel.formatCurrency(amount, currency),
                                            modifier = Modifier.padding(start = 8.dp),
                                            color = when (type) {
                                                "income" -> Color.Green
                                                "expense" -> errorColor
                                                else -> textColor
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF21262D) // Foreground component color
                        ),
                        border = BorderStroke(1.dp, borderColor) // GitHub border
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Monthly Spending",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            monthlySpending?.forEach { (month, amount) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF21262D) // Foreground component color
                                    ),
                                    border = BorderStroke(1.dp, borderColor) // GitHub border
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            month,
                                            color = textColor
                                        )
                                        Text(
                                            viewModel.formatCurrency(amount, currency),
                                            color = textColor
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

