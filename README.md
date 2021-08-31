# NutriTrack ([Quick Demo](https://youtu.be/BHLMPYgMhw8))

This is a simple calorie tracking Android app that I built for myself and my family, in a very short time frame.

## Knowledge gained

- Kotlin language (opt-in nullability, null checks, data class, sealed class, coroutines).
    
- [Basic Android concepts:](https://wideskills.com/android/overview-android/principal-ingredients-android)
    
    - Activities, Services, broadcast receivers, content providers.
    - Explicit and implicit intents.
    - Single activity and multiple fragments vs multiple activity apps.
    - Android life-cycles:
        - Activity: OnCreate, OnStart, OnPause, onDestroy.
        - ViewModel: onCreate, onDestroy.
        - Managing screen rotations by using `savedInstanceBundle` or `rememberSavable`.
- The model-view-view-model (MVVM) architecture pattern.
    
- [Using repositories to manage data sources](https://miro.medium.com/max/1400/1*-yY0l4XD3kLcZz0rO1sfRA.png).
    
- Web scraping the public [Nutracheck calorie database](https://www.nutracheck.co.uk/CaloriesIn/) with JSoup.
    
    - String parsing, CSS selectors, HTML, the DOM, tags, classes.
- Data persistence with the AndroidX Room database library.
    
    - Annotations:
        
        ```kotlin
        @Entity, @Dao, @Database, @TypeConverters, @Embedded, @Query, @Insert, @Delete
        ```
        
    - SQL queries, primary keys, composite keys, foreign keys (cascade delete)
        
- ViewModel factories.
    
- HTTP requests with Retrofit:
    
    - status codes: 200 - OK, 404 - Not Found, etc.
- JSON parsing, HTML parsing.
    
- Lazily loading images from a remote Url with Coil.
    
- Android Resource IDs for localisation (the string store).
    
- Handling failure states gracefully with exceptions (try-catch).
    
- The observer pattern:
    
    - LiveData and MutableStateFlow.
- Databinding for XML layouts.
    
- Async tasks with flows and coroutines.
    
- Cancellable jobs.
    
- Coroutine scopes (i.e. Global, ViewModel) and dispatcher (i.e. Default, IO, Main).
    
- The importance of keeping the UI thread available.
    
- Using a single source of truth (SSOT) data model for log entries.
    
- Jetpack Compose UI:
    
    - Unidirectional data flow.
    - Declarative UI.
    - `@Composable, @Preview`
- Material Design Principles.
    
- UI Recomposition.
    
    - Watch for data changes with `mutableStateOf(...)`
- Cold vs Hot flows:
    
    - flow (cold) vs StateFlow (hot) and SharedFlow (hot).
- Stateful composables:
    
    - `remember {...}` or `rememberSaveable {...}`
- State hoisting in compose functions for modularity.
    
- Navigation:
    
    - Url parameters, the backstack, nav-graphs.
- Android XML manifest:
    
    - App permissions, features, intents, deep links.
- Modifier parameters and themes:
    
    - Rounded corner shapes, HTML colors, gradient borders and backgrounds.
- Text input widgets and form validation:
    
    - On value change hooks
