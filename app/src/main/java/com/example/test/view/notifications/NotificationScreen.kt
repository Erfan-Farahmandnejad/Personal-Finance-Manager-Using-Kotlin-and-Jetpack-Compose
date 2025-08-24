package com.example.test.view.notifications

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.test.viewmodel.NotificationViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.compose.AppNavigationDrawer
import com.example.test.view.dashboard.DrawerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {}
) {
    val notifications by viewModel.notifications.observeAsState(emptyList())

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
        currentRoute = "notifications"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notifications") },
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Notifications",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            if (notifications.isNotEmpty()) {
                                TextButton(
                                    onClick = { viewModel.clearAll() }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Clear All",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear All")
                                }
                            }
                        }
                    }

                    if (notifications.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No notifications",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    items(
                        items = notifications,
                        key = { it.id }
                    ) { notification ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.markAsRead(notification.id) }
                                .animateContentSize(),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (notification.read) 1.dp else 4.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (notification.read)
                                    MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = notification.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (notification.read) FontWeight.Normal else FontWeight.Bold
                                    )
                                    Text(
                                        text = notification.message,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = notification.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = if (notification.read)
                                        Icons.Outlined.CheckCircle
                                    else
                                        Icons.Default.CheckCircle,
                                    contentDescription = if (notification.read) "Read" else "Unread",
                                    tint = if (notification.read)
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else
                                        MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
