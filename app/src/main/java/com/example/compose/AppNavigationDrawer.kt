package com.example.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.test.view.dashboard.DrawerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    drawerItems: List<DrawerItem>,
    onItemClick: (String) -> Unit,
    currentRoute: String = "dashboard", // Default to dashboard if not specified
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Money Tracker",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = item.route == currentRoute,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                onItemClick(item.route)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    ) {
        content()
    }
} 