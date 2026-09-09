# TV App

A simple two-screen TV show browser built with Jetpack Compose, backed by the
[TVMaze API](https://api.tvmaze.com). Browse a paged grid of shows, tap one to see its
details (poster, summary, genres, season/episode counts, cast) and share it.

**Walkthrough video:** <link>

## Screenshots

| List | Detail | Loading | Error |
|------|--------|---------|-------|
| _(screenshot)_ | _(screenshot)_ | _(screenshot)_ | _(screenshot)_ |

## How to run

```bash
git clone <repo-url>
cd tv-app
./gradlew installDebug
```

Or open the project in Android Studio and run the `app` configuration.

Tests:

```bash
./gradlew test
```

To see the error state, turn on airplane mode before opening the app, or open a detail
screen and hit retry with no connection.

## Architecture

Clean architecture, one app module, three layers plus a small `di`:

- **`domain`**: plain Kotlin. Models (`Show`, `ShowDetail`, `CastMember`), the
  `ShowRepository` interface, and two use cases (`GetShowsUseCase`,
  `GetShowDetailUseCase`). Nullable wherever the API is genuinely nullable, so the UI,
  not the model, decides how to show a gap.
- **`data`**: `TvMazeApi` (Retrofit), DTOs that never leave the package, mappers from DTO
  to domain (Jsoup turns the HTML `summary` into plain text), `ShowsPagingSource` (a 404
  means "end of list", not a failure) and `ShowRepositoryImpl`.
- **`presentation`**: Compose screens, one ViewModel per screen exposing immutable state.
  `ShowListViewModel` exposes `Flow<PagingData<Show>>`. `ShowDetailViewModel` exposes a
  `StateFlow<ShowDetailUiState>` (`Loading` / `Error` / `Success`) driven by a retry
  stream with `flatMapLatest` and `stateIn`, so there is no `init` block and the load is
  easy to trigger from a test.

`ShowRepository` is the only interface in the app. The use cases are concrete classes,
since there is exactly one implementation of each and an interface there would just be
indirection. The repository interface earns its place because it keeps `domain`
independent and lets the ViewModel test use a handwritten fake instead of a network
stack. `getShowDetail` throws on failure rather than returning a `Result` or `Either`,
and the ViewModel catches it and maps it to an `Error` state.

`domain` does import `androidx.paging`, which is not strictly clean. That is a
deliberate trade-off for an app this size: `getShows()` returns `Flow<PagingData<Show>>`
directly instead of writing a custom paging wrapper just to avoid one import.

The detail id travels as a Navigation 3 `NavKey` rather than through `SavedStateHandle`.
Navigation 3 does not populate `SavedStateHandle` from route args, that was a Navigation
2 mechanism, so the id reaches `ShowDetailViewModel` through Koin `parametersOf(route)`.

## Technology

- Kotlin, Jetpack Compose, Material 3
- Navigation 3, single activity, edge-to-edge
- Koin for dependency injection
- Retrofit + OkHttp + kotlinx.serialization
- Paging 3 + paging-compose
- Coil 3 for images
- Jsoup for HTML to plain text
- JUnit4, kotlinx-coroutines-test, Turbine

## What I'd improve with more time

- Room as a local cache so the list survives a cold start offline, instead of showing the
  error state.
- ktlint and a small CI workflow so formatting and tests run on every push.
- Search and genre filtering on the list, which the API supports.

## Other documents

- [AI_LOG.md](AI_LOG.md)
- [CODE_REVIEW.md](CODE_REVIEW.md)
- [REFLECTION.md](REFLECTION.md)
