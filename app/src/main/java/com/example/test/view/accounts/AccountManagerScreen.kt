package com.example.test.view.accounts

// Import layout components
import androidx.compose.foundation.layout.*
// Import list components
import androidx.compose.foundation.lazy.LazyColumn
// Import material icons
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
// Import Material 3 components
import androidx.compose.material3.*
// Import Compose runtime components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
// Import UI components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Import view models
import com.example.test.viewmodel.AccountManagerViewModel
// Import Hilt dependency injection
import androidx.hilt.navigation.compose.hiltViewModel
// Import lazy list items
import androidx.compose.foundation.lazy.items
// Import coroutine components
import androidx.compose.runtime.rememberCoroutineScope
// Import experimental Material 3 API
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
// Import resource components
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
// Import coroutine components
import kotlinx.coroutines.launch
// Import custom navigation drawer
import com.example.compose.AppNavigationDrawer
// Import navigation routes
import com.example.test.navigation.Screen
// Import currency formatter
import com.example.test.util.CurrencyFormatter
// Import drawer item component
import com.example.test.view.dashboard.DrawerItem
// Import settings view model
import com.example.test.viewmodel.SettingsViewModel

// Account management screen that allows users to view, add, edit, and delete financial accounts
// Uses experimental Material 3 API for advanced UI components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagerScreen(
    viewModel: AccountManagerViewModel = hiltViewModel(),  // View model for account data
    settingsViewModel: SettingsViewModel = hiltViewModel(), // View model for user preferences
    onAddAccount: () -> Unit,                             // Callback for adding a new account
    onNavigate: (String) -> Unit = {}                     // Navigation callback
) {
    // Get all accounts from the database
    val accounts by viewModel.accounts.observeAsState(emptyList())
    // Get user's preferred currency from settings
    val currency by settingsViewModel.currency.observeAsState("USD")

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
        currentRoute = "accounts"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Accounts") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddAccount,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Account")
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
                                "Accounts",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }

                    if (accounts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No accounts yet. Add your first account!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(accounts) { account ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = account.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Balance: ${viewModel.formatWithDualCurrency(account.balance, currency)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Type: ${account.type}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = { onNavigate(Screen.EditAccount.createRoute(account.id)) }
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Account",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteAccount(account.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Account",
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