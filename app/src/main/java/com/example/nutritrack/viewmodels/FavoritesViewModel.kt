package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.model.AppDatabase
import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.data.model.LogsEntity
import com.example.nutritrack.data.state.FavoritesViewState
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(private val db: AppDatabase): ViewModel() {
    private val _viewState = MutableStateFlow(FavoritesViewState())
    val viewState: StateFlow<FavoritesViewState> = _viewState

    private var _searchJob: Job? = null
    private val _foodDAO = db.foodDAO()

    init {
        _searchJob?.cancel()
        _searchJob = viewModelScope.launch(Dispatchers.IO) {
            _foodDAO.getAll().collect { entities ->

                val favorites = MealCategory.values().map {
                    it to mutableListOf<FoodEntity>()
                }.toMap().toMutableMap()

                entities.forEach { favorites[it.category]!!.add(it) }

                _viewState.value = _viewState.value.copy(
                    favorites = favorites
                )
            }
        }
    }

    fun selectCategory(category: MealCategory?) {
        _viewState.value = _viewState.value.copy(
            selectedCategory = category
        )
    }
}