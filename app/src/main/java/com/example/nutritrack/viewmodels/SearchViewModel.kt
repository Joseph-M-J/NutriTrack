package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.FoodRepository
import com.example.nutritrack.data.local.SearchViewState
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.RemoteResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel constructor(private val repo: FoodRepository) : ViewModel() {

    private val _viewState = MutableStateFlow(SearchViewState())
    val viewState: StateFlow<SearchViewState> = _viewState

    private var _previousQuery: String? = null
    private var _fetchJob: Job? = null

    private var _quantity = 1.0f
    private var _loadedEntry: LogEntry? = null

    fun selectItem(foodItem: Int) {
        _viewState.value = _viewState.value.copy(
            selectedItem =
                if (_viewState.value.selectedItem != foodItem) foodItem else -1,
            selectedUnit = 0
        )
        updateDisplayStats()
    }

    fun selectUnit(foodUnit: Int) {
        _viewState.value = _viewState.value.copy(
            selectedUnit = foodUnit
        )
        updateDisplayStats()
    }

    fun fetchFoodList(query: String?, force: Boolean = false) {
        val newQuery = query ?: _previousQuery

        if (
            newQuery != null &&
            newQuery.isNotBlank() &&
            (force || newQuery != _previousQuery)
        ) {
            _previousQuery = newQuery
            _fetchJob?.cancel()

            _fetchJob = viewModelScope.launch(Dispatchers.Default) {
                repo.fetchFoodList(newQuery, 0).collect { results ->
                    _viewState.value = _viewState.value.copy(
                        searchResults = results
                    )
                }
            }
        }
    }

    fun updateDisplayStats(quantity: Float = 1.0f) {
        if (_viewState.value.searchResults !is RemoteResource.Success) return

        val foodInfo = (
            _viewState.value.searchResults
            as RemoteResource.Success<List<FoodInfo>>
        ).data
        .getOrNull(_viewState.value.selectedItem) ?: return

        val unit = _viewState.value.selectedUnit
        _quantity = quantity

        _viewState.value = _viewState.value.copy(
            displayStats = listOf(
                foodInfo.kcal[unit],
                foodInfo.protein[unit],
                foodInfo.carbs[unit],
                foodInfo.fat[unit]
            ).map { x -> x * _quantity }
        )
    }

    fun copyLogEntry(entry: LogEntry) {
        _loadedEntry = entry
    }

    fun pasteLogEntry(): LogEntry? {
        val entry = _loadedEntry?.copy()
        _loadedEntry = null
        return entry
    }
}