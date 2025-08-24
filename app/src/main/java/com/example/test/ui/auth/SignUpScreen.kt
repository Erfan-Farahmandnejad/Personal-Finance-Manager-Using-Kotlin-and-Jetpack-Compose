package com.example.test.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.test.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.signUp {
                    onSignUpSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate("signin")
        }) {
            Text("Already have an account? Sign In")
        }

        viewModel.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
