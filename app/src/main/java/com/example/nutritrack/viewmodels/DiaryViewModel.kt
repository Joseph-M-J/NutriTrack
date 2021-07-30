package com.example.nutritrack.viewmodels

import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.local.DiaryViewState
import com.example.nutritrack.data.model.AppDatabase
import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.util.DailyLog
import com.example.nutritrack.util.MealCategory
import com.example.nutritrack.util.testLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel(private val db: AppDatabase): ViewModel() {

    private val _viewState = MutableStateFlow(DiaryViewState())
    val viewState: StateFlow<DiaryViewState> = _viewState

    private val logsDAO = db.logsDAO()

    private var day: Int = -1
    private var month: Int = -1
    private var year: Int = -1

    init {
        val fmt = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
        val date = fmt.format(Date())
        val dateSplit = date.split("/")
        day = dateSplit[1].toInt()
        month = dateSplit[0].toInt()
        year = dateSplit[2].toInt()

        _viewState.value = _viewState.value.copy(
            displayDate = "$day/$month/$year",
            isToday = true
        )

        /**** INIT WITH TEST DATA ****/
//        runBlocking {
//            logsDAO.insertAll(
//                testLog.flatMap { mealLog ->
//                    mealLog.value.map { (title, kcal) ->
//                        LogsEntity(
//                            title = title,
//                            category = mealLog.key,
//                            kcal = kcal,
//                            date = date
//                        )
//                    }
//                }
//            )
//        }

        viewModelScope.launch(Dispatchers.IO) {

            // The Flow returned by Room will be continuously collected!
            logsDAO.getToday(date).collect { entities ->

                val log = MealCategory.values().map {
                    it to mutableListOf<LogsEntity>()
                }.toMap().toMutableMap()

                // Build new daily log
                entities.forEach { log[it.category]!!.add(it) }

                val subTotals = getSubTotals(log)

                // Update log and totals
                _viewState.value = _viewState.value.copy(
                    currentLog = log,
                    subTotalKcal = subTotals,
                    totalKcal = subTotals.values.sum()
                )
            }
        }
    }

    fun changeDate() {

    }

    fun selectEntity(id: Long) {
        _viewState.value = _viewState.value.copy(
            selectedId = if (_viewState.value.selectedId == id) -1 else id
        )
    }

    fun updateLog(entity: LogsEntity, add: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (add) {
                logsDAO.save(entity)
            } else {
                logsDAO.delete(entity)
            }
        }
        /*val entries = _viewState.value.currentLog.getOrElse(category) { emptyList() }

        val log = _viewState.value.currentLog
            .map {
                if (it.key == category) {
                    category to if (add) {
                        entries.plus(entry)
                    } else {
                        entries.minus(entry)
                    }
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
        )*/
    }

    private fun getSubTotals(log: DailyLog): Map<MealCategory, Float> {
        return log.map {
            it.key to it.value.map{ entity -> entity.kcal }.sum()
        }.toMap()
    }

    fun toggleQuickAddMenu(category: MealCategory) {
        val show = _viewState.value.showQuickAddMenu

        _viewState.value = _viewState.value.copy(
            showQuickAddMenu = !show,
            selectedCategory = if (show) null else category
        )
    }

//    private fun getSubTotalKcal(entities: List<LogsEntity>): Float {
//        return entities.map { it.kcal }.sum()
//    }
//    fun getTotalKcal(): Float {
//        return _viewState.value.currentLog.keys
//            .map { getSubTotalKcal(it) }
//            .sum()
//    }
}