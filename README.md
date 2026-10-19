# GIF Search

An Android app for searching GIFs through the [Giphy API](https://developers.giphy.com/docs/api/).

## Setup

1. Get an API key at <https://developers.giphy.com/dashboard/> (Create an App → API).
2. Add it to `local.properties` in the project root (this file is not committed):
   ```properties
   GIPHY_API_KEY=your_key_here
   ```
3. Open the project in Android Studio and run the `app` configuration.

> The project uses Gradle 8.11, which needs **JDK 17–23**. If sync fails with
> "Incompatible Gradle JVM version", set *Settings → Build Tools → Gradle → Gradle JDK* to JDK 21.

Optimized release build (R8 minification + resource shrinking, signed with the debug key for local install):

```bash
./gradlew assembleRelease
```

## Tech stack

Kotlin · Jetpack Compose · MVVM · Hilt · Retrofit + kotlinx.serialization · Coroutines/Flow · Paging 3 · Coil 3 (animated GIF decoding) · Navigation Compose

## Architecture

```
:app              Application, MainActivity, navigation graph, Coil setup
:core:ui          Theme (light/dark), shared composables
:core:network     Retrofit/OkHttp setup, API key interceptor, connectivity observer
:domain           Models and repository interface (pure Kotlin, no Android)
:data             Giphy API, DTOs, mappers, PagingSource, repository implementation
:feature:search   Search screen + ViewModel
:feature:details  Details screen + ViewModel
```

Dependencies point inward: features → domain ← data. Features don't know about Retrofit or DTOs.

## Status

All primary requirements are implemented:

- Auto search with a 400 ms debounce (trimmed, duplicate and blank queries skipped)
- Paging 3 pagination, stopping at the end of results and at Giphy's 4999 offset limit
- Animated GIFs in an adaptive grid (more columns in landscape), details screen on tap
- Loading indicators for the first load, query changes and next pages
- Error states with Retry; offline banner and automatic retry when the connection returns
- Light and dark theme (dynamic color on Android 12+)
- `@Preview` (light + dark) for both screens
- Unit tests: `GifPagingSource` (paging, end of list, offset cap, errors) and `SearchViewModel` (debounce)

Bonus: Clean Architecture layers, multi-module, offline indication, minified release build (`./gradlew assembleRelease`).

Run tests with `./gradlew testDebugUnitTest`.

### Known limitations

- Grid items have no stable keys: Giphy can return the same GIF on two pages, and duplicate keys crash a lazy grid.
- "Nothing found" may flash for ~400 ms while the first query is debounced.
