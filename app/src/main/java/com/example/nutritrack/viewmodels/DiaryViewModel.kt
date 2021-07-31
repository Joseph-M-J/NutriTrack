package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.state.DiaryViewState
import com.example.nutritrack.data.model.AppDatabase
import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.util.DailyLog
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel(private val db: AppDatabase): ViewModel() {

    private val _viewState = MutableStateFlow(DiaryViewState())
    val viewState: StateFlow<DiaryViewState> = _viewState

    private var _logsJob: Job? = null
    private val _logsDAO = db.logsDAO()

    private val _fmt = SimpleDateFormat.getDateInstance(
        SimpleDateFormat.SHORT
    )

    // milliseconds
    private var _todayMillis: Long = Date().time
    private var _currentMillis = _todayMillis

    init {
        _viewState.value = _viewState.value.copy(
            displayDate = millisToDisplayDate(_todayMillis),
            isToday = true
        )

//
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
        watchLogs()
    }
    private fun watchLogs() {
        _logsJob?.cancel()
        _logsJob = viewModelScope.launch(Dispatchers.IO) {

            // The Flow returned by Room will be continuously collected!
//            _logsDAO.getAll().collect { entities ->
            _logsDAO.getByDate(_viewState.value.displayDate).collect { entities ->

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

    fun changeDate(deltaMillis: Long) {
        if (deltaMillis == 0L) {
            _todayMillis = Date().time
            _currentMillis = _todayMillis
        } else {
            _currentMillis += deltaMillis
        }

        _viewState.value = _viewState.value.copy(
            displayDate = millisToDisplayDate(_currentMillis),
            isToday = _currentMillis == _todayMillis
        )

        watchLogs()
    }

    fun selectEntity(id: Long) {
        _viewState.value = _viewState.value.copy(
            selectedId = if (_viewState.value.selectedId == id) -1 else id
        )
    }

    fun updateLog(entity: LogsEntity, add: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (add) {
                _logsDAO.save(entity)
            } else {
                _logsDAO.delete(entity)
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

    private fun millisToDisplayDate(millis: Long): String {
        return _fmt.format(millis)
//        Timber.i(date)
//        val dateSplit = date.split("/")
//        return "${dateSplit[1]}/${dateSplit[0]}/${dateSplit[2]}"
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