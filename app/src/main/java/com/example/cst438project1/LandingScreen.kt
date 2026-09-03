package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Displays after successful account creation and login
@Composable
fun LandingScreen(
    // Username passed
    username: String,

    // Method from MainActivity.kt that returns to LoginScreen.kt
    onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You're logged in, $username!",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Calls MainActivity's logout function
        Button(onClick = onLogout) {
            Text("Log Out")
        }
    }
}