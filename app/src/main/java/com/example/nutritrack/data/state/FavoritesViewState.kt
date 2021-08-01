package com.example.nutritrack.data.state

import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.util.MealCategory

data class FavoritesViewState(
    val selectedCategory: MealCategory? = null,

    val favorites: Map<MealCategory, List<FoodEntity>> = MealCategory
        .values()
        .map { it to emptyList<FoodEntity>() }
        .toMap()
)
