package com.example.test.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuBox(
    selectedItem: T,
    options: List<T>,
    label: String = "",
    labelFor: (T) -> String = { it.toString() },
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        TextField(
            value = labelFor(selectedItem),
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(labelFor(item)) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
