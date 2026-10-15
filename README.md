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

<!-- Fill in as you go: what's done, what isn't, and what you tried. -->
