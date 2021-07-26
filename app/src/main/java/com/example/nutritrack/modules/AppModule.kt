package com.example.nutritrack.modules

import android.content.Context
import com.example.nutritrack.api.NutracheckService
import com.example.nutritrack.data.model.AppDatabase

object AppModule {

    fun provideNutracheckService(): NutracheckService = NutracheckService.create()

    fun provideFakeNutracheckService(): NutracheckService = object : NutracheckService {
        override suspend fun searchProducts(desc: String, page: Int): String {
            TODO("Not yet implemented")
        }

        override suspend fun getProductInfo(productId: String, productTitle: String): String {
            TODO("Not yet implemented")
        }
    }

    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.getInstance(context)
}