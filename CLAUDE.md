# TV App

A simple TV show browser app with two screens, using the TVMaze API

## Stack

- Kotlin, Jetpack Compose, Material 3
- Navigation 3 (androidx.navigation3), single activity
- Koin for dependency injection (DSL)
- Retrofit + OkHttp + kotlinx.serialization
- Paging 3 + paging-compose
- Coil 3
- JUnit4, kotlinx-coroutines-test, Turbine
- Gradle Kotlin DSL with a version catalog (libs.versions.toml)

No mocking library. Test doubles are handwritten fakes that implement the real interface.

## API: https://api.tvmaze.com

- `GET /shows?page={page}` returns a JSON array of 250 shows. Page 0 is the first page. Requesting a
  page past the last one returns HTTP 404. That 404 means "no more data", not a failure.
- `GET /shows/{id}` returns one show. Cast and episodes can be pulled in the same call with
  `embed[]=cast&embed[]=episodes`, or from `/shows/{id}/cast` and `/shows/{id}/episodes`.
- Nullable in practice: the whole `image` object, `rating.average`, `summary`, `premiered`,
  `runtime`, `officialSite`.
- `summary` contains HTML tags like `<p>` and `<b>`.
- `url` is the public TVMaze page for the show. That is the link used in the share action.

## Architecture

Clean architecture, three layers in one app module: `domain`, `data`, `presentation`, plus a small
`di`.

- `domain` must not import anything from Android, Retrofit, Paging internals, or the `data` package.
  The dependency arrow always points inward toward domain.
- The repository is an interface declared in `domain`, implemented in `data` as
  `ShowRepositoryImpl`. That is the one abstraction I want, because it is what keeps domain
  independent and what makes the ViewModel tests use a fake instead of a real network stack.
- Use cases are NOT interfaces. They are concrete classes with `operator fun invoke`, one per use
  case. There is exactly one implementation of each and nothing needs to swap them out, so an
  interface there would be noise.
- DTOs live in `data` and never leave it. Mappers convert DTO to domain model.
- ViewModels expose immutable state: a `StateFlow<UiState>` or a `Flow<PagingData<T>>`. No mutable
  state leaking out.

## Code style

- No comments in production code. If a line needs a comment, extract it into a well named function
  or constant instead. Named things like `isEndOfPagination` beat a note explaining a magic branch.
- No dead code, no TODOs left behind, no unused imports.
- Prefer explicit types on public declarations.

## Git

Commit in small pieces as I go, with conventional commit messages (`feat:`, `refactor:`, `test:`,
`chore:`). Never bundle a whole layer into one commit. My commit history is part of the evaluation,
so a single giant commit is a real problem, no claude co-authored commits.

## Working style

- If you need a library that is not listed above, stop and ask me first.
- If you are unsure how an API behaves, verify it with curl or against the official docs instead of
  guessing. This applies especially to Koin and Navigation 3, where the DSL has changed between
  versions.
- At the end of every task, list the decisions you made that I should double check, and anything you
  were genuinely unsure about.