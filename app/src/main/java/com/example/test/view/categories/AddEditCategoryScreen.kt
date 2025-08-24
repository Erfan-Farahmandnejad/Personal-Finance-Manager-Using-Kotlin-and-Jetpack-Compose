package com.example.test.view.categories

// Import for border effects
import androidx.compose.foundation.BorderStroke
// Import layout components
import androidx.compose.foundation.layout.*
// Import scrolling functionality
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Import material icons
import androidx.compose.material.icons.Icons
// Import Material 3 components
import androidx.compose.material3.*
// Import Compose runtime components
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
// Import UI alignment components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// Import Hilt view model support
import androidx.hilt.navigation.compose.hiltViewModel
// Import category type enum
import com.example.test.model.CategoryType
// Import category editor view model
import com.example.test.viewmodel.AddEditCategoryViewModel
// Import background drawing capability
import androidx.compose.foundation.background
// Import custom app components and theme colors
import com.example.compose.*

// Screen for adding a new category or editing an existing one
@Composable
fun AddEditCategoryScreen(
    viewModel: AddEditCategoryViewModel = hiltViewModel(), // View model for category editing
    categoryId: Int? = null,                              // ID of category to edit (null for new category)
    onSave: () -> Unit                                    // Callback for when category is saved
) {
    // If editing an existing category, load its data when the screen opens
    LaunchedEffect(categoryId) {
        categoryId?.let { viewModel.loadCategory(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = lightBackground
                    ),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (categoryId == null) "Create Category" else "Edit Category",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        TextField(
                            value = viewModel.name,
                            onValueChange = { viewModel.name = it },
                            label = { Text("Category Name", color = lightText) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = cardBackground,
                                focusedContainerColor = cardBackground,
                                unfocusedTextColor = textColor,
                                focusedTextColor = textColor,
                                cursorColor = primaryColor,
                                focusedIndicatorColor = primaryColor,
                                unfocusedIndicatorColor = borderColor
                            )
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Category Type",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryType.values().forEach { categoryType ->
                                    FilterChip(
                                        selected = viewModel.type == categoryType,
                                        onClick = { viewModel.type = categoryType },
                                        label = { Text(categoryType.name, color = textColor) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Display error message if any
                        viewModel.errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.saveCategory(onSave) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text(if (categoryId == null) "Create Category" else "Save Changes")
                        }
                    }
                }
            }
        }
    }
}
