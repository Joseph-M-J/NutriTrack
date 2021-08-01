package com.example.nutritrack.data.model

import androidx.room.*
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entry: FoodEntity)

    @Query("SELECT * FROM food")
    fun getAll(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food WHERE category = :category")
    fun getByCategory(category: MealCategory): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FoodEntity>)

    @Delete
    fun delete(entry: FoodEntity)
}