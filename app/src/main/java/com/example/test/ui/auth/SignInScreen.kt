package com.example.test.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.test.viewmodel.SignInViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.signIn {
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate("signup")
        }) {
            Text("Don't have an account? Sign Up")
        }
    }
}
