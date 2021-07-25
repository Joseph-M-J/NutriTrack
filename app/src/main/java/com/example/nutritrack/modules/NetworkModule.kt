package com.example.nutritrack.modules

import com.example.nutritrack.api.NutracheckService

object NetworkModule {

    fun provideNutracheckService(): NutracheckService = NutracheckService.create()

    fun provideFakeService(): NutracheckService = object : NutracheckService {
        override suspend fun searchProducts(desc: String, page: Int): String {
            TODO("Not yet implemented")
        }

        override suspend fun getProductInfo(infoLink: String): String {
            TODO("Not yet implemented")
        }

    }
}