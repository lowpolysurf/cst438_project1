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
    // Methods from MainActivity
    // Called after login is successful
    onLoginSuccess: (username: String) -> Unit,

    // Called after user enters registration screen
    onNavigateToRegister: () -> Unit
) {
    // Gets context needed to access database
    val context = LocalContext.current

    // Creates and remember repository
    // Repository communicates with user database
    val repository = remember { UserRepository.getRepository(context.applicationContext as android.app.Application) }

    // State variables, changing one redraws the current UI
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Triggers database lookup after "log in" button is pressed
    var usernameToCheck by remember { mutableStateOf<String?>(null) }

    // Looks up user when usernameToCheck has a value
    val userResult by (usernameToCheck?.let {
        repository.getUserByUsername(it) })
        ?.observeAsState() // Converts LiveData into Compose state
        // If there is no lookup, use null
        ?: remember { mutableStateOf(null) }

    // Runs when database result or requested username changes
    LaunchedEffect(userResult, usernameToCheck) {
        // Do nothing until log in button is pressed
        if (usernameToCheck == null) return@LaunchedEffect
        when {
            // Username not found
            userResult == null -> {
                errorMessage = "Username not found"
            }
            // If the username exists and password is wrong
            userResult!!.password != password -> {
                errorMessage = "Incorrect password"
            }
            // Username and Password match
            else -> {
                errorMessage = null
                onLoginSuccess(userResult!!.username)
            }
        }
    }
    // UI arrangement
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(), // Hides typed password
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validates user input before querying the database
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Username and password are required"
                } else {
                    // Removes accidental spaces in username
                    // Updating this triggers database lookup
                    usernameToCheck = username.trim()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Opens RegistrationScreen.kt
        TextButton(onClick = onNavigateToRegister) {
            Text("Sign up Here!")
        }
    }
}