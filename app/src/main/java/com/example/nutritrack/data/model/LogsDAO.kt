package com.example.nutritrack.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogsDAO {
    @Insert(onConflict = REPLACE)
    fun save(entry: LogsEntity)

    @Query("SELECT * FROM logs")
    fun getAll(): Flow<List<LogsEntity>>

    // GROUP BY category
    @Query("SELECT * FROM logs WHERE date = :date")
    fun getToday(date: String): Flow<List<LogsEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(entries: List<LogsEntity>)

    @Delete
    fun delete(entry: LogsEntity)
}
