package com.example.nutritrack.data.model

import androidx.room.*
import com.example.nutritrack.util.MealCategory

@Entity(
    tableName = "favorites",
    primaryKeys = ["food_title", "food_img_res", "category"],
    foreignKeys = [
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["title", "img_res"],
            childColumns = ["food_title", "food_img_res"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FavoritesEntity(
    @ColumnInfo(name = "food_title") val title: String,

    @ColumnInfo(name = "food_img_res") val imgRes: String,

    @ColumnInfo(name = "category") val category: MealCategory
)

//data class FoodAndFavorites(
//    @Embedded
//    val food: FoodEntity,
//
//    @Relation(
//        parentColumn = "title",
//        entityColumn = "food_title"
//    )
//    val albums: List<FavoritesEntity>
//)
