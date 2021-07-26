package com.example.nutritrack.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritrack.util.MealCategory

@Entity(tableName = "logs")
data class LogsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,

    @ColumnInfo(name = "category") val category: MealCategory,

    @ColumnInfo(name = "title") val title: String,

    @ColumnInfo(name = "kcal") val kcal: Float,

    @ColumnInfo(name = "date") val date: String
)
