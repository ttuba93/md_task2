@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.moviewatchlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviewatchlist.WatchlistViewModel

@Composable
fun WatchlistScreen(viewModel: WatchlistViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showDemo by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disney-style Watchlist") },
                actions = {
                    TextButton(onClick = { showDemo = !showDemo }) {
                        Text(if (showDemo) "Hide demo" else "State demo")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add movie")
            }
        }
    ) { padding ->
        val visibleMovies = state.visibleMovies

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SearchField(query = state.query, onQueryChange = viewModel::onQueryChange)
            }
            item {
                GenreFilterRow(selected = state.genreFilter, onSelected = viewModel::onGenreFilter)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hide watched movies")
                    Switch(checked = state.hideWatched, onCheckedChange = viewModel::onHideWatchedChange)
                }
            }
            item {
                StatsRow(
                    total = state.movies.size,
                    watched = state.watchedCount,
                    minutesLeft = state.minutesLeft
                )
            }
            if (showDemo) {
                item {
                    StateDemoCard(vmCounter = state.vmCounter, onVmIncrement = viewModel::incrementVmCounter)
                }
            }
            if (visibleMovies.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No movies match your search or filters")
                    }
                }
            }
            items(visibleMovies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    onWatchedChange = { viewModel.toggleWatched(movie.id, it) },
                    onRate = { viewModel.rate(movie.id, it) },
                    onDelete = { viewModel.delete(movie.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddMovieDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, year, minutes, genre, type ->
                viewModel.addMovie(title, year, minutes, genre, type)
            }
        )
    }
}
