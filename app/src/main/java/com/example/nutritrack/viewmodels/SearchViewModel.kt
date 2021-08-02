package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.FoodRepository
import com.example.nutritrack.data.state.SearchViewState
import com.example.nutritrack.util.FoodPreset
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

    private var _loadedEntries = mutableListOf<FoodPreset>()

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
                        currentPage = page,
                        hasNextPage = hasNext,
                        searchResults = results
                    )
                }
            }
        }
    }

    fun copyLogEntry(entry: FoodPreset) {
        _loadedEntries.add(entry)
        _viewState.value = _viewState.value.copy(
            loadedEntries = _loadedEntries.size
        )
    }

    fun pasteLogEntry(): List<FoodPreset> {
        val entry = listOf(*_loadedEntries.toTypedArray())
        _loadedEntries.clear()
        _viewState.value = _viewState.value.copy(
            loadedEntries = 0
        )
        return entry
    }
}