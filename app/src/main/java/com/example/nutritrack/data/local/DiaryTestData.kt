package com.example.nutritrack.data.local

import com.example.nutritrack.util.MealCategory

val testLog = mutableMapOf(
    MealCategory.BREAKFAST to mutableListOf(
        Pair("Milk", 120f),
        Pair("Cereal", 302.1f)
    ),
    MealCategory.LUNCH to mutableListOf(
        Pair("Pizza", 300.05f),
        Pair("Chips", 252.1f),
        Pair("Cola", 120f)
    ),
    MealCategory.DINNER to mutableListOf(
        Pair("Pasta", 380f),
        Pair("Cereal", 302.1f)
    ),
    MealCategory.SNACKS to mutableListOf(
        Pair("Crisps", 135f),
        Pair("Sausage Rolls", 302.1f)
    )
)