package com.example.test.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.ui.components.DropdownMenuBox
import com.example.test.util.CurrencyFormatter
import com.example.test.viewmodel.SettingsViewModel
import com.example.test.navigation.Screen
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.compose.AppNavigationDrawer
import com.example.test.view.dashboard.DrawerItem
import com.example.test.model.Currencies

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    // Settings view model to manage user preferences
    viewModel: SettingsViewModel = hiltViewModel(),
    // Navigation callback function
    onNavigate: (String) -> Unit = {}
) {
    // Get the day of month when monthly calculations should start (default: 1)
    val firstDayOfMonth by viewModel.firstDayOfMonth.observeAsState(1)
    // Get user's preferred currency for financial displays
    val currency by viewModel.currency.observeAsState("USD")
    // Check if user has enabled notifications
    val notificationsEnabled by viewModel.notificationsEnabled.observeAsState(false)
    // Get preferred calendar system (Gregorian/Western or Persian/Shamsi)
    val calendarType by viewModel.calendarType.observeAsState("GREGORIAN")
    // Create currency formatter based on user preferences
    val currencyFormatter = remember { viewModel.getCurrencyFormatter() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppNavigationDrawer(
        drawerState = drawerState,
        drawerItems = listOf(
            DrawerItem("Dashboard", Icons.Default.Home, "dashboard"),
            DrawerItem("Transactions", Icons.AutoMirrored.Filled.List, "transactions"),
            DrawerItem("Categories", Icons.Default.AddCircle, "categories"),
            DrawerItem("Accounts", Icons.Default.AccountBox, "accounts"),
            DrawerItem("Budgets", Icons.Default.ShoppingCart, "budgets"),
            DrawerItem("Reports", Icons.Default.Info, "reports"),
            DrawerItem("Notifications", Icons.Default.Notifications, "notifications"),
            DrawerItem("Settings", Icons.Default.Settings, "settings")
        ),
        onItemClick = { route ->
            scope.launch {
                drawerState.close()
                onNavigate(route)
            }
        },
        currentRoute = "settings"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "General Settings",
                            style = MaterialTheme.typography.titleLarge
                        )

                        // Dropdown for selecting which day of month budgets and reports should start on
                        // This allows users to align their financial tracking with their salary/income date
                        DropdownMenuBox(
                            selectedItem = firstDayOfMonth.toString(),
                            options = (1..31).map { it.toString() },  // All possible days in a month
                            label = "First Day of Month",
                            labelFor = { it },  // Display the day number as-is
                            onSelected = { viewModel.setFirstDayOfMonth(it.toInt()) },  // Save selection
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Calendar Settings",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        
                        // Calendar type selection - choose between Gregorian (Western) or Persian (Shamsi) calendar
                        // This affects date display throughout the entire app
                        DropdownMenuBox(
                            selectedItem = calendarType,
                            options = listOf("GREGORIAN", "PERSIAN"),  // Available calendar systems
                            label = "Calendar Type",
                            labelFor = { 
                                when(it) {
                                    "GREGORIAN" -> "Gregorian Calendar (میلادی)"  // Western calendar with English/Arabic numerals
                                    "PERSIAN" -> "Persian Calendar (شمسی)"  // Iranian/Afghan calendar with Persian months
                                    else -> it
                                }
                            },
                            onSelected = { viewModel.setCalendarType(it) },  // Save selection to preferences
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text(
                            text = "This will affect how dates are displayed throughout the app.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Currency Settings",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        DropdownMenuBox(
                            selectedItem = currency,
                            options = Currencies.allCurrencies.map { it.code },
                            label = "Default Currency",
                            labelFor = { currencyCode -> 
                                val curr = Currencies.allCurrencies.find { it.code == currencyCode }
                                "${curr?.symbol} - ${curr?.name} (${curr?.code})"
                            },
                            onSelected = { 
                                viewModel.setCurrency(it)
                                currencyFormatter.setPrimaryCurrency(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        val secondaryCurrency = if (currency == "USD") Currencies.IRR else Currencies.USD
                        Text(
                            text = "Your secondary currency will be ${secondaryCurrency.symbol} - ${secondaryCurrency.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "This will be used as your primary currency throughout the app.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable Notifications")
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Exchange Rate Information",
                            style = MaterialTheme.typography.titleLarge
                        )

                        val lastUpdateTime = currencyFormatter.getLastUpdateTime()
                        Text(
                            text = "Last updated: $lastUpdateTime",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (currency == "USD") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("USD to IRR (Buy)")
                                Text(
                                    text = "USD Buying Rate",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("IRR to USD (Sell)")
                                Text(
                                    text = "USD Selling Rate",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Display a sample conversion
                        val sampleAmount = if (currency == "IRR") 100000.0 else 100.0
                        Text(
                            text = "Sample conversion: ${currencyFormatter.formatWithDualCurrency(sampleAmount, currency)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}