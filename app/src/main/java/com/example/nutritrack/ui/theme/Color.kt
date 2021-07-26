package com.example.nutritrack.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.nutritrack.util.MealCategory

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val mealCategoryColors = mapOf(
    MealCategory.BREAKFAST to Color(0.125f, 0.455f, 0.714f, 0.75f),
    MealCategory.LUNCH     to Color(0.125f, 0.714f, 0.647f, 0.749f),
    MealCategory.DINNER    to Color(0.49f, 0.714f, 0.125f, 0.749f),
    MealCategory.SNACKS    to Color(0.714f, 0.616f, 0.125f, 0.749f)
)