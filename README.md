# NutriTrack

**Things learnt**

Kotlin language (opt-in nullability, null checks, data class, sealed class, coroutines)

Basic android concepts. https://wideskills.com/android/overview-android/principal-ingredients-android

MVVM (Model, View, ViewModel) architecture.

Activities, Services, broadcast receivers, content providers.

Explicit and implicit intents.

https://miro.medium.com/max/1400/1*-yY0l4XD3kLcZz0rO1sfRA.png

using Repo to manage remote services like nutracheck API

Webscraping nutracheck with JSoup (String parsing, CSS selectors, DOM, tags, classes)

Lazy loading with paging 3.0 (page sizing, virtual paging).

Database with Room 2.3.0 (Entity, Dao, Database, Typeconverters, Foreign keys)

Dependancy injection with Hilt/Dagger (Injection scope i.e Singleton, activity, fragment, navGraph)

ViewModel factories.

HTTP requests with retrofit (status codes, 200 OK, 404 not found)

JSON parsing, HTML parsing.

Loading images from url with Coil.

Android Resource ids for localisation (string store)

Handling failure states with exceptions (try-catch)

Observer pattern with LiveData and MutableStateFlow.

Databinding for XML layouts.

Async tasks with FLow and coroutines.

Cancellable jobs.

Coroutines scopes (i.e. Global, ViewModel) and context (i.e. Default, IO, Main).

Keeping UI thread free with coroutines.

Single source of truth for diary log with Room database.

Unidirectional state with declaritive compose UI.

Composables and previews.

Composable recomposition (mutableStateOf to watch for data changes)

State hoisting in composables for testability.

Navigation (navbar, url parameters, backstack, nav graph).

Android manifest (permissions, features, intents, deep links).

Android lifecycles (i.e. OnCreate, OnStart, OnPause, onDestroy)

Single activity and multiple fragments vs multiple activity.

Viewmodel lifecyle (onCreate, onDestroy)

Implications of rotations, use savedInstanceBundle or rememberSavables.

Modifier parameters and themeing (backgrounds, html colors, darkmode, rounded corners)

Text input widgets and validation (clickabled modifier, on value change callbacks)

Referenced Example Garden App:

    Foundation - Components for core system capabilities, Kotlin extensions and support for multidex and automated testing.
        AppCompat - Degrade gracefully on older versions of Android.
        Android KTX - Write more concise, idiomatic Kotlin code.
        
    Architecture - A collection of libraries that help you design robust, testable, and maintainable apps. Start with classes for managing your UI component lifecycle and handling data persistence.
        Data Binding - Declaratively bind observable data to UI elements.
        Lifecycles - Create a UI that automatically responds to lifecycle events.
        LiveData - Build data objects that notify views when the underlying database changes.
        Navigation - Handle everything needed for in-app navigation.
        Room - Access your app's SQLite database with in-app objects and compile-time checks.
        ViewModel - Store UI-related data that isn't destroyed on app rotations. Easily schedule asynchronous tasks for optimal execution.
        WorkManager - Manage your Android background jobs.
        
    UI - Details on why and how to use UI Components in your apps - together or separate
        Animations & Transitions - Move widgets and transition between screens.
        Fragment - A basic unit of composable UI.
        Layout - Lay out widgets using different algorithms.
        
    Third party and miscellaneous libraries
        Glide for image loading
        Hilt: for dependency injection
        Kotlin Coroutines for managing background threads with simplified code and reducing needs for callbacks
