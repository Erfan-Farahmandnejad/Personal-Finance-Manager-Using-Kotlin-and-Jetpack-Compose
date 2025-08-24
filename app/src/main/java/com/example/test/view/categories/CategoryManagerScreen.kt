package com.example.test.view.categories

// Import layout components
import androidx.compose.foundation.layout.*
// Import list component for scrollable lists
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// Import Hilt view model support
import androidx.hilt.navigation.compose.hiltViewModel
// Import category type enum
import com.example.test.model.CategoryType
// Import category manager view model
import com.example.test.viewmodel.CategoryManagerViewModel
// Import lazy list items
import androidx.compose.foundation.lazy.items
// Import custom components
import com.example.compose.*
// Import for locale-sensitive string operations
import java.util.Locale
// Import coroutine components
import androidx.compose.runtime.rememberCoroutineScope
// Import experimental Material 3 API
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
// Import coroutine launch functionality
import kotlinx.coroutines.launch
// Import navigation screen routes
import com.example.test.navigation.Screen
// Import drawer item class from dashboard
import com.example.test.view.dashboard.DrawerItem


// Category management screen that allows users to view, filter, add and edit categories
// Uses experimental Material 3 API for advanced UI components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerScreen(
    viewModel: CategoryManagerViewModel = hiltViewModel(), // View model for category data
    onAddCategory: () -> Unit,                            // Callback for adding a category
    onNavigate: (String) -> Unit = {}                     // Callback for navigation
) {
    // Track the current category type filter (EXPENSE or INCOME)
    val currentType by viewModel.currentType.observeAsState(CategoryType.EXPENSE)
    // Get filtered list of categories based on selected type
    val categories by viewModel.categories.observeAsState(emptyList())

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
        currentRoute = "categories"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Categories") },
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
                    // Type Selector Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FilterChip(
                                    selected = currentType == CategoryType.EXPENSE,
                                    onClick = { viewModel.switchType(CategoryType.EXPENSE) },
                                    label = { Text("Expense") },
                                    leadingIcon = {
                                        if (currentType == CategoryType.EXPENSE) {
                                            Icon(Icons.Default.Add, "Selected")
                                        }
                                    }
                                )
                                FilterChip(
                                    selected = currentType == CategoryType.INCOME,
                                    onClick = { viewModel.switchType(CategoryType.INCOME) },
                                    label = { Text("Income") },
                                    leadingIcon = {
                                        if (currentType == CategoryType.INCOME) {
                                            Icon(Icons.Default.Add, "Selected")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${currentType.toString().lowercase().capitalize(Locale.ROOT)} Categories",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            if (categories.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No ${currentType.toString().lowercase()} categories yet",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(categories) { category ->
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
                                                Text(
                                                    text = category.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = { onNavigate(Screen.EditCategory.createRoute(category.id)) }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Edit,
                                                            contentDescription = "Edit",
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = { viewModel.deleteCategory(category.id) }
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

                FloatingActionButton(
                    onClick = onAddCategory,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Category")
                }
            }
        }
    }
}
