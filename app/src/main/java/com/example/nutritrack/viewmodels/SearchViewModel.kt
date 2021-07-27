package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.FoodRepository
import com.example.nutritrack.data.local.SearchViewState
import com.example.nutritrack.util.LogEntry
import com.example.nutritrack.util.RemoteResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(private val repo: FoodRepository) : ViewModel() {

    private val _viewState = MutableStateFlow(SearchViewState())
    val viewState: StateFlow<SearchViewState> = _viewState

    private var _previousQuery: String? = null
    private var _fetchJob: Job? = null

    private var _loadedEntry = mutableListOf<LogEntry>()

    fun selectItem(foodItem: Int) {
        if (_viewState.value.selectedItem != foodItem) {
            _viewState.value = _viewState.value.copy(
                selectedItem = foodItem,
                selectedUnit = 0,
                quantity = 1.0f
            )
            updateDisplayStats(1.0f)
        }
    }

    fun selectUnit(foodUnit: Int) {
        _viewState.value = _viewState.value.copy(
            selectedUnit = foodUnit,
            quantity = 1.0f
        )
        updateDisplayStats(_viewState.value.quantity)
    }

    fun toggleUnitMenu() {
        _viewState.value = _viewState.value.copy(
            showUnitMenu = !_viewState.value.showUnitMenu
        )
    }

    fun fetchFoodList(query: String?, page: Int, force: Boolean = false) {
        val newQuery = query ?: _previousQuery

        if (
            newQuery != null &&
            newQuery.isNotBlank() &&
            (force || newQuery != _previousQuery)
        ) {
            _previousQuery = newQuery
            _fetchJob?.cancel()

            _fetchJob = viewModelScope.launch(Dispatchers.Default) {
                repo.fetchFoodList(newQuery, page).collect { (results, hasNext) ->
                    _viewState.value = _viewState.value.copy(
                        selectedUnit = 0,
                        selectedItem = -1,
                        currentPage = page,
                        hasNextPage = hasNext,
                        searchResults = results
                    )
                }
            }
        }
    }

    fun updateDisplayStats(quantity: Float) {
        val searchResults = _viewState.value.searchResults

        if (searchResults !is RemoteResource.Success) return

        val foodInfo = searchResults
            .data.getOrNull(_viewState.value.selectedItem) ?: return

        val unit = _viewState.value.selectedUnit

        _viewState.value = _viewState.value.copy(
            quantity = quantity,
            displayStats = listOf(
                foodInfo.kcal[unit],
                foodInfo.protein[unit],
                foodInfo.carbs[unit],
                foodInfo.fat[unit]
            ).map { it * quantity }
        )
    }

    fun copyLogEntry(entry: LogEntry) {
        _loadedEntry.add(entry)
    }

    fun pasteLogEntry(): List<LogEntry> {
        val entry = listOf(*_loadedEntry.toTypedArray())
        _loadedEntry.clear()
        return entry
    }
}