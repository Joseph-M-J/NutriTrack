package com.example.nutritrack.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import com.example.nutritrack.data.model.AppDatabase
import com.example.nutritrack.data.model.FavoritesEntity
import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.data.state.FavoritesViewState
import com.example.nutritrack.util.MealCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritesViewModel(private val db: AppDatabase): ViewModel() {
    private val _viewState = MutableStateFlow(FavoritesViewState())
    val viewState: StateFlow<FavoritesViewState> = _viewState

    private var _subsetJob: Job? = null
    private var _countJob: Job? = null
    private val _foodDAO = db.foodDAO()
    private val _favoritesDAO = db.favoritesDAO()

    init {
        _countJob?.cancel()
        _countJob = viewModelScope.launch(Dispatchers.IO) {
            _favoritesDAO.getTotals().collect { entities ->
                _viewState.value = _viewState.value.copy(
                    categoryTotals = entities.map{ it.category to it.count }.toMap()
                )
            }
        }
    }

    fun deleteFavorite(category: MealCategory, entity: FoodEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = _favoritesDAO.getTotalForFood(entity.title, entity.imgRes)

            if (count == 1) {
                _foodDAO.delete(entity)
            } else {
                val favorite = FavoritesEntity(entity.title, entity.imgRes, category)
                _favoritesDAO.delete(favorite)
                updateCategory(category)
            }
        }
    }

    fun addFavorites(category: MealCategory, entities: List<FoodEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            _foodDAO.saveAll(entities)

            val favorites = entities.map {
                FavoritesEntity(
                    title = it.title,
                    imgRes = it.imgRes,
                    category = category
                )
            }

            _favoritesDAO.saveAll(favorites)

            if (category == _viewState.value.selectedCategory) {
                updateCategory(category)
            }
        }
    }

    fun selectCategory(category: MealCategory?) {
        _viewState.value = _viewState.value.copy(
            selectedCategory = category
        )
        updateCategory(category)
    }

    private fun updateCategory(category: MealCategory?) {
        _subsetJob?.cancel()

        category?.let { cat ->
            _subsetJob = viewModelScope.launch(Dispatchers.IO) {
                Timber.i("Updating catagory")
                // Computed string id to search for composite keys
                val ids = _favoritesDAO.getByCategory(cat).map { it.title + it.imgRes }

                _foodDAO.getAllById(ids).collect { entities ->
                    _viewState.value = _viewState.value.copy(
                        favorites = entities
                    )
                }
            }
        }
    }
}