package com.example.nutritrack.data.local

import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.util.DailyLog
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.MealCategory

data class DiaryViewState(

    val currentLog: DailyLog = MealCategory
        .values()
        .map { it to emptyList<LogsEntity>() }
        .toMap(),

    val displayDate: String = "--/--/--",

    val isToday: Boolean = false,

    val subTotalKcal: Map<MealCategory, Float> = MealCategory
        .values()
        .map { it to 0.0f }
        .toMap(),

    val totalKcal: Float = 0.0f,

    val selectedId: Long = -1,

    val selectedCategory: MealCategory? = null,

    val showQuickAddMenu: Boolean = false
)
