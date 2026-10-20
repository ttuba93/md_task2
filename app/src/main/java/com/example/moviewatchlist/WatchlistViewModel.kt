package com.example.moviewatchlist

import androidx.lifecycle.ViewModel
import com.example.moviewatchlist.model.AddResult
import com.example.moviewatchlist.model.Genre
import com.example.moviewatchlist.model.Movie
import com.example.moviewatchlist.model.MovieType
import com.example.moviewatchlist.model.resultMessage
import com.example.moviewatchlist.model.sampleMovies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WatchlistUiState(
    val movies: List<Movie> = emptyList(),
    val query: String = "",
    val genreFilter: Genre? = null,
    val hideWatched: Boolean = false,
    val message: String? = null,
    val vmCounter: Int = 0
) {
    val visibleMovies: List<Movie>
        get() = movies
            .filter { it.title.contains(query.trim(), ignoreCase = true) }
            .filter { genreFilter == null || it.genre == genreFilter }
            .filter { !hideWatched || !it.watched }
            .sortedBy { it.year }

    val watchedCount: Int get() = movies.count { it.watched }
    val minutesLeft: Int get() = movies.filter { !it.watched }.sumOf { it.minutes }
}
class WatchlistViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState(movies = sampleMovies()))
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    private var nextId = 100

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onGenreFilter(genre: Genre?) {
        _uiState.update { it.copy(genreFilter = genre) }
    }

    fun onHideWatchedChange(hide: Boolean) {
        _uiState.update { it.copy(hideWatched = hide) }
    }

    fun toggleWatched(id: Int, watched: Boolean) {
        _uiState.update { state ->
            state.copy(movies = state.movies.map {
                if (it.id == id) it.copy(watched = watched, rating = if (watched) it.rating else 0) else it
            })
        }
    }

    fun rate(id: Int, rating: Int) {
        _uiState.update { state ->
            state.copy(movies = state.movies.map { if (it.id == id) it.copy(rating = rating) else it })
        }
    }

    fun delete(id: Int) {
        _uiState.update { state -> state.copy(movies = state.movies.filter { it.id != id }) }
    }

    fun addMovie(
        title: String,
        yearText: String,
        minutesText: String,
        genre: Genre,
        type: MovieType
    ): AddResult {
        val cleanTitle = title.trim()
        val year = yearText.toIntOrNull()
        val minutes = minutesText.toIntOrNull()

        val result: AddResult = when {
            cleanTitle.isEmpty() -> AddResult.InvalidInput("Title is empty")
            year == null -> AddResult.InvalidInput("Year must be a number")
            minutes == null || minutes <= 0 -> AddResult.InvalidInput("Minutes must be a positive number")
            year !in 2000..2015 -> AddResult.YearOutOfRange(cleanTitle, year)
            _uiState.value.movies.any { it.title.equals(cleanTitle, ignoreCase = true) } ->
                AddResult.Duplicate(cleanTitle)
            else -> AddResult.Added(Movie(nextId++, cleanTitle, year, genre, minutes, type))
        }

        _uiState.update { state ->
            val newMovies = if (result is AddResult.Added) state.movies + result.movie else state.movies
            state.copy(movies = newMovies, message = resultMessage(result))
        }
        return result
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }

    fun incrementVmCounter() {
        _uiState.update { it.copy(vmCounter = it.vmCounter + 1) }
    }
}
