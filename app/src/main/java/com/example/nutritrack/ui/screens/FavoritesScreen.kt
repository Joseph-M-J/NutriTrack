package com.example.nutritrack.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceEvenly
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.data.state.FavoritesViewState
import com.example.nutritrack.data.state.SearchViewState
import com.example.nutritrack.ui.theme.mealCategoryColors
import com.example.nutritrack.util.FoodPreset
import com.example.nutritrack.util.MealCategory
import com.example.nutritrack.viewmodels.FavoritesViewModel
import com.example.nutritrack.viewmodels.SearchViewModel

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun FavoritesScreenContent(
    favoritesViewModel: FavoritesViewModel,
    searchViewModel: SearchViewModel
) {
    val favoritesState by favoritesViewModel.viewState.collectAsState()
    val searchState by searchViewModel.viewState.collectAsState()

    FavoritesScreenHoist(
        favoritesState = favoritesState,
        searchState = searchState,
        loadedEntries = searchState.loadedEntries,
        onSelectCategory = favoritesViewModel::selectCategory,
        onAddItem = { searchViewModel.copyLogEntry(it) },
        onDeleteItem = favoritesViewModel::deleteFavorite
    )
}

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun FavoritesScreenHoist(
    favoritesState: FavoritesViewState,
    searchState: SearchViewState,
    loadedEntries: Int,
    onSelectCategory: (MealCategory?) -> Unit,
    onAddItem: (FoodPreset) -> Unit,
    onDeleteItem: (MealCategory, FoodEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LoadedEntries(
                    modifier = Modifier.align(Alignment.CenterStart),
                    count = loadedEntries
                )

                if (favoritesState.selectedCategory != null) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { onSelectCategory(null) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Favorites overview",
                            modifier = Modifier
                                .size(25.dp)
                        )
                        Text(
                            text = "Leave ${favoritesState.selectedCategory.name}",
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        // Show all categories
        if (favoritesState.selectedCategory == null) {
            val categories: MutableList<MealCategory?> = MealCategory.values().toMutableList()

            if (categories.size % 2 != 0) {
                categories += null
            }

            val evens = categories.filterIndexed{ i, _ -> i % 2 == 0 }
            val odds = categories.filterIndexed{ i, _ -> i % 2 != 0 }

            val pairs = evens zip odds

            LazyColumn {
                pairs.forEach { (cat1, cat2) ->
                    item {
                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            horizontalArrangement = SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            cat1?.let {
                                CategoryTile(
                                    category = it,
                                    itemCount = favoritesState.categoryTotals.getOrElse(cat1) {0},
                                    onSelectCategory = onSelectCategory
                                )
                            }
                            cat2?.let {
                                CategoryTile(
                                    category = it,
                                    itemCount = favoritesState.categoryTotals.getOrElse(cat2) {0},
                                    onSelectCategory = onSelectCategory
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                FoodList(
                    foodList = favoritesState.favorites,
                    currentPage = 0,
                    hasNextPage = false,
                    onChangePage = {},
                    onAddItem = onAddItem,
                    onDeleteItem = { onDeleteItem(favoritesState.selectedCategory, it) }
                )
            }
        }
    }
}

@Composable
fun CategoryTile(
    category: MealCategory,
    itemCount: Int,
    onSelectCategory: (MealCategory) -> Unit
) {
    Surface(
        color = mealCategoryColors[category]?.copy(alpha = 0.75f) ?: Color.Gray,
        shape = RoundedCornerShape(10.dp),
        elevation = 5.dp,
        modifier = Modifier
            .size(150.dp)
            .clickable { onSelectCategory(category) }
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ){
                Text(
                    text = category.name,
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = itemCount.toString(),
                    fontSize = 25.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewFavoritesScreen() {
//    val state by remember { mutableStateOf(FavoritesViewState(
//
//    ))}
//
//    NutriTrackTheme {
//        Scaffold(
//            bottomBar = { BottomNavigation {} }
//        ) { innerPadding ->
//
//            Box(
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize()
//            ) {
//                FavoritesScreenHoist(
//                    favoritesState = state,
//                    loadedEntries = 0,
//                    onSelectCategory = {},
//                    searchState = SearchViewState(),
//                    on
//                )
//            }
//        }
//    }
//}