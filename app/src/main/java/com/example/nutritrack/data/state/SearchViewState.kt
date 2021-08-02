package com.example.nutritrack.data.state

import com.example.nutritrack.data.model.FoodEntity
import com.example.nutritrack.util.RemoteResource

data class SearchViewState(
    val loadedEntries: Int = 0,
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val searchResults: RemoteResource<List<FoodEntity>> = RemoteResource.Success(emptyList())
)
