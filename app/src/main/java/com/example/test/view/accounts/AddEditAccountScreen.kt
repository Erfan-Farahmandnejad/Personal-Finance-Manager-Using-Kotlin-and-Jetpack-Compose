package com.example.test.view.accounts

// Import layout components
import androidx.compose.foundation.layout.*
// Import scroll functionality
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Import keyboard handling for input fields
import androidx.compose.foundation.text.KeyboardOptions
// Import material icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
// Import Material 3 components
import androidx.compose.material3.*
// Import Compose runtime components
import androidx.compose.runtime.*
// Import UI components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// Import keyboard input types
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
// Import Hilt dependency injection
import androidx.hilt.navigation.compose.hiltViewModel
// Import account type enum
import com.example.test.model.AccountType
// Import custom dropdown component
import com.example.test.ui.components.DropdownMenuBox
// Import account editor view model
import com.example.test.viewmodel.AddEditAccountViewModel
// Import side effect handling
import androidx.compose.runtime.LaunchedEffect

// Screen for creating a new account or editing an existing account
// Uses experimental Material 3 API for advanced UI components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountScreen(
    accountId: Int? = null,                              // ID of account to edit (null for new account)
    onSave: () -> Unit,                                  // Callback for when save operation completes
    viewModel: AddEditAccountViewModel = hiltViewModel()  // View model for account editing
) {
    // If editing an existing account, load its data when the screen opens
    LaunchedEffect(accountId) {
        accountId?.let { viewModel.loadAccount(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (accountId == null) "Create Account" else "Edit Account") },
                navigationIcon = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Account Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.balance,
                onValueChange = { viewModel.balance = it },
                label = { Text("Initial Balance") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenuBox(
                selectedItem = viewModel.type.name,
                options = AccountType.entries.map { it.name },
                onSelected = { viewModel.type = AccountType.valueOf(it) },
                label = "Account Type",
                labelFor = { it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.saveAccount {
                        onSave()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (accountId == null) "Create Account" else "Update Account")
            }
        }
    }
}
