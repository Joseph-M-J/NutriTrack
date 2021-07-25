package com.example.nutritrack.data

import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.local.fakeSearchData
import com.example.nutritrack.data.remote.FoodInfo
import com.example.nutritrack.util.FoodResource
import com.example.nutritrack.util.RemoteResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FoodRepository constructor(private val service: NutracheckService) {

    fun fetchFoodList(query: String, page: Int): Flow<FoodResource> = flow {
        emit(RemoteResource.Loading())
        delay(100)
        emit(try {
            RemoteResource.Success(fakeSearchData)
        } catch (e: Exception) {
            RemoteResource.Error("${e.cause}: ${e.message}")
        })
    }
}