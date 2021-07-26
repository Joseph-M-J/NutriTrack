package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nutritrack.data.local.DiaryViewState
import com.example.nutritrack.util.DailyLog
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DiaryViewModel
//    constructor (private val database: AppDatabase)
    : ViewModel() {

    private val _viewState = MutableStateFlow(DiaryViewState())
    val viewState: StateFlow<DiaryViewState> = _viewState

//    init {
//        val log = MealCategory.values().map { it to emptyList<LogEntry>() }.toMap()
//
//        _viewState.value = _viewState.value.copy(
//            currentLog = log
//        )
//    }

    fun removeLogEntry(category: MealCategory, entry: LogEntry) {

    }

    fun addLogEntry(category: MealCategory, entry: LogEntry?) {
        if (entry != null) {
            val entries = _viewState.value.currentLog
                .getOrElse(category)
                { emptyList() }

            val log = _viewState.value.currentLog
                .map {
                    if (it.key == category) {
                        category to entries.plus(entry)
                    } else {
                        it.toPair()
                    }
                }
                .toMap()

            val subTotals = updateSubTotalKcal(log)

            _viewState.value = _viewState.value.copy(
                currentLog = log,
                subTotalKcal = subTotals,
                totalKcal = subTotals.values.sum()
            )
        }
    }

    fun updateSubTotalKcal(log: DailyLog): Map<MealCategory, Float> {
        return MealCategory
            .values()
            .map { it to getSubTotalKcal(log, it) }
            .toMap()
    }

    fun getSubTotalKcal(log: DailyLog, category: MealCategory): Float {
        return log.getOrElse(category) { emptyList() }
            .map { it.second }
            .sum()
    }

//    fun getTotalKcal(): Float {
//        return _viewState.value.currentLog.keys
//            .map { getSubTotalKcal(it) }
//            .sum()
//    }
}