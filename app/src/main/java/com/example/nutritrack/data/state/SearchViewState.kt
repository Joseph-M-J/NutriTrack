package com.example.nutritrack.data.state

import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.util.RemoteResource

data class SearchViewState(
    val selectedUnit: Int = 0,
    val selectedItem: Int = -1,
    val quantity: Float = 1.0f,
    val loadedEntries: Int = 0,
    val showUnitMenu: Boolean = false,
    val displayStats: List<Float> = emptyList(),
    val searchResults: RemoteResource<List<FoodEntity>> = RemoteResource.Success(emptyList()),
    val currentPage: Int = NutracheckService.FIRST_PAGE,
    val hasNextPage: Boolean = false
)
