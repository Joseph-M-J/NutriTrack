package com.example.nutritrack.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.nutritrack.util.MealCategory

val mealCategoryColors = mapOf(
    MealCategory.BREAKFAST to Color(0.192f, 0.525f, 0.788f, 0.749f),
    MealCategory.LUNCH     to Color(0.125f, 0.714f, 0.647f, 0.749f),
    MealCategory.DINNER    to Color(0.518f, 0.761f, 0.122f, 0.749f),
    MealCategory.SNACKS    to Color(0.761f, 0.655f, 0.133f, 0.749f)
)