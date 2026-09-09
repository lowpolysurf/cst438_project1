package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
//import androidx.compose.runtime.Composable will be replaced by the import below it
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    username: String,
    onLogout: () -> Unit) {
    // variables needed
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var suggestion by remember { mutableStateOf<SimklMedia?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        // space modifier for the button
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "You're logged in, $username!",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            isLoading = true
            suggestion = null
            coroutineScope.launch {
                suggestion = LuckySearch.getRandomSuggestion()
                isLoading = false
            }
        }) {
            Text("🍀 Lucky Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        suggestion?.let { media ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = media.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    media.year?.let { year ->
                        Text(text = "Year: $year")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Log Out")
        }
    }
}
