package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import com.example.cst438project1.database.UserRepository

@Composable
fun LoginScreen(
    onLoginSuccess: (username: String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { UserRepository.getRepository(context.applicationContext as android.app.Application) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var usernameToCheck by remember { mutableStateOf<String?>(null) }

    val userResult by (usernameToCheck?.let { repository.getUserByUsername(it) })
        ?.observeAsState()
        ?: remember { mutableStateOf(null) }

    LaunchedEffect(userResult, usernameToCheck) {
        if (usernameToCheck == null) return@LaunchedEffect
        when {
            userResult == null -> errorMessage = "No account found with that username"
            userResult!!.password != password -> errorMessage = "Incorrect password"
            else -> {
                errorMessage = null
                onLoginSuccess(userResult!!.username)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome Back", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Username and password are required"
                } else {
                    usernameToCheck = username.trim()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Sign up")
        }
    }
}