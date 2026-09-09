package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Displays after successful account creation and login
@Composable
fun LandingScreen(
    // Username passed from the login navigation route.
    username: String,

    // Returns the user to LoginScreen through the navigation callback.
    onLogout: () -> Unit
) {
    // Stores text typed into the search bar.
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Stores SIMKL media results for display in the scrollable media list.
    var searchResults by remember { mutableStateOf<List<SimklMedia>>(emptyList()) }

    // Tracks search progress and an API error message for feedback in the media list.
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchError by rememberSaveable { mutableStateOf<String?>(null) }
    var hasSearched by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                // Search bar: accepts keywords used to find matching media in SIMKL.
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search media") },
                    singleLine = true
                )

                // Search button: requests matching movie, TV, and anime media objects from the SIMKL API.
                Button(
                    onClick = {
                        val query = searchQuery.trim()
                        coroutineScope.launch {
                            isSearching = true
                            searchError = null
                            hasSearched = true

                            try {
                                // Network calls run off the main thread, then Compose redraws with the returned results.
                                searchResults = withContext(Dispatchers.IO) {
                                    listOf("movie", "tv", "anime")
                                        .map { type ->
                                            async {
                                                SimklClient.api.searchMedia(
                                                    type = type,
                                                    query = query,
                                                    clientId = SimklClient.CLIENT_ID
                                                )
                                            }
                                        }
                                        .awaitAll()
                                        .flatten()
                                        .distinctBy { "${it.title}-${it.year}-${it.ids?.simkl}" }
                                }
                            } catch (exception: Exception) {
                                searchResults = emptyList()
                                searchError = "Unable to load media. Please try again."
                            } finally {
                                isSearching = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = searchQuery.isNotBlank() && !isSearching
                ) {
                    Text("Search Media")
                }

                // Page-break divider: visually separates search controls from the media list section.
                HorizontalDivider()

                // Media list title: identifies the area containing SIMKL search results.
                Text(
                    text = "Media List",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Scrollable media list: displays matching SIMKL media objects as title-and-year cards.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        // Loading indicator: shown while the SIMKL API request is in progress.
                        isSearching -> item {
                            CircularProgressIndicator()
                        }

                        // Error message: shown if the SIMKL request could not be completed.
                        searchError != null -> item {
                            Text(searchError!!)
                        }

                        // Empty-state message: shown before a search or when no SIMKL media matches the keywords.
                        searchResults.isEmpty() -> item {
                            Text(
                                if (hasSearched) "No matching media found."
                                else "Search results will appear here."
                            )
                        }

                        // Search result cards: each card displays the title and year from a SIMKL media object.
                        else -> items(
                            items = searchResults,
                            key = { media -> "${media.title}-${media.year}-${media.ids?.simkl}" }
                        ) { media ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = media.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("Year: ${media.year ?: "Unknown"}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
