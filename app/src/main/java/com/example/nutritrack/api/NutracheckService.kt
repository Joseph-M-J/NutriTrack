package com.example.nutritrack.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NutracheckService {

    @GET("CaloriesIn/Product/Search")
    suspend fun searchProducts(
        @Query("desc") desc: String,
        @Query("page") page: Int
    ): String

    @GET("CaloriesIn/Product/{id}/{title}")
    suspend fun getProductInfo(
        @Path("id") productId: String,
        @Path("title") productTitle: String
    ): String

    companion object {
        private const val BASE_URI = "https://www.nutracheck.co.uk/"
        const val IMAGE_URI = "https://d2lhwe7okuon6r.cloudfront.net/media/productimages/148/"
        const val PAGE_SIZE = 5
        const val FIRST_PAGE = 0

        fun create(): NutracheckService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URI)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(NutracheckService::class.java)
        }
    }
}