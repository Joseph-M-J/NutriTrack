package com.example.nutritrack.data.state

import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.util.MealCategory

data class FavoritesViewState(
    val selectedCategory: MealCategory? = null,

    val categoryTotals: Map<MealCategory, Int> = emptyMap(),

    val favorites: List<FoodEntity> = emptyList()
)
