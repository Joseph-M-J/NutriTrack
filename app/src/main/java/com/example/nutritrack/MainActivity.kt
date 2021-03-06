package com.example.nutritrack

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.example.nutritrack.data.FoodRepository
import com.example.nutritrack.modules.AppModule
import com.example.nutritrack.receivers.DateReceiver
import com.example.nutritrack.ui.screens.DiaryScreenContent
import com.example.nutritrack.ui.screens.FavoritesScreenContent
import com.example.nutritrack.ui.screens.SearchScreenContent
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.viewmodels.DiaryViewModel
import com.example.nutritrack.viewmodels.FavoritesViewModel
import com.example.nutritrack.viewmodels.SearchViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val searchViewModel: SearchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T: ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(
                    FoodRepository(AppModule.provideNutracheckService())
                ) as T
            }
        }
    }

    private val diaryViewModel: DiaryViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T: ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DiaryViewModel(
                    AppModule.provideAppDatabase(applicationContext)
                ) as T
            }
        }
    }

    private val favoritesViewModel: FavoritesViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T: ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return FavoritesViewModel(
                    AppModule.provideAppDatabase(applicationContext)
                ) as T
            }
        }
    }

    private lateinit var receiver: DateReceiver

    @ExperimentalCoilApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiver = DateReceiver()

//        IntentFilter(Intent.ACTION_DATE_CHANGED).also {
//            registerReceiver(receiver, it)
//        }

        setContent {
            MainContent(
                searchViewModel = searchViewModel,
                diaryViewModel = diaryViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }
    }

    companion object {
        val startScreen = Screen.Diary
        var currentScreen by mutableStateOf(startScreen)
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun MainContent(
    searchViewModel: SearchViewModel,
    diaryViewModel: DiaryViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    val navController = rememberNavController()

    NutriTrackTheme {
        Scaffold(
            bottomBar = {
                MainNavBar(
                    onNavigate = { navController.navigate(it.name) }
                )
            }
        ) { pad ->

            MainNavHost(
                searchViewModel = searchViewModel,
                diaryViewModel = diaryViewModel,
                favoritesViewModel = favoritesViewModel,
                navController = navController,
                modifier = Modifier.padding(pad)
            )
        }
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun MainNavHost(
    searchViewModel: SearchViewModel,
    diaryViewModel: DiaryViewModel,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = MainActivity.startScreen.name,
        modifier = modifier
    ) {

        composable(Screen.Diary.name) {
            DiaryScreenContent(
                diaryViewModel = diaryViewModel,
                searchViewModel = searchViewModel,
                favoritesViewModel = favoritesViewModel
            )
        }

        composable(Screen.Search.name) {
            SearchScreenContent(
                searchViewModel = searchViewModel
            )
        }

        composable(Screen.Saved.name) {
            FavoritesScreenContent(
                favoritesViewModel = favoritesViewModel,
                searchViewModel = searchViewModel
            )
        }

        composable(Screen.Profile.name) {
            Text("Temp")
        }
    }
}

@Composable
fun MainNavBar(
    onNavigate: (Screen) -> Unit
) {
    BottomNavigation() {
        Screen.values().forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.name + " icon"
                    )
                },
                label = {
                    Text(screen.name)
                },
                selected = screen == MainActivity.currentScreen,
                onClick = {
                    onNavigate(screen)
                    MainActivity.currentScreen = screen
                }
            )
        }
    }
}

//@Preview
//@Composable
//fun DefaultPreview() {
//    NutriTrackTheme {
//        Scaffold(
//            bottomBar = { MainNavBar(onNavigate = {_->}) }
//        ) {
//            Text("Main content")
//        }
//    }
//}