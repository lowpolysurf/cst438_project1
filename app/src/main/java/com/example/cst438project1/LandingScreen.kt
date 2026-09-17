package com.example.cst438project1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.cst438project1.database.MediaRepository
import java.net.URL

@Composable
fun LandingScreen(
    username: String,
    onLogout: () -> Unit,
    onOpenWatchlist: () -> Unit
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
    var selectedMedia by remember { mutableStateOf<SimklMedia?>(null)}

    // --- Lucky Search feature state ---
    var isLuckyLoading by remember { mutableStateOf(false) }
    var luckySuggestion by remember { mutableStateOf<SimklMedia?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Watchlist titles for this user, so cards know to show "Add" or "Remove"
    val watchlistItems by mediaRepository.getWatchlistForUser(username).observeAsState(emptyList())
    val watchlistTitles = remember(watchlistItems) {watchlistItems.map{ it.mediaTitle }.toSet()}

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

                    // Opens WatchlistScreen for this user
                    TextButton(onClick = onOpenWatchlist) {
                        Text("My Watchlist")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

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

                            val isInWatchlist = watchlistTitles.contains(media.title)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable{ selectedMedia = media}
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Watchlist toggle button on every card
                                    Button(onClick = {
                                        if (isInWatchlist) {
                                            mediaRepository.removeFromWatchlist(mediaTitle = media.title, username = username)
                                        } else {
                                            mediaRepository.addToWatchlist(media, username)
                                        }
                                    }) {
                                        Text(if (isInWatchlist) "Remove from Watchlist" else "Add to Watchlist")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedMedia?.let{ media->
        MediaInfoDialog(
            media = media,
            isInWatchlist = watchlistTitles.contains(media.title),
            onToggleWatchlist = {
                if(watchlistTitles.contains(media.title)){
                    mediaRepository.removeFromWatchlist(media.title, username)
                }
                else{
                    mediaRepository.addToWatchlist(media, username)
                }
            },
            onDismiss = {selectedMedia = null}
        )
    }
}

@Composable
private fun MediaInfoDialog(
    media: SimklMedia,
    isInWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Media Info",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Exit")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            MediaPoster(
                                posterPath = media.poster,
                                title = media.title,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(180.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = media.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Year: ${media.year ?: "Unknown"}")
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(media.overview ?: "No description is currently available for this media.")
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = onToggleWatchlist) {
                                Text(if (isInWatchlist) "Remove from Watchlist" else "Add to List")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaPoster(
    posterPath: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    val posterBitmap by produceState<Bitmap?>(initialValue = null, posterPath) {
        value = posterPath?.takeIf { it.isNotBlank() }?.let { path ->
            withContext(Dispatchers.IO) {
                runCatching {
                    URL("https://simkl.in/posters/${path}_m.jpg")
                        .openStream()
                        .use(BitmapFactory::decodeStream)
                }.getOrNull()
            }
        }
    }

    if (posterBitmap != null) {
        Image(
            bitmap = posterBitmap!!.asImageBitmap(),
            contentDescription = "Poster for $title",
            modifier = modifier
        )
    } else {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("No image")
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