package com.example.nutritrack.data.model

import androidx.room.*
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun save(entity: FavoritesEntity)

    @Query("SELECT * FROM favorites")
    fun getAll(): Flow<List<FavoritesEntity>>

    @Query("SELECT COUNT(*) FROM favorites WHERE food_title = :title AND food_img_res = :imgRes")
    fun getTotalForFood(title: String, imgRes: String): Int

    @Query("SELECT category, COUNT(*) AS count FROM favorites GROUP BY category")
    fun getTotals(): Flow<List<CategoryCount>>

    @Query("SELECT * FROM favorites WHERE category = :category")
    suspend fun getByCategory(category: MealCategory): List<FavoritesEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveAll(entities: List<FavoritesEntity>)

    @Delete
    fun delete(entity: FavoritesEntity)
}

data class CategoryCount(
    @ColumnInfo(name = "category") val category: MealCategory,
    @ColumnInfo(name = "count") val count: Int
)