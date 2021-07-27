package com.example.nutritrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.nutritrack.data.FoodRepository
import com.example.nutritrack.modules.AppModule
import com.example.nutritrack.ui.screens.DiaryScreenContent
import com.example.nutritrack.ui.screens.SearchScreenContent
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.viewmodels.DiaryViewModel
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

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent(
                searchViewModel = searchViewModel,
                diaryViewModel = diaryViewModel
            )
        }
    }
    companion object {
        val startScreen = Screen.Diary
        var currentScreen by mutableStateOf(startScreen)
    }
}

@ExperimentalFoundationApi
@Composable
fun MainContent(
    searchViewModel: SearchViewModel,
    diaryViewModel: DiaryViewModel
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
                navController = navController,
                modifier = Modifier.padding(pad)
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MainNavHost(
    searchViewModel: SearchViewModel,
    diaryViewModel: DiaryViewModel,
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
                searchViewModel = searchViewModel
            )
        }

        composable(Screen.Search.name) {
            SearchScreenContent(
                searchViewModel = searchViewModel,
                onAddItem = {
                    Timber.i("Adding: $it")
//                    MainActivity.currentScreen = Screen.Diary
//                    navController.navigate(Screen.Diary.name)
                }
            )
        }

        composable(Screen.Saved.name) {
            Text("Temp")
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

@Preview
@Composable
fun DefaultPreview() {
    NutriTrackTheme {
        Scaffold(
            bottomBar = { MainNavBar(onNavigate = {_->}) }
        ) {
            Text("Main content")
        }
    }
}