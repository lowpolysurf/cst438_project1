package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.cst438project1.database.MediaRepository

@Composable
fun LandingScreen(
    username: String,
    onLogout: () -> Unit
) {
    // Gets the application context
    val context = LocalContext.current

    // Creates the media repository
    val mediaRepository = remember {
        MediaRepository.getRepository(
            context.applicationContext as android.app.Application
        )
    }

    // --- Search feature state ---
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SimklMedia>>(emptyList()) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchError by rememberSaveable { mutableStateOf<String?>(null) }
    var hasSearched by rememberSaveable { mutableStateOf(false) }

    // --- Lucky Search feature state ---
    var isLuckyLoading by remember { mutableStateOf(false) }
    var luckySuggestion by remember { mutableStateOf<SimklMedia?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Username header + logout
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = username, modifier = Modifier.weight(1f))
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
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search media") },
                    singleLine = true
                )

                // Search + Lucky Search buttons side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val query = searchQuery.trim()
                            coroutineScope.launch {
                                isSearching = true
                                searchError = null
                                hasSearched = true

                                try {
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
                        modifier = Modifier.weight(1f),
                        enabled = searchQuery.isNotBlank() && !isSearching
                    ) {
                        Text("Search Media")
                    }

                    Button(
                        onClick = {
                            isLuckyLoading = true
                            luckySuggestion = null
                            coroutineScope.launch {
                                luckySuggestion = LuckySearch.getRandomSuggestion()
                                isLuckyLoading = false
                            }
                        },
                        enabled = !isLuckyLoading
                    ) {
                        Text("🍀 Lucky")
                    }
                }

                if (isLuckyLoading) {
                    CircularProgressIndicator()
                }

                luckySuggestion?.let { media ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = media.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            media.year?.let { year -> Text(text = "Year: $year") }
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Media List",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    when {
                        isSearching -> item { CircularProgressIndicator() }
                        searchError != null -> item { Text(searchError!!) }
                        searchResults.isEmpty() -> item {
                            Text(
                                if (hasSearched) "No matching media found."
                                else "Search results will appear here."
                            )
                        }
                        else -> items(
                            items = searchResults,
                            key = { media -> "${media.title}-${media.year}-${media.ids?.simkl}" }
                        ) { media ->

                            // Gets the average rating for this media
                            val averageRating by remember(media.title) {
                                mediaRepository.getAverageRating(media.title)
                            }.observeAsState()

                            // Gets this user's rating
                            val userRating by remember(media.title, username) {
                                mediaRepository.getUserRating(
                                    media.title,
                                    username
                                )
                            }.observeAsState()

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = media.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Year: ${media.year ?: "Unknown"}")

                                    // Allows the user to select a rating
                                    Text("Your rating:")

                                    StarRating(
                                        rating = userRating ?: 0,
                                        onRatingSelected = { selectedRating ->

                                            // Saves the user's selected rating
                                            mediaRepository.saveRating(
                                                mediaTitle = media.title,
                                                username = username,
                                                rating = selectedRating
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// Displays five selectable stars
@Composable
fun StarRating(
    rating: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row {
        for (star in 1..5) {
            TextButton(
                onClick = {
                    // Saves the selected star number
                    onRatingSelected(star)
                }
            ) {
                Text(
                    text = if (star <= rating) "★" else "☆"
                )
            }
        }
    }
}