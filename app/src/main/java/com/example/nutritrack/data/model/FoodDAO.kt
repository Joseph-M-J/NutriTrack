package com.example.nutritrack.data.model

import androidx.room.*
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun save(entry: FoodEntity)

    @Query("SELECT * FROM food")
    fun getAll(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food WHERE (title || img_res) in (:ids)")
    fun getAllById(ids: List<String>): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveAll(entries: List<FoodEntity>)

    @Delete
    fun delete(entry: FoodEntity)
}