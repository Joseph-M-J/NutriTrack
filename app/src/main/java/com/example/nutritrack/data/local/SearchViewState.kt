package com.example.nutritrack.data.local

import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.util.RemoteResource

data class SearchViewState(
    var selectedUnit: Int = 0,
    var selectedItem: Int = -1,
    var showUnitMenu: Boolean = false,
    var displayStats: List<Float> = emptyList(),
    var searchResults: RemoteResource<List<FoodInfo>> = RemoteResource.Success(emptyList())
)
