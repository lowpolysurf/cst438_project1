package com.example.cst438project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cst438project1.ui.theme.Cst438Project1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cst438Project1Theme {
                MediaSearchScreen()
            }
        }
    }
}

@Composable
fun MediaSearchScreen() {
    // Stores text typed into the search bar. This is the only active search behavior for now.
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header: keeps account-related content at the top of the app.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                // Borderless username box: currently displays a placeholder name instead of accepting input.
                // TODO: Replace this value with the username supplied by a future database-backed data source.
                Text(
                    text = "Placeholder Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
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

@Preview(showBackground = true)
@Composable
fun MediaSearchScreenPreview() {
    Cst438Project1Theme {
        MediaSearchScreen()
    }
}
