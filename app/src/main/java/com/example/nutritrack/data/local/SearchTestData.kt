package com.example.nutritrack.data.local

import com.example.nutritrack.data.remote.FoodInfo

val fakeSearchData = listOf(
    FoodInfo(
        title = "Asda 50% Less Fat Mature Grated British Cheese 200g oispfoijpofjoivmoiv voidv oisdjv oisv svj ods o;asv ofnvofv ofsv ovn",
        imgRes = "13/681013.png",
        portions = listOf("100g", "25g Serving"),
        kcal = listOf(287.0f, 72.0f),
        protein = listOf(32.0f, 8.0f),
        carbs = listOf(1.7f, 0.4f),
        fat = listOf(17.0f, 4.2f)
    ),
    FoodInfo(
        title = "Asda Shredded Iceberg Lettuce 130g",
        imgRes = "88/751988.png",
        portions = listOf("100g", "1/2 pack"),
        kcal = listOf(12.0f, 8.0f),
        protein = listOf(0.6f, 0.4f),
        carbs = listOf(2.0f, 1.3f),
        fat = listOf(0.5f, 0.3f)
    ),
    FoodInfo(
        title = "Asda Semi Skimmed British Milk 2272ml",
        imgRes = "93/565893.png",
        portions = listOf("40ml for Tea/Coffee", "100ml", "125ml for Cereal"),
        kcal = listOf(20.0f, 50.0f, 63.0f),
        protein = listOf(1.4f, 3.6f, 4.5f),
        carbs = listOf(1.9f, 4.8f, 6.0f),
        fat = listOf(0.7f, 1.8f, 2.2f)
    )
)