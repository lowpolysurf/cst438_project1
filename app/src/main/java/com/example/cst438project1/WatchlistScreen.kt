package com.example.cst438project1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cst438project1.database.MediaRepository

@Composable
fun WatchlistScreen(username: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val mediaRepository = remember {
        MediaRepository.getRepository(context.applicationContext as android.app.Application)
    }

    val watchlist by mediaRepository.getWatchlistForUser(username).observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = onBack) { Text("< Back") }

        Text(text = "My Watchlist", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (watchlist.isEmpty()) {
            Text("Nothing added so far")
        } else {
            LazyColumn {
                items(watchlist, key = { it.mediaTitle }) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = item.mediaTitle,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                item.year?.let { Text("Year: $it") }
                            }
                            TextButton(onClick = {
                                mediaRepository.removeFromWatchlist(item.mediaTitle, username)
                            }) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }
        }
    }
}