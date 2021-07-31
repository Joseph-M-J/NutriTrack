package com.example.nutritrack.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nutritrack.ui.theme.NutriTrackTheme
import com.example.nutritrack.viewmodels.SearchViewModel

@Composable
fun FavoritesScreenContent(
    searchViewModel: SearchViewModel
) {
    val searchState by searchViewModel.viewState.collectAsState()

    FavoritesScreenHoist(
        loadedEntries = searchState.loadedEntries
    )
}

@Composable
fun FavoritesScreenHoist(
    loadedEntries: Int
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar() {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LoadedEntries(
                    modifier = Modifier.align(Alignment.CenterStart),
                    count = loadedEntries
                )
            }
        }

        //LazyColumn
    }
}

@Preview
@Composable
fun PreviewFavoritesScreen() {
    NutriTrackTheme {
        Scaffold(
            bottomBar = { BottomNavigation {} }
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                FavoritesScreenHoist(loadedEntries = 0)
            }
        }
    }
}