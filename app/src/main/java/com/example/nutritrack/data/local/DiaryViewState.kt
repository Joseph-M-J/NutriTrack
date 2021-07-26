package com.example.nutritrack.data.local

import com.example.nutritrack.util.DailyLog
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.MealCategory

data class DiaryViewState(

    val currentLog: DailyLog = MealCategory
        .values()
        .map { it to emptyList<LogEntry>() }
        .toMap(),

    val subTotalKcal: Map<MealCategory, Float> = MealCategory
        .values()
        .map { it to 0.0f }
        .toMap(),

    val totalKcal: Float = 0.0f

//    val breakfast: List<LogEntry> = emptyList(),
//    val lunch: List<LogEntry> = emptyList(),
//    val dinner: List<LogEntry> = emptyList(),
//    val snacks: List<LogEntry> = emptyList()
)
