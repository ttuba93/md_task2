@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.moviewatchlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.moviewatchlist.model.AddResult
import com.example.moviewatchlist.model.Genre
import com.example.moviewatchlist.model.Movie
import com.example.moviewatchlist.model.MovieType
import com.example.moviewatchlist.model.resultMessage

@Composable
fun MovieCard(
    movie: Movie,
    onWatchedChange: (Boolean) -> Unit,
    onRate: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth().alpha(if (movie.watched) 0.75f else 1f)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterBadge(title = movie.title)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (movie.watched) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = "${movie.year} • ${movie.genre.label} • ${movie.minutes} min",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = movie.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (movie.watched) {
                    RatingStars(rating = movie.rating, onRate = onRate)
                }
            }
            Checkbox(checked = movie.watched, onCheckedChange = onWatchedChange)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete ${movie.title}")
            }
        }
    }
}

@Composable
fun PosterBadge(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun RatingStars(rating: Int, onRate: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (star in 1..5) {
            IconButton(onClick = { onRate(star) }, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rate $star",
                    tint = if (star <= rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Search movies") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun GenreFilterRow(selected: Genre?, onSelected: (Genre?) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelected(null) },
            label = { Text("All") }
        )
        Genre.entries.forEach { genre ->
            FilterChip(
                selected = selected == genre,
                onClick = { onSelected(if (selected == genre) null else genre) },
                label = { Text(genre.label) }
            )
        }
    }
}

@Composable
fun StatsRow(total: Int, watched: Int, minutesLeft: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard("Total", total.toString(), Modifier.weight(1f))
        StatCard("Watched", watched.toString(), Modifier.weight(1f))
        StatCard("Hours left", (minutesLeft / 60).toString(), Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun StateDemoCard(vmCounter: Int, onVmIncrement: () -> Unit, modifier: Modifier = Modifier) {
    var rememberCount by remember { mutableStateOf(0) }          // lost on rotation
    var savedCount by rememberSaveable { mutableStateOf(0) }     // survives rotation
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "State demo: tap the buttons, then rotate the device",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            DemoRow("remember", "resets to 0 after rotation", rememberCount) { rememberCount++ }
            DemoRow("rememberSaveable", "survives rotation", savedCount) { savedCount++ }
            DemoRow("ViewModel", "survives rotation", vmCounter) { onVmIncrement() }
        }
    }
}

@Composable
private fun DemoRow(title: String, description: String, count: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
        FilledTonalButton(onClick = onClick) { Text("Count: $count") }
    }
}

@Composable
fun AddMovieDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, year: String, minutes: String, genre: Genre, type: MovieType) -> AddResult
) {
    var title by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableStateOf("100") }
    var genre by rememberSaveable { mutableStateOf(Genre.FAMILY) }
    var type by rememberSaveable { mutableStateOf(MovieType.ANIMATED) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add movie (2000-2015)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it },
                        label = { Text("Minutes") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Text("Genre", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Genre.entries.forEach {
                        FilterChip(selected = genre == it, onClick = { genre = it }, label = { Text(it.label) })
                    }
                }
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MovieType.entries.forEach {
                        FilterChip(selected = type == it, onClick = { type = it }, label = { Text(it.label) })
                    }
                }
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val result = onConfirm(title, year, minutes, genre, type)
                if (result is AddResult.Added) onDismiss() else error = resultMessage(result)
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
