package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Displays after successful account creation and login
@Composable
fun LandingScreen(
    // Username passed from the login navigation route.
    username: String,

    // Returns the user to LoginScreen through the navigation callback.
    onLogout: () -> Unit
) {
    // Stores text typed into the search bar. This is the only active search behavior for now.
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Username header: displays the logged-in username and keeps logout at the top right.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Static username display: will receive the username from the future database-backed source.
                    Text(
                        text = username,
                        modifier = Modifier.weight(1f)
                    )

                    // Logout button: calls the navigation callback to return to LoginScreen.
                    Button(onClick = onLogout) {
                        Text("Log Out")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search bar: accepts a media search query without performing a search yet.
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search media") },
                    singleLine = true
                )

                // Search button: can be pressed now; connect this callback to media search logic later.
                Button(
                    onClick = {
                        // TODO: Send searchQuery to a ViewModel/repository and display the media results here.
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Search Media")
                }

                // Page-break divider: visually separates search controls from the media list section.
                HorizontalDivider()

                // Media list title: a borderless static heading for future search results.
                Text(
                    text = "Media List",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Scrollable media list: will contain the media listings the user selects in the future.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Empty-state message: shown until future media-listing data is available.
                    item {
                        Text("Selected media will appear here.")
                    }

                    // TODO: Add selected media items from the future data source to this LazyColumn.
                }
            }
        }
    }
}
