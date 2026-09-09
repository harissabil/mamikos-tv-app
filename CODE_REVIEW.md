# Code Review

```kotlin
class MovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()

    fun loadMovies() {
        val url = URL("https://api.example.com/movies")
        val data = url.readText()
        movies = parseMovies(data)
    }
}
```

## 1. State is not observable

```kotlin
var movies: List<Movie> = emptyList()
```

Compose can't observe a plain `var`, so nothing recomposes when it changes. The data can load fine and the screen still shows an empty list. It's also public `var`, so anything outside can overwrite the state.

```kotlin
private val _movies = MutableStateFlow<List<Movie>>(emptyList())
val movies: StateFlow<List<Movie>> = _movies.asStateFlow()
```

`mutableStateOf` with `private set` also works. I prefer StateFlow, it keeps the ViewModel free of Compose and is easier to test.

## 2. Runs on the main thread

```kotlin
fun loadMovies() {
```

No coroutine, so this runs on whoever calls it, which is the main thread. Frozen UI and `NetworkOnMainThreadException`. Marking it `suspend` alone doesn't fix it either, a suspend function still runs on the caller's thread. It needs a scope and a dispatcher.

```kotlin
fun loadMovies() {
    viewModelScope.launch {
        ...
    }
}
```

`viewModelScope` also cancels the work when the ViewModel is cleared.

## 3. Network call inside the ViewModel

```kotlin
val url = URL("https://api.example.com/movies")
val data = url.readText()
```

This is data layer work. The URL is hardcoded so there's no way to inject a fake or point at staging, and the ViewModel can't be unit tested without hitting the real network.

```kotlin
class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel()
```

Repository as an interface, Retrofit call and DTO mapping behind it, base URL in the DI graph.

## 4. No error handling

```kotlin
val data = url.readText()
movies = parseMovies(data)
```

`readText()` throws on any DNS failure or timeout, `parseMovies` throws on bad JSON. Nothing catches either, so the app crashes. Offline should be a normal path, not a crash.

```kotlin
try {
    _uiState.value = MovieListUiState.Success(repository.getMovies())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    _uiState.value = MovieListUiState.Error(e.toUserMessage())
}
```

Not `runCatching`, it catches `Throwable` including `CancellationException` and would silently swallow coroutine cancellation.

## 5. No loading or error state

```kotlin
var movies: List<Movie> = emptyList()
```

The only thing this can say is "here's a list, maybe empty". The screen can't tell loading from loaded-but-empty from failed.

```kotlin
sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Error(val message: String) : MovieListUiState
    data class Success(val movies: List<Movie>) : MovieListUiState
}
```

## 6. Repeated calls are not guarded

```kotlin
fun loadMovies() {
```

Call it twice and you get two requests in flight, with the last one wins.

```kotlin
private var loadJob: Job? = null

fun loadMovies() {
    if (loadJob?.isActive == true) return
    loadJob = viewModelScope.launch { ... }
}
```

## Final code

```kotlin
class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadMovies()
    }

    fun loadMovies() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _uiState.value = MovieListUiState.Loading
            try {
                _uiState.value = MovieListUiState.Success(repository.getMovies())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = MovieListUiState.Error(e.toUserMessage())
            }
        }
    }
}
```

Or with a reactive approach, where the job guard and the manual state writes go away:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val refresh = MutableStateFlow(0)

    val uiState: StateFlow<MovieListUiState> = refresh
        .flatMapLatest {
            flow { emit(repository.getMovies()) }
                .map<List<Movie>, MovieListUiState> { MovieListUiState.Success(it) }
                .onStart { emit(MovieListUiState.Loading) }
                .catch { emit(MovieListUiState.Error(it.toUserMessage())) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MovieListUiState.Loading
        )

    fun retry() {
        refresh.value++
    }
}
```