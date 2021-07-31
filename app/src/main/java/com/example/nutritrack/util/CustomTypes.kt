package com.example.nutritrack.util

import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.data.model.FoodEntity

typealias FoodResource = RemoteResource<List<FoodEntity>>
typealias LogEntry = Pair<String, Float>
typealias DailyLog = Map<MealCategory, List<LogsEntity>>