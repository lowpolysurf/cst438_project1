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
import com.example.cst438project1.database.entities.User

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val context = LocalContext.current

    // Provides access to database operations: Lookup and add users
    val repository = remember {
        UserRepository.getRepository(
            context.applicationContext as android.app.Application
        )
    }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Checks username after register button is pressed
    var usernameToCheck by remember { mutableStateOf<String?>(null) }

    // Looks at database for a user with that username
    val existingUser by (usernameToCheck?.let {
        repository.getUserByUsername(it) })
        ?.observeAsState()
        ?: remember { mutableStateOf(null) }

    // Executes after username lookup is found
    LaunchedEffect(existingUser, usernameToCheck) {
        // Exit until Register requests a username check
        val checkedUsername = usernameToCheck ?: return@LaunchedEffect

        if (existingUser != null) {
            // Username already in database
            errorMessage = "That username is already taken"
        } else {
            // No username found, so create and store new user
            repository.insertUser(
                User(
                    username = checkedUsername,
                    password = password
                )
            )

            // Returns to LoginScreen.kt
            errorMessage = null
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.headlineMedium
        )
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

        // Password field
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
                // Validates user input
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Username and password are required"
                } else {
                    // Looks up username in database
                    usernameToCheck = username.trim()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}