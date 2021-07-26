package com.example.nutritrack.util

import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.data.remote.FoodInfo

typealias FoodResource = RemoteResource<List<FoodInfo>>
typealias LogEntry = Pair<String, Float>
typealias DailyLog = Map<MealCategory, List<LogsEntity>>