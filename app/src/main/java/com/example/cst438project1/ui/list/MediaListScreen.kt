package com.example.cst438project1.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cst438project1.model.MediaObj
import com.example.cst438project1.model.MediaType

@Composable
fun MediaListScreen() {
    val items = remember {
        mutableStateListOf(
            MediaObj(1, "Dune", MediaType.BOOK),
            MediaObj(2, "The Matrix", MediaType.MOVIE),
            MediaObj(3, "Severance", MediaType.TV)
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My List", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(items, key = { it.id }) { item ->
                MediaRow(item = item, onToggle = {
                    val index = items.indexOf(item)
                    items[index] = item.copy(completed = !item.completed)
                })
            }
        }
    }
}

@Composable
fun MediaRow(item: MediaObj, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.completed, onCheckedChange = { onToggle() })
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(item.title, style = MaterialTheme.typography.bodyLarge)
            Text(item.type.name, style = MaterialTheme.typography.labelSmall)
        }
    }
}