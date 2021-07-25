package com.example.nutritrack.data.local

import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.util.RemoteResource

data class SearchViewState(
    var selectedUnit: Int = 0,
    var selectedItem: Int = -1,
    var quantity: Float = 1.0f,
    var showUnitMenu: Boolean = false,
    var displayStats: List<Float> = emptyList(),
    var searchResults: RemoteResource<List<FoodInfo>> = RemoteResource.Success(emptyList()),
    var currentPage: Int = NutracheckService.FIRST_PAGE,
    var hasNextPage: Boolean = false
)
