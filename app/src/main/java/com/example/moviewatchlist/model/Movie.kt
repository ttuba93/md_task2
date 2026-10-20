package com.example.moviewatchlist.model

enum class Genre(val label: String) {
    ADVENTURE("Adventure"),
    FAMILY("Family"),
    MUSICAL("Musical"),
    COMEDY("Comedy"),
    FANTASY("Fantasy")
}

enum class MovieType(val label: String) {
    ANIMATED("Animated"),
    LIVE_ACTION("Live-action"),
    CHANNEL("Channel movie")
}

data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val genre: Genre,
    val minutes: Int,
    val type: MovieType,
    val watched: Boolean = false,
    val rating: Int = 0
)

sealed class AddResult {
    data class Added(val movie: Movie) : AddResult()
    data class Duplicate(val title: String) : AddResult()
    data class YearOutOfRange(val title: String, val year: Int) : AddResult()
    data class InvalidInput(val reason: String) : AddResult()
}

fun resultMessage(result: AddResult): String = when (result) {
    is AddResult.Added -> "Added: ${result.movie.title}"
    is AddResult.Duplicate -> "'${result.title}' is already in the list"
    is AddResult.YearOutOfRange -> "${result.year} is outside 2000-2015"
    is AddResult.InvalidInput -> result.reason
}

fun sampleMovies(): List<Movie> = listOf(
    Movie(1, "Tangled", 2010, Genre.ADVENTURE, 100, MovieType.ANIMATED),
    Movie(2, "Frozen", 2013, Genre.MUSICAL, 102, MovieType.ANIMATED),
    Movie(3, "Lilo & Stitch", 2002, Genre.FAMILY, 85, MovieType.ANIMATED),
    Movie(4, "Big Hero 6", 2014, Genre.ADVENTURE, 102, MovieType.ANIMATED),
    Movie(5, "Up", 2009, Genre.ADVENTURE, 96, MovieType.ANIMATED),
    Movie(6, "Finding Nemo", 2003, Genre.FAMILY, 100, MovieType.ANIMATED),
    Movie(7, "Inside Out", 2015, Genre.FAMILY, 95, MovieType.ANIMATED),
    Movie(8, "Enchanted", 2007, Genre.FANTASY, 107, MovieType.LIVE_ACTION),
    Movie(9, "Alice in Wonderland", 2010, Genre.FANTASY, 108, MovieType.LIVE_ACTION),
    Movie(10, "High School Musical", 2006, Genre.MUSICAL, 98, MovieType.CHANNEL)
)
